package com.wanderwildwood.tarot.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import com.mudita.mmd.components.text.TextMMD
import com.mudita.mmd.components.top_app_bar.TopAppBarMMD
import com.wanderwildwood.tarot.R
import com.wanderwildwood.tarot.deck.Drawn
import com.wanderwildwood.tarot.deck.Spread
import kotlin.math.abs

private val SIDE = 20.dp

/**
 * A card on its own page: the picture as large as a screen holds it, then its name, and both
 * meanings, the one for how it was drawn first. [position] is its place in a reading.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardScreen(drawn: Drawn, position: String?, onBack: () -> Unit, onZoom: () -> Unit) {
    val words = rememberCardWords()
    val card = drawn.card
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            TopAppBarMMD(
                title = { TextMMD(text = words.names[card]) },
                navigationIcon = { BarButton(Icons.Back, stringResource(R.string.cd_back), onBack) },
            )
        },
    ) { padding ->
        BoxWithConstraints(Modifier.padding(padding).fillMaxSize()) {
            val tall = maxHeight - 24.dp
            val w = min(maxWidth - SIDE * 2, tall / Spread.ASPECT)
            PagedColumn(Modifier.fillMaxSize().testTag("card_page")) {
                item {
                    Box(Modifier.fillMaxWidth().padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
                        CardArt(
                            card, drawn.reversed, small = false, description = drawnName(words, drawn),
                            modifier = Modifier.size(w, w * Spread.ASPECT).clickable(onClick = onZoom).testTag("card_art"),
                        )
                    }
                }
                item {
                    Column(Modifier.padding(horizontal = SIDE).padding(bottom = 8.dp)) {
                        if (position != null) TextMMD(text = position, style = MaterialTheme.typography.labelSmall)
                        TextMMD(text = drawnName(words, drawn), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        TextMMD(text = stringResource(R.string.zoom_hint), style = MaterialTheme.typography.labelSmall)
                    }
                    Rule()
                }
                val order = if (drawn.reversed) listOf(true, false) else listOf(false, true)
                for (rev in order) {
                    item(key = "m$rev") {
                        Column(Modifier.padding(horizontal = SIDE, vertical = 12.dp)) {
                            val label = when {
                                position != null && rev == drawn.reversed ->
                                    stringResource(if (rev) R.string.drawn_reversed else R.string.drawn_upright)
                                else -> stringResource(if (rev) R.string.reversed else R.string.upright)
                            }
                            TextMMD(text = label, style = MaterialTheme.typography.labelSmall)
                            TextMMD(
                                text = if (rev) words.revKeys[card] else words.upKeys[card],
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                            )
                            Spacer(Modifier.height(6.dp))
                            TextMMD(text = if (rev) words.rev[card] else words.up[card], style = MaterialTheme.typography.bodySmall)
                        }
                        Rule()
                    }
                }
            }
        }
    }
}

/** How far a card can be zoomed, in steps: one redraw for each, nothing in between. */
private val STEPS = floatArrayOf(1f, 1.5f, 2f, 3f, 4f)

/**
 * The card alone, filling the screen, to be looked into. A double tap zooms a step in where it
 * lands (and back out from the last); a pinch zooms to the nearest step when the fingers lift;
 * a drag moves the zoomed card, again when the finger lifts. Nothing follows the finger,
 * because on this panel every frame of following is a smeared redraw.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZoomScreen(drawn: Drawn, onBack: () -> Unit) {
    val words = rememberCardWords()
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var box by remember { mutableStateOf(IntSize.Zero) }

    fun fitted(): Pair<Float, Float> {
        val w = minOf(box.width.toFloat(), box.height / Spread.ASPECT)
        return w to w * Spread.ASPECT
    }

    fun clamp(o: Offset, s: Float): Offset {
        val (w, h) = fitted()
        val mx = maxOf(0f, (w * s - box.width) / 2)
        val my = maxOf(0f, (h * s - box.height) / 2)
        return Offset(o.x.coerceIn(-mx, mx), o.y.coerceIn(-my, my))
    }

    /** Zoom to [s], keeping the point under [at] (or the middle) where it is. */
    fun zoomTo(s: Float, at: Offset?) {
        val c = Offset(box.width / 2f, box.height / 2f)
        val p = (at ?: c) - c
        offset = clamp(p - (p - offset) * (s / scale), s)
        scale = s
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            TopAppBarMMD(
                title = { TextMMD(text = drawnName(words, drawn)) },
                navigationIcon = { BarButton(Icons.Back, stringResource(R.string.cd_back), onBack) },
            )
        },
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            Box(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clipToBounds()
                    .testTag("zoom")
                    .onSizeChanged { box = it }
                    .pointerInput(Unit) {
                        detectTapGestures(onDoubleTap = { p ->
                            val next = STEPS.firstOrNull { it > scale + 0.01f }
                            if (next == null) zoomTo(1f, null) else zoomTo(next, p)
                        })
                    }
                    .pointerInput(Unit) {
                        awaitEachGesture {
                            awaitFirstDown(requireUnconsumed = false)
                            var zoom = 1f
                            var pan = Offset.Zero
                            var moved = false
                            var centroid: Offset? = null
                            do {
                                val event = awaitPointerEvent()
                                val pressed = event.changes.filter { it.pressed }
                                if (pressed.size >= 2) {
                                    zoom *= event.calculateZoom()
                                    centroid = pressed.fold(Offset.Zero) { a, c -> a + c.position } / pressed.size.toFloat()
                                }
                                pan += event.calculatePan()
                                if (abs(zoom - 1f) > 0.05f || pan.getDistance() > viewConfiguration.touchSlop) moved = true
                                if (moved) event.changes.forEach { if (it.positionChanged()) it.consume() }
                            } while (event.changes.any { it.pressed })
                            if (!moved) return@awaitEachGesture
                            if (abs(zoom - 1f) > 0.05f) {
                                val target = scale * zoom
                                zoomTo(STEPS.minByOrNull { abs(it - target) } ?: 1f, centroid)
                            } else if (scale > 1f) {
                                offset = clamp(offset + pan, scale)
                            }
                        }
                    },
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    bitmap = ImageBitmap.imageResource(artId(drawn.card, small = false)),
                    contentDescription = drawnName(words, drawn),
                    contentScale = ContentScale.Fit,
                    filterQuality = FilterQuality.High,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                            translationX = offset.x
                            translationY = offset.y
                            rotationZ = if (drawn.reversed) 180f else 0f
                        },
                )
            }
            TextMMD(
                text = stringResource(R.string.zoom_how),
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(horizontal = SIDE, vertical = 6.dp),
            )
        }
    }
}
