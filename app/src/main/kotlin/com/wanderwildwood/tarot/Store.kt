package com.wanderwildwood.tarot

import android.content.Context
import com.wanderwildwood.tarot.deck.Entry
import com.wanderwildwood.tarot.deck.JournalCodec
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * What the app keeps, all of it on the phone: the reversed-cards setting in a preferences file, and the
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

    /**
     * True once a journal file that could not be read was set aside (as journal.json.bad) and a
     * new journal begun, so the app can say so instead of closing at every start.
     */
    var setAside = false
        private set

    /** Newest first. */
    fun entries(): List<Entry> =
        cache ?: read().sortedByDescending { it.id }.also { cache = it }

    private fun read(): List<Entry> {
        if (!file.isFile) return emptyList()
        return try {
            JournalCodec.decode(file.readText())
        } catch (e: Exception) {
            // Kept, never written over: the next save starts a new journal beside it.
            runCatching { file.copyTo(File(file.parentFile, "journal.json.bad"), overwrite = true) }
            setAside = true
            emptyList()
        }
    }

    fun entry(id: Long) = entries().firstOrNull { it.id == id }

    /** Each change returns false when it could not be saved; the journal is then as it was. */
    fun put(e: Entry): Boolean = write(listOf(e) + entries().filter { it.id != e.id })

    fun delete(id: Long): Boolean = write(entries().filter { it.id != id })

    /**
     * Adds the readings of a restored file that are not here yet, in one write; a reading with
     * an id already in the journal is left as it is. Returns how many were added.
     */
    fun merge(restored: List<Entry>): Int {
        val have = entries()
        val ids = have.map { it.id }.toSet()
        val fresh = restored.filter { it.id !in ids }
        if (fresh.isNotEmpty() && !write(have + fresh)) throw IOException("could not save the journal")
        return fresh.size
    }

    private fun write(list: List<Entry>): Boolean {
        val sorted = list.sortedByDescending { it.id }
        val tmp = File(file.parentFile, "journal.json.tmp")
        val ok = runCatching {
            // On the disk before the rename, so a battery dying mid-save leaves the old journal.
            FileOutputStream(tmp).use { out ->
                out.write(JournalCodec.encode(sorted).toByteArray(Charsets.UTF_8))
                out.fd.sync()
            }
            tmp.renameTo(file)
        }.getOrDefault(false)
        if (!ok) runCatching { tmp.delete() } else cache = sorted
        return ok
    }
}
