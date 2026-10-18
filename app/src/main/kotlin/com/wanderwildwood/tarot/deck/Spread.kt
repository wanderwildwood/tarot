package com.wanderwildwood.tarot.deck

/**
 * Where a card lies in a spread, in card widths: [x], [y] are its top-left corner, a card is
 * 1 wide and [Spread.ASPECT] tall, and a [sideways] card is turned a quarter about its own
 * centre (the card that crosses the first in the Celtic Cross).
 */
data class Slot(val x: Float, val y: Float, val sideways: Boolean = false)

/**
 * The spreads. [key] is what the journal stores, so it never changes. [size] is how many
 * cards; the positions' names live in strings, in the same order as [slots].
 */
enum class Spread(val key: String, val slots: List<Slot>) {
    /** The card of the day, kept in the journal. One card, no position name of its own. */
    DAILY("daily", listOf(Slot(0f, 0f))),

    ONE("one", listOf(Slot(0f, 0f))),

    /** Past, present, future, left to right. */
    THREE("three", listOf(Slot(0f, 0f), Slot(1 + GAP, 0f), Slot(2 + 2 * GAP, 0f))),

    /**
     * Waite's "ancient Celtic method" (The Pictorial Key, 1910): the cross of six on the left,
     * the staff of four up the right. 1 in the middle and 2 across it; 3 above, 4 below;
     * 5 behind (left) and 6 before (right); then 7 at the foot of the staff up to 10 at its top.
     */
    CELTIC("celtic", celtic());

    val size get() = slots.size

    /** The spread's extent, in card widths. */
    val width get() = slots.maxOf { it.x + 1f }
    val height get() = slots.maxOf { it.y + ASPECT }

    companion object {
        /** Height over width of a card in this deck, as the art is cut. */
        const val ASPECT = 1.735f

        fun of(key: String) = entries.firstOrNull { it.key == key }
    }
}

private const val GAP = 0.1f
private const val STAFF_GAP = 0.35f

/** Room either side of the middle column for the crossing card's ends, which stick out. */
private const val CROSS_GAP = (Spread.ASPECT - 1f) / 2 + 0.1f

private fun celtic(): List<Slot> {
    val a = Spread.ASPECT
    val staff = 4 * a + 3 * GAP
    val cross = 3 * a + 2 * GAP
    val top = (staff - cross) / 2
    val mid = 1 + CROSS_GAP
    val row = a + GAP
    val staffX = 3 + 2 * CROSS_GAP + STAFF_GAP
    return listOf(
        Slot(mid, top + row),                 // 1 the heart of it
        Slot(mid, top + row, sideways = true), // 2 what crosses it
        Slot(mid, top),                       // 3 above
        Slot(mid, top + 2 * row),             // 4 below
        Slot(0f, top + row),                  // 5 behind
        Slot(2 + 2 * CROSS_GAP, top + row),   // 6 before
        Slot(staffX, 3 * row),                // 7 at the foot of the staff
        Slot(staffX, 2 * row),
        Slot(staffX, row),
        Slot(staffX, 0f),                     // 10 at its top
    )
}
