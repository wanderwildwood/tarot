package com.wanderwildwood.tarot

import android.content.Context
import com.wanderwildwood.tarot.deck.Entry

/**
 * What the app keeps, all of it on the phone: the reversed-cards setting in a preferences file, and the
 * journal as one JSON file in the app's private storage (see [JournalFile]).
 */
class Store(context: Context) {
    private val prefs = context.getSharedPreferences("tarot", Context.MODE_PRIVATE)
    private val journal = JournalFile(context.filesDir)

    var reversals: Boolean
        get() = prefs.getBoolean("reversals", true)
        set(v) { prefs.edit().putBoolean("reversals", v).apply() }

    /** A journal that could not be read was moved aside and a new one begun. */
    val setAside get() = journal.setAside

    /** A journal that could not be read is still in its place, and nothing will be saved over it. */
    val stuck get() = journal.stuck

    /** Newest first. */
    fun entries(): List<Entry> = journal.entries()

    fun entry(id: Long) = journal.entry(id)

    /** Each change returns false when it could not be saved; the journal is then as it was. */
    fun put(e: Entry): Boolean = journal.put(e)

    fun delete(id: Long): Boolean = journal.delete(id)

    /** Adds the restored readings not here yet; returns how many. Throws IOException if not saved. */
    fun merge(restored: List<Entry>): Int = journal.merge(restored)
}
