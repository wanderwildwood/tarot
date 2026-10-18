package com.wanderwildwood.tarot.deck

/**
 * The 78 cards in the order of the deck: the 22 trumps, then Wands, Cups, Swords and
 * Pentacles, ace to king. A card is its place in that order, 0 to 77; its id names its art
 * (card_m00, thumb_swords10) and its words (the card_* string arrays, in the same order).
 */
object Deck {
    const val SIZE = 78
    const val MAJORS = 22

    val SUITS = listOf("wands", "cups", "swords", "pentacles")

    val ids: List<String> = (0 until MAJORS).map { "m%02d".format(it) } +
        SUITS.flatMap { suit -> (1..14).map { "$suit%02d".format(it) } }

    fun id(card: Int): String = ids[card]

    fun isMajor(card: Int) = card < MAJORS

    /** 0..3 for Wands, Cups, Swords, Pentacles; -1 for a trump. */
    fun suit(card: Int) = if (isMajor(card)) -1 else (card - MAJORS) / 14
}

/** A card as it came out of the deck: which one, and whether it lay upside down. */
data class Drawn(val card: Int, val reversed: Boolean) {
    init {
        require(card in 0 until Deck.SIZE) { "no card $card" }
    }
}
