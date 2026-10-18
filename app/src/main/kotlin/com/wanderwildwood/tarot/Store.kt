package com.wanderwildwood.tarot

import android.content.Context
import com.wanderwildwood.tarot.deck.Entry
import com.wanderwildwood.tarot.deck.JournalCodec
import java.io.File

/**
 * What the app keeps, all of it on the phone: the one setting in a preferences file, and the
 * journal as one JSON file in the app's private storage, rewritten whole on each change (a
 * temporary file renamed over it, so a write cut short cannot leave half a journal).
 */
class Store(context: Context) {
    private val prefs = context.getSharedPreferences("tarot", Context.MODE_PRIVATE)
    private val file = File(context.filesDir, "journal.json")

    var reversals: Boolean
        get() = prefs.getBoolean("reversals", true)
        set(v) { prefs.edit().putBoolean("reversals", v).apply() }

    private var cache: List<Entry>? = null

    /** Newest first. */
    fun entries(): List<Entry> =
        cache ?: (if (file.isFile) JournalCodec.decode(file.readText()) else emptyList())
            .sortedByDescending { it.id }
            .also { cache = it }

    fun entry(id: Long) = entries().firstOrNull { it.id == id }

    fun put(e: Entry) = write(listOf(e) + entries().filter { it.id != e.id })

    fun delete(id: Long) = write(entries().filter { it.id != id })

    private fun write(list: List<Entry>) {
        val sorted = list.sortedByDescending { it.id }
        val tmp = File(file.parentFile, "journal.json.tmp")
        tmp.writeText(JournalCodec.encode(sorted))
        if (!tmp.renameTo(file)) error("could not save the journal")
        cache = sorted
    }
}
