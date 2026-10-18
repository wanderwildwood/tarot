package com.wanderwildwood.tarot.deck

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SpreadTest {

    private data class Box(val l: Float, val t: Float, val r: Float, val b: Float) {
        fun overlaps(o: Box) = l < o.r - EPS && o.l < r - EPS && t < o.b - EPS && o.t < b - EPS
    }

    private fun box(s: Slot): Box {
        val a = Spread.ASPECT
        if (!s.sideways) return Box(s.x, s.y, s.x + 1, s.y + a)
        val cx = s.x + 0.5f
        val cy = s.y + a / 2
        return Box(cx - a / 2, cy - 0.5f, cx + a / 2, cy + 0.5f)
    }

    @Test
    fun sizes() {
        assertEquals(1, Spread.DAILY.size)
        assertEquals(1, Spread.ONE.size)
        assertEquals(3, Spread.THREE.size)
        assertEquals(10, Spread.CELTIC.size)
    }

    @Test
    fun keysAreUniqueAndRoundTrip() {
        assertEquals(Spread.entries.size, Spread.entries.map { it.key }.toSet().size)
        for (s in Spread.entries) assertEquals(s, Spread.of(s.key))
    }

    @Test
    fun pastPresentFutureRunLeftToRightOnOneRow() {
        val s = Spread.THREE.slots
        assertTrue(s.all { it.y == s[0].y && !it.sideways })
        assertTrue(s[0].x < s[1].x && s[1].x < s[2].x)
    }

    @Test
    fun noCardCoversAnotherExceptTheCrossing() {
        for (spread in Spread.entries) {
            val boxes = spread.slots.map(::box)
            for (i in boxes.indices) for (j in i + 1 until boxes.size) {
                val crossing = spread == Spread.CELTIC && i == 0 && j == 1
                assertEquals("${spread.key} $i/$j", crossing, boxes[i].overlaps(boxes[j]))
            }
        }
    }

    @Test
    fun everyCardIsInsideTheSpread() {
        for (spread in Spread.entries) for (b in spread.slots.map(::box)) {
            assertTrue(spread.key, b.l >= -EPS && b.t >= -EPS && b.r <= spread.width + EPS && b.b <= spread.height + EPS)
        }
    }

    @Test
    fun theCelticCrossIsLaidAsWaiteLaysIt() {
        val s = Spread.CELTIC.slots
        val heart = s[0]
        // 2 lies across 1, centred on it.
        assertEquals(heart.x, s[1].x)
        assertEquals(heart.y, s[1].y)
        assertTrue(s[1].sideways)
        assertFalse(s.filterIndexed { i, _ -> i != 1 }.any { it.sideways })
        // 3 above and 4 below, in 1's column; 5 to its left and 6 to its right, on its row.
        assertEquals(heart.x, s[2].x); assertTrue(s[2].y < heart.y)
        assertEquals(heart.x, s[3].x); assertTrue(s[3].y > heart.y)
        assertEquals(heart.y, s[4].y); assertTrue(s[4].x < heart.x)
        assertEquals(heart.y, s[5].y); assertTrue(s[5].x > heart.x)
        // The staff: one column right of the cross, 7 at the foot rising to 10 at the top.
        val staff = s.subList(6, 10)
        assertTrue(staff.all { it.x == staff[0].x && it.x > s[5].x + 1 })
        assertTrue(staff.zipWithNext().all { (a, b) -> b.y < a.y })
        assertEquals(0f, staff.last().y)
        // The cross sits in the middle of the staff's height.
        val crossMid = (s[2].y + s[3].y + Spread.ASPECT) / 2
        assertEquals(Spread.CELTIC.height / 2, crossMid, EPS)
    }

    private companion object {
        const val EPS = 1e-4f
    }
}
