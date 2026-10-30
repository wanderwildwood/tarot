package com.wanderwildwood.tarot.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import com.mudita.mmd.components.text.TextMMD
import com.wanderwildwood.tarot.R
import com.wanderwildwood.tarot.deck.Deck
import com.wanderwildwood.tarot.deck.Draw
import com.wanderwildwood.tarot.deck.Drawn
import com.wanderwildwood.tarot.deck.Entry
import com.wanderwildwood.tarot.deck.Spread
import java.time.LocalDate

private val SIDE = 20.dp

/** The card for today: the same all day, and its meaning as it lies. */
@Composable
fun TodayTab(state: AppState, go: (Screen) -> Unit) {
    val words = rememberCardWords()
    val today = LocalDate.now().toEpochDay()
    val drawn = Draw.daily(today, state.reversals)
    val kept = state.entries.firstOrNull { it.spread == Spread.DAILY && it.epochDay == today }
    val position = stringResource(R.string.pos_daily)

    PagedColumn(Modifier.fillMaxSize().testTag("today")) {
        // The first page is the card itself, as large as the room under the tabs allows; what it
        // means comes on the next page.
        item {
            Column(Modifier.fillParentMaxHeight().fillMaxWidth()) {
                TextMMD(
                    text = longDate(today),
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(horizontal = SIDE, vertical = 10.dp),
                )
                BoxWithConstraints(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    val w = min(maxWidth - SIDE * 2, (maxHeight - 8.dp) / Spread.ASPECT)
                    CardArt(
                        drawn.card, drawn.reversed, small = false,
                        description = drawnName(words, drawn),
                        modifier = Modifier
                            .size(width = w, height = w * Spread.ASPECT)
                            .clickable { go(Screen.Card(drawn, position)) }
                            .testTag("today_card"),
                    )
                }
                TextMMD(
                    text = drawnName(words, drawn),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = SIDE, vertical = 8.dp),
                )
            }
        }
        item { Meaning(words, drawn, Modifier.padding(horizontal = SIDE).padding(top = 12.dp)) }
        item {
            Column(Modifier.padding(horizontal = SIDE, vertical = 12.dp)) {
                if (kept == null) {
                    HouseButton(stringResource(R.string.today_keep), modifier = WideRow, onClick = {
                        state.put(Entry(System.currentTimeMillis(), today, Spread.DAILY, listOf(drawn)))
                    })
                } else {
                    HouseButton(stringResource(R.string.today_kept), onClick = { go(Screen.Reading(kept.id)) }, modifier = WideRow)
                }
            }
        }
    }
}

/** A drawn card's name, its keywords and the meaning that applies as it lies. */
@Composable
fun Meaning(words: CardWords, d: Drawn, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth()) {
        TextMMD(text = drawnName(words, d), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        TextMMD(
            text = if (d.reversed) words.revKeys[d.card] else words.upKeys[d.card],
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(6.dp))
        TextMMD(
            text = if (d.reversed) words.rev[d.card] else words.up[d.card],
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

/** The three spreads. Choosing one draws it, and the reading goes in the journal. */
@Composable
fun SpreadTab(state: AppState, go: (Screen) -> Unit) {
    PagedColumn(Modifier.fillMaxSize()) {
        item {
            TextMMD(
                text = stringResource(R.string.spread_choose),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = SIDE, vertical = 14.dp),
            )
        }
        for (s in listOf(Spread.ONE, Spread.THREE, Spread.CELTIC)) {
            item(key = s.key) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .clickable { go(Screen.Reading(state.draw(s).id)) }
                        .testTag("spread_${s.key}")
                        .padding(horizontal = SIDE, vertical = 16.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            TextMMD(text = spreadName(s), style = MaterialTheme.typography.titleMedium)
                            TextMMD(
                                text = pluralStringResource(R.plurals.cards_count, s.size, s.size.toString()),
                                style = MaterialTheme.typography.labelSmall,
                            )
                        }
                        Chevron()
                    }
                }
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

/** Every reading, newest first. */
@Composable
fun JournalTab(state: AppState, go: (Screen) -> Unit, onTab: (Int) -> Unit) {
    val words = rememberCardWords()
    val entries = state.entries
    if (entries.isEmpty()) {
        Column(Modifier.padding(horizontal = SIDE, vertical = 16.dp).testTag("journal_empty")) {
            TextMMD(text = stringResource(R.string.journal_empty), style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(12.dp))
            HouseButton(stringResource(R.string.journal_empty_today), onClick = { onTab(0) }, modifier = WideRow)
            HouseButton(stringResource(R.string.journal_empty_spread), onClick = { onTab(1) }, modifier = WideRow)
        }
        return
    }
    val list = rememberLazyListState()
    run {
        PagedColumn(state = list, modifier = Modifier.fillMaxSize().testTag("journal")) {
            for (e in entries) {
                item(key = e.id) {
                    val cards = e.cards.map { d ->
                        if (d.reversed) stringResource(R.string.reversed_short, words.names[d.card]) else words.names[d.card]
                    }.joinToString(", ")
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable { go(Screen.Reading(e.id)) }
                            .padding(horizontal = SIDE, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        TextMMD(
                            text = stringResource(R.string.journal_row, shortDate(e.epochDay), spreadName(e.spread)),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                        )
                        TextMMD(text = cards, style = MaterialTheme.typography.labelSmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
                        if (e.note.isNotBlank()) {
                            TextMMD(text = e.note, style = MaterialTheme.typography.labelSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }
                    Chevron()
                    }
                    Spacer(Modifier.height(12.dp))
                }
            }
        }
    }
}

/** All 78, in the order of the deck, each opening its page. */
@Composable
fun CardsTab(go: (Screen) -> Unit) {
    val words = rememberCardWords()
    val list = rememberLazyListState()
    run {
        PagedColumn(state = list, modifier = Modifier.fillMaxSize().testTag("cards")) {
            for (card in 0 until Deck.SIZE) {
                item(key = card) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable { go(Screen.Card(Drawn(card, false), null)) }
                            .padding(horizontal = SIDE, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(Modifier.weight(1f)) {
                            TextMMD(text = words.names[card], style = MaterialTheme.typography.bodyMedium)
                            TextMMD(text = words.upKeys[card], style = MaterialTheme.typography.labelSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                        Chevron()
                    }
                    Spacer(Modifier.height(12.dp))
                }
            }
        }
    }
}
