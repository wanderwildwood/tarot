package com.wanderwildwood.tarot.deck

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.util.Random

class DrawTest {

    private val day = LocalDate.of(2026, 10, 17).toEpochDay()

    @Test
    fun theDailyCardIsTheSameAllDay() {
        // Asked again and again, as the app does every time Today is drawn.
        val first = Draw.daily(day, reversals = true)
        repeat(1000) { assertEquals(first, Draw.daily(day, reversals = true)) }
    }

    @Test
    fun theDailyCardIsFixedForADate() {
        // Pinned, so a change to the mixing that would change everyone's card for a date is noticed.
        val cards = (0L until 7).map { Draw.daily(day + it, reversals = true) }
        assertEquals(cards, (0L until 7).map { Draw.daily(day + it, reversals = true) })
        assertEquals(PINNED, cards.joinToString(" ") { Deck.id(it.card) + if (it.reversed) "r" else "" })
    }

    @Test
    fun neighbouringDaysDiffer() {
        val run = (0L until 365).map { Draw.daily(day + it, reversals = false).card }
        val sameAsYesterday = run.zipWithNext().count { (a, b) -> a == b }
        // Chance alone would repeat about 365/78 ≈ 4.7 times a year.
        assertTrue("repeats: $sameAsYesterday", sameAsYesterday <= 12)
        assertNotEquals(run[0], run[1])
    }

    @Test
    fun everyCardComesUpOverTheYears() {
        val seen = (0L until 3000).map { Draw.daily(day + it, reversals = false).card }.toSet()
        assertEquals(Deck.SIZE, seen.size)
    }

    @Test
    fun reversalsOffMeansUpright() {
        repeat(500) { assertFalse(Draw.daily(day + it, reversals = false).reversed) }
        repeat(50) { assertTrue(Draw.cards(10, reversals = false).none { it.reversed }) }
    }

    @Test
    fun reversalsOnTurnSomeOver() {
        val days = (0L until 500).map { Draw.daily(day + it, reversals = true) }
        val reversed = days.count { it.reversed }
        assertTrue("reversed $reversed of 500", reversed in 200..300)
        // Turning reversals on or off never changes which card it is.
        days.forEachIndexed { i, d -> assertEquals(d.card, Draw.daily(day + i, reversals = false).card) }
    }

    @Test
    fun aSpreadDrawsDifferentCards() {
        repeat(200) {
            val ten = Draw.cards(10, reversals = true)
            assertEquals(10, ten.size)
            assertEquals(10, ten.map { it.card }.toSet().size)
        }
        assertEquals(Deck.SIZE, Draw.cards(Deck.SIZE, reversals = false).map { it.card }.toSet().size)
    }

    @Test
    fun theShuffleIsEven() {
        // The first card of a shuffle, 78,000 times: each card about 1,000 times.
        val random = Random(42)
        val counts = IntArray(Deck.SIZE)
        repeat(78_000) { counts[Draw.cards(1, false, random)[0].card]++ }
        assertTrue(counts.joinToString(), counts.all { it in 850..1150 })
    }

    companion object {
        private const val PINNED = "swords11r pentacles09 wands12 cups14 m07 pentacles03r swords04"
    }
}
