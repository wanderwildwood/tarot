package com.wanderwildwood.tarot.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.unit.dp
import com.wanderwildwood.tarot.deck.Deck

/**
 * A card's picture: Smith's drawing in sixteen greys. [small] takes the 240-pixel copy made
 * for spreads; the full one is the scan's own size, for the card's page and the zoom. A
 * reversed card is drawn upside down, as it lay.
 */
@Composable
fun CardArt(card: Int, reversed: Boolean, small: Boolean, modifier: Modifier = Modifier, description: String? = null) {
    val id = artId(card, small)
    Box(modifier.border(1.dp, Color.Black)) {
        Image(
            bitmap = ImageBitmap.imageResource(id),
            contentDescription = description,
            contentScale = ContentScale.FillBounds,
            // Smith's hatching shrunk with a plain bilinear filter shimmers into moire.
            filterQuality = FilterQuality.High,
            modifier = Modifier.matchParentSize().let { if (reversed) it.rotate(180f) else it },
        )
    }
}

/**
 * The drawable for a card. Looked up by name so the 156 of them need no hand-kept table;
 * res/raw/keep.xml stops resource shrinking from dropping what only this names.
 */
@SuppressLint("DiscouragedApi")
@Composable
fun artId(card: Int, small: Boolean): Int {
    val context = LocalContext.current
    return remember(card, small) {
        val name = (if (small) "thumb_" else "card_") + Deck.id(card)
        context.resources.getIdentifier(name, "drawable", context.packageName)
    }
}
