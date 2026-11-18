package com.wanderwildwood.tarot

import com.wanderwildwood.tarot.deck.Drawn
import com.wanderwildwood.tarot.deck.Entry
import com.wanderwildwood.tarot.deck.Spread
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeFalse
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class JournalFileTest {

    @get:Rule val tmp = TemporaryFolder()

    private val reading = Entry(1_760_600_000_000, 20_377, Spread.DAILY, listOf(Drawn(18, true)))

    @Test fun keepsAndReadsBack() {
        val dir = tmp.newFolder()
        assertTrue(JournalFile(dir).put(reading))
        assertEquals(listOf(reading), JournalFile(dir).entries())
    }

    @Test fun aDamagedJournalIsMovedAsideWhole() {
        val dir = tmp.newFolder()
        File(dir, "journal.json").writeText("{ not a journal")
        val j = JournalFile(dir)
        assertEquals(emptyList<Entry>(), j.entries())
        assertTrue(j.setAside)
        assertFalse(j.stuck)
        assertEquals("{ not a journal", File(dir, "journal.json.bad").readText())
        assertTrue(j.put(reading))
    }

    @Test fun aSecondDamagedJournalDoesNotReplaceTheFirst() {
        val dir = tmp.newFolder()
        File(dir, "journal.json.bad").writeText("first")
        File(dir, "journal.json").writeText("second {")
        JournalFile(dir).entries()
        assertEquals("first", File(dir, "journal.json.bad").readText())
        assertEquals("second {", File(dir, "journal.json.bad-2").readText())
    }

    @Test fun aDamagedJournalThatCannotBeMovedIsNeverWrittenOver() {
        val dir = tmp.newFolder()
        val file = File(dir, "journal.json")
        file.writeText("{ damaged")
        // A folder that cannot be changed, so the move fails (unless the tests run as root).
        dir.setWritable(false)
        try {
            assumeFalse(runCatching { File(dir, "probe").createNewFile() }.getOrDefault(false))
            val j = JournalFile(dir)
            assertEquals(emptyList<Entry>(), j.entries())
            assertTrue(j.stuck)
            assertFalse(j.setAside)
            assertFalse(j.put(reading))
            assertFalse(j.delete(reading.id))
        } finally {
            dir.setWritable(true)
        }
        assertEquals("{ damaged", file.readText())
    }

    @Test fun noDamagedFilesWhenTheJournalReads() {
        val dir = tmp.newFolder()
        val j = JournalFile(dir)
        assertTrue(j.put(reading))
        assertEquals(emptyList<File>(), JournalFile(dir).damaged())
    }

    @Test fun damagedFilesAreOfferedInTheOrderTheyWereSetAside() {
        val dir = tmp.newFolder()
        File(dir, "journal.json.bad").writeText("first")
        File(dir, "journal.json.bad-10").writeText("tenth")
        File(dir, "journal.json.bad-2").writeText("second")
        File(dir, "journal.json.bak").writeText("not ours")
        File(dir, "journal.json").writeText("eleventh {")
        val names = JournalFile(dir).damaged().map { it.name }
        assertEquals(listOf("journal.json.bad", "journal.json.bad-2", "journal.json.bad-3", "journal.json.bad-10"), names)
        assertEquals("eleventh {", File(dir, "journal.json.bad-3").readText())
    }

    @Test fun aStuckJournalIsOfferedAsItIs() {
        val dir = tmp.newFolder()
        val file = File(dir, "journal.json")
        file.writeText("{ damaged")
        dir.setWritable(false)
        try {
            assumeFalse(runCatching { File(dir, "probe").createNewFile() }.getOrDefault(false))
            assertEquals(listOf(file), JournalFile(dir).damaged())
        } finally {
            dir.setWritable(true)
        }
    }
}
