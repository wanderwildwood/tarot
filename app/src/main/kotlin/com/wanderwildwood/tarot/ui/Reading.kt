package com.wanderwildwood.tarot.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import com.mudita.mmd.components.text.TextMMD
import com.mudita.mmd.components.text_field.TextFieldMMD
import com.mudita.mmd.components.top_app_bar.TopAppBarMMD
import com.wanderwildwood.tarot.R
import com.wanderwildwood.tarot.deck.Entry
import com.wanderwildwood.tarot.deck.Spread

private val SIDE = 20.dp

/**
 * A reading: the cards laid out as the spread lays them, then each place named with its card
 * and meaning, the note, and the way to draw again or let the reading go.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadingScreen(state: AppState, id: Long, onBack: () -> Unit, go: (Screen) -> Unit, replace: (Screen) -> Unit) {
    val entry = state.entries.firstOrNull { it.id == id }
    if (entry == null) {
        // Deleted from under this screen: leave it, once, after this frame.
        LaunchedEffect(id) { onBack() }
        return
    }
    val words = rememberCardWords()
    var editing by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            TopAppBarMMD(
                title = { TextMMD(text = spreadName(entry.spread)) },
                navigationIcon = { BarButton(Icons.Back, stringResource(R.string.cd_back), onBack) },
            )
        },
    ) { padding ->
        PagedColumn(Modifier.padding(padding).fillMaxSize().testTag("reading")) {
            item {
                TextMMD(
                    text = longDate(entry.epochDay),
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(horizontal = SIDE, vertical = 10.dp),
                )
            }
            item {
                val names = entry.cards.indices.map { positionName(entry.spread, it) }
                Layout(entry, Modifier.padding(horizontal = SIDE)) { i ->
                    go(Screen.Card(entry.cards[i], names[i]))
                }
            }
            entry.cards.forEachIndexed { i, d ->
                item(key = "pos$i") {
                    val position = positionName(entry.spread, i)
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable { go(Screen.Card(d, position)) }
                            .padding(horizontal = SIDE, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(Modifier.weight(1f)) {
                            TextMMD(
                                text = if (entry.size > 1) stringResource(R.string.position_numbered, (i + 1).toString(), position) else position,
                                style = MaterialTheme.typography.labelSmall,
                            )
                            Meaning(words, d)
                        }
                        Chevron()
                    }
                    Rule()
                }
            }
            item(key = "note") {
                Column(Modifier.padding(horizontal = SIDE)) {
                    if (entry.note.isNotBlank()) {
                        Heading(stringResource(R.string.note))
                        TextMMD(text = entry.note, style = MaterialTheme.typography.bodyMedium)
                    }
                    Spacer(Modifier.height(8.dp))
                    HouseButton(stringResource(if (entry.note.isBlank()) R.string.note_add else R.string.note_edit), onClick = { editing = true }, modifier = WideRow)
                    if (entry.spread != Spread.DAILY) {
                        HouseButton(stringResource(R.string.draw_again), modifier = WideRow, onClick = {
                            replace(Screen.Reading(state.draw(entry.spread).id))
                        })
                    }
                    ArmedRow(stringResource(R.string.delete_reading), stringResource(R.string.delete_reading_armed)) {
                        state.delete(entry.id)
                        onBack()
                    }
                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    }

    if (editing) {
        NoteDialog(entry.note, onDismiss = { editing = false }) { state.put(entry.copy(note = it.trim())) }
    }
}

private val Entry.size get() = cards.size

/** The cards where the spread puts them, as large as the width and a screen's height allow. */
@Composable
private fun Layout(entry: Entry, modifier: Modifier, onCard: (Int) -> Unit) {
    val spread = entry.spread
    val words = rememberCardWords()
    BoxWithConstraints(modifier.fillMaxWidth()) {
        // One card is shown large; a spread fits the width, and a Celtic Cross a screenful.
        val byWidth = maxWidth / spread.width
        val cap: Dp = when (spread) {
            Spread.DAILY, Spread.ONE -> 168.dp
            Spread.THREE -> 112.dp
            Spread.CELTIC -> 440.dp / spread.height
        }
        val unit = min(byWidth, cap)
        val w = unit * spread.width
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
            Box(Modifier.size(w, unit * spread.height)) {
                spread.slots.forEachIndexed { i, slot ->
                    val d = entry.cards[i]
                    val cw = unit
                    val ch = unit * Spread.ASPECT
                    val small = spread != Spread.ONE && spread != Spread.DAILY
                    if (!slot.sideways) {
                        CardArt(
                            d.card, d.reversed, small, description = drawnName(words, d),
                            modifier = Modifier.offset(unit * slot.x, unit * slot.y).size(cw, ch)
                                .clickable { onCard(i) }.testTag("slot$i"),
                        )
                    } else {
                        // Turned a quarter about its own centre, over the card it crosses. The
                        // box that takes the press is the turned one, so the ends of the card
                        // beneath still answer to theirs.
                        Box(
                            Modifier
                                .offset(unit * slot.x + (cw - ch) / 2, unit * slot.y + (ch - cw) / 2)
                                .size(ch, cw)
                                .clickable { onCard(i) }
                                .testTag("slot$i"),
                            contentAlignment = Alignment.Center,
                        ) {
                            CardArt(
                                d.card, d.reversed, small, description = drawnName(words, d),
                                modifier = Modifier.requiredSize(cw, ch).rotate(90f),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NoteDialog(start: String, onDismiss: () -> Unit, onSave: (String) -> Unit) {
    var text by remember { mutableStateOf(start) }
    EInkDialog(onDismiss = onDismiss) {
        TextMMD(text = stringResource(R.string.note), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(8.dp))
        TextFieldMMD(
            value = text,
            onValueChange = { text = it },
            placeholder = { TextMMD(text = stringResource(R.string.note_hint), style = MaterialTheme.typography.bodyMedium) },
            minLines = 3,
            maxLines = 6,
            modifier = Modifier.fillMaxWidth().testTag("note_field"),
        )
        Spacer(Modifier.height(12.dp))
        HouseButton(stringResource(R.string.save), on = true, modifier = WideRow, onClick = {
            onSave(text)
            onDismiss()
        })
        HouseButton(stringResource(R.string.cancel), onClick = onDismiss, modifier = WideRow)
    }
}
