package com.wanderwildwood.tarot.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.wanderwildwood.tarot.R
import com.wanderwildwood.tarot.deck.Drawn
import com.wanderwildwood.tarot.deck.Spread
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

/** The words for the 78 cards, from the card_* arrays in deck order. */
class CardWords(
    val names: Array<String>,
    val upKeys: Array<String>,
    val up: Array<String>,
    val revKeys: Array<String>,
    val rev: Array<String>,
)

@Composable
fun rememberCardWords(): CardWords {
    val res = LocalContext.current.resources
    return remember(res) {
        CardWords(
            res.getStringArray(R.array.card_names),
            res.getStringArray(R.array.card_up_keys),
            res.getStringArray(R.array.card_up),
            res.getStringArray(R.array.card_rev_keys),
            res.getStringArray(R.array.card_rev),
        )
    }
}

/** "The Moon" or "The Moon, reversed". */
@Composable
fun drawnName(words: CardWords, d: Drawn): String =
    if (d.reversed) stringResource(R.string.card_reversed_name, words.names[d.card]) else words.names[d.card]

@Composable
fun spreadName(s: Spread): String = stringResource(
    when (s) {
        Spread.DAILY -> R.string.spread_daily
        Spread.ONE -> R.string.spread_one
        Spread.THREE -> R.string.spread_three
        Spread.CELTIC -> R.string.spread_celtic
    },
)

private val THREE = intArrayOf(R.string.pos_three_1, R.string.pos_three_2, R.string.pos_three_3)
private val CELTIC = intArrayOf(
    R.string.pos_celtic_1, R.string.pos_celtic_2, R.string.pos_celtic_3, R.string.pos_celtic_4,
    R.string.pos_celtic_5, R.string.pos_celtic_6, R.string.pos_celtic_7, R.string.pos_celtic_8,
    R.string.pos_celtic_9, R.string.pos_celtic_10,
)

@Composable
fun positionName(s: Spread, i: Int): String = stringResource(
    when (s) {
        Spread.DAILY -> R.string.pos_daily
        Spread.ONE -> R.string.pos_one
        Spread.THREE -> THREE[i]
        Spread.CELTIC -> CELTIC[i]
    },
)

fun longDate(epochDay: Long): String =
    LocalDate.ofEpochDay(epochDay).format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL))

fun shortDate(epochDay: Long): String =
    LocalDate.ofEpochDay(epochDay).format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
