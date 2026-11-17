package com.wanderwildwood.tarot.ui

import java.io.ByteArrayInputStream
import org.junit.Assert.assertEquals
import org.junit.Test

class ReadAtMostTest {
    @Test
    fun stopsAtTheLimit() {
        val big = ByteArray(200_000) { 1 }
        assertEquals(150_001, readAtMost(ByteArrayInputStream(big), 150_001).size)
        assertEquals(10, readAtMost(ByteArrayInputStream(ByteArray(10)), 150_001).size)
    }
}
