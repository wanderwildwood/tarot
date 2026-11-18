package com.wanderwildwood.tarot

import com.wanderwildwood.tarot.deck.Entry
import com.wanderwildwood.tarot.deck.JournalCodec
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * The journal as one JSON file in [dir], rewritten whole on each change (a temporary file renamed
 * over it, so a write cut short cannot leave half a journal).
 */
class JournalFile(private val dir: File) {
    private val file = File(dir, NAME)

    private var cache: List<Entry>? = null

    /**
     * True once a journal file that could not be read was moved aside (as journal.json.bad, or
     * journal.json.bad-2 and on when that name is taken) and a new journal begun, so the app can
     * say so instead of closing at every start.
     */
    var setAside = false
        private set

    /**
     * True when the journal could not be read and could not be moved aside either. It is then
     * left where it is and nothing is saved over it; the next start tries again.
     */
    var stuck = false
        private set

    /** Newest first. */
    fun entries(): List<Entry> =
        cache ?: read().sortedByDescending { it.id }.also { cache = it }

    private fun read(): List<Entry> {
        if (!file.isFile) return emptyList()
        return try {
            JournalCodec.decode(file.readText())
        } catch (e: Exception) {
            // Moved, not copied: a rename needs no room on a full phone and cannot half happen.
            // A file set aside before is never written over.
            if (runCatching { file.renameTo(freeBadName()) }.getOrDefault(false)) setAside = true else stuck = true
            emptyList()
        }
    }

    private fun freeBadName(): File {
        var bad = File(dir, "$NAME.bad")
        var n = 2
        while (bad.exists()) bad = File(dir, "$NAME.bad-${n++}")
        return bad
    }

    /**
     * The journal files that could not be read and are still kept, so they can be saved out as
     * they are: those moved aside, in the order they were set aside, then the journal itself
     * while it is stuck. Empty when there are none.
     */
    fun damaged(): List<File> {
        entries()
        val aside = dir.listFiles()?.filter { it.isFile && badNumber(it.name) != null }.orEmpty()
            .sortedBy { badNumber(it.name) }
        return if (stuck && file.isFile) aside + file else aside
    }

    /** 1 for journal.json.bad, n for journal.json.bad-n, null for any other name. */
    private fun badNumber(name: String): Int? = when {
        name == "$NAME.bad" -> 1
        name.startsWith("$NAME.bad-") -> name.removePrefix("$NAME.bad-").toIntOrNull()
        else -> null
    }

    fun entry(id: Long) = entries().firstOrNull { it.id == id }

    /** Each change returns false when it could not be saved; the journal is then as it was. */
    fun put(e: Entry): Boolean = write(listOf(e) + entries().filter { it.id != e.id })

    fun delete(id: Long): Boolean = write(entries().filter { it.id != id })

    /**
     * Adds the readings of a restored file that are not here yet, in one write; a reading with
     * an id already in the journal is left as it is. Returns how many were added; throws
     * [IOException] when they could not be saved.
     */
    fun merge(restored: List<Entry>): Int {
        val have = entries()
        val ids = have.map { it.id }.toSet()
        val fresh = restored.filter { it.id !in ids }
        if (fresh.isNotEmpty() && !write(have + fresh)) throw IOException("could not save the journal")
        return fresh.size
    }

    private fun write(list: List<Entry>): Boolean {
        entries()
        // The journal that could not be read is still in its place: nothing goes over it.
        if (stuck) return false
        val sorted = list.sortedByDescending { it.id }
        val tmp = File(dir, "$NAME.tmp")
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

    companion object {
        const val NAME = "journal.json"
    }
}
