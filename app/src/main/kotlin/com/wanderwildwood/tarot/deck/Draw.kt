package com.wanderwildwood.tarot.deck

import java.security.SecureRandom
import java.util.Random

object Draw {
    private val secure = SecureRandom()

    /**
     * [count] different cards off a deck shuffled with [random] (Fisher-Yates), each turned
     * upside down at even odds when [reversals] is on.
     */
    fun cards(count: Int, reversals: Boolean, random: Random = secure): List<Drawn> {
        require(count in 1..Deck.SIZE)
        val deck = IntArray(Deck.SIZE) { it }
        for (i in deck.size - 1 downTo 1) {
            val j = random.nextInt(i + 1)
            val t = deck[i]; deck[i] = deck[j]; deck[j] = t
        }
        return (0 until count).map { Drawn(deck[it], reversals && random.nextBoolean()) }
    }

    /**
     * The card for a day: the same all day, on any phone, for anyone, and a different one
     * tomorrow. [epochDay] is the local date as days since 1970-01-01. Whether it lies upside
     * down is fixed by the day too, and only shown when [reversals] is on.
     */
    fun daily(epochDay: Long, reversals: Boolean): Drawn {
        val h = mix(epochDay xor DAILY_SALT)
        val card = ((h ushr 1) % Deck.SIZE).toInt()
        return Drawn(card, reversals && (h and 1L) == 1L)
    }

    /** SplitMix64's finaliser: neighbouring days give unrelated numbers. */
    internal fun mix(x: Long): Long {
        var z = x + -0x61c8864680b583ebL
        z = (z xor (z ushr 30)) * -0x40a7b892e31b1a47L
        z = (z xor (z ushr 27)) * -0x6b2fb644ecceee15L
        return z xor (z ushr 31)
    }

    private const val DAILY_SALT = 0x7a2070a1L
}
