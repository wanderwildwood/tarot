package com.wanderwildwood.tarot.deck

import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class JournalTest {

    private val entries = listOf(
        Entry(1_760_700_000_000, 20_378, Spread.CELTIC, Draw.cards(10, true), "A question about work,\n\"quoted\""),
        Entry(1_760_600_000_000, 20_377, Spread.DAILY, listOf(Drawn(18, true))),
        Entry(1_760_500_000_000, 20_376, Spread.THREE, listOf(Drawn(0, false), Drawn(77, true), Drawn(40, false))),
    )

    @Test
    fun roundTrips() {
        assertEquals(entries, JournalCodec.decode(JournalCodec.encode(entries)))
    }

    @Test
    fun exportCarriesNamesAndDates() {
        val json = JSONObject(JournalCodec.encode(entries) { "card #$it" })
        val first = json.getJSONArray("readings").getJSONObject(2)
        assertEquals("2025-10-15", first.getString("date"))
        assertEquals("three", first.getString("spread"))
        val card = first.getJSONArray("cards").getJSONObject(1)
        assertEquals("pentacles14", card.getString("card"))
        assertEquals("card #77", card.getString("name"))
        assertTrue(card.getBoolean("reversed"))
        // And reads back the same, names and all.
        assertEquals(entries, JournalCodec.decode(json.toString()))
    }

    @Test
    fun aBrokenEntryDoesNotLoseTheRest() {
        val json = JSONObject(JournalCodec.encode(entries))
        json.getJSONArray("readings").getJSONObject(1).put("spread", "nonsense")
        assertEquals(listOf(entries[0], entries[2]), JournalCodec.decode(json.toString()))
        assertEquals(emptyList<Entry>(), JournalCodec.decode(""))
    }

    @Test
    fun deckIdsAreTheArtNames() {
        assertEquals(78, Deck.ids.toSet().size)
        assertEquals("m00", Deck.id(0))
        assertEquals("m21", Deck.id(21))
        assertEquals("wands01", Deck.id(22))
        assertEquals("pentacles14", Deck.id(77))
    }
}

/** Restoring an export: the whole file is checked first, and a wrong one is refused. */
class JournalRestoreTest {

    private val entries = listOf(
        Entry(1_760_700_000_000, 20_378, Spread.CELTIC, Draw.cards(10, true), "A question about Ada's job,\n\"quoted\""),
        Entry(1_760_600_000_000, 20_377, Spread.DAILY, listOf(Drawn(18, true))),
        Entry(1_760_500_000_000, 20_376, Spread.THREE, listOf(Drawn(0, false), Drawn(77, true), Drawn(40, false))),
    )

    private fun refused(text: String, kind: JournalCodec.JournalException.Kind) {
        try {
            JournalCodec.decodeStrict(text)
            org.junit.Assert.fail("should have been refused")
        } catch (e: JournalCodec.JournalException) {
            assertEquals(kind, e.kind)
        }
    }

    private fun edited(change: (JSONObject) -> Unit): String {
        val o = JSONObject(JournalCodec.encode(entries) { "card #$it" })
        change(o)
        return o.toString()
    }

    @Test
    fun exportThenRestoreGivesTheSameReadings() {
        assertEquals(entries, JournalCodec.decodeStrict(JournalCodec.encode(entries) { "card #$it" }))
        assertEquals(entries, JournalCodec.decodeStrict(JournalCodec.encode(entries)))
        assertEquals(emptyList<Entry>(), JournalCodec.decodeStrict(JournalCodec.encode(emptyList())))
    }

    @Test
    fun otherFilesAreNotAJournal() {
        refused("", JournalCodec.JournalException.Kind.NOT_A_JOURNAL)
        refused("not json at all", JournalCodec.JournalException.Kind.NOT_A_JOURNAL)
        refused("{\"format\":\"nikka-backup\",\"version\":1}", JournalCodec.JournalException.Kind.NOT_A_JOURNAL)
        refused("[1,2]", JournalCodec.JournalException.Kind.NOT_A_JOURNAL)
        val text = JournalCodec.encode(entries)
        refused(text.substring(0, text.length / 2), JournalCodec.JournalException.Kind.NOT_A_JOURNAL)
    }

    @Test
    fun aNewerVersionIsNamedAsNewer() {
        refused(edited { it.put("version", 2) }, JournalCodec.JournalException.Kind.NEWER)
    }

    @Test
    fun oneBadReadingRefusesTheWholeFile() {
        // The app's own decode would skip it and keep the rest; a restore must not half-restore.
        refused(edited { it.getJSONArray("readings").getJSONObject(1).put("spread", "nonsense") }, JournalCodec.JournalException.Kind.DAMAGED)
        refused(edited { it.getJSONArray("readings").getJSONObject(0).getJSONArray("cards").getJSONObject(0).put("card", "m99") }, JournalCodec.JournalException.Kind.DAMAGED)
        refused(edited { it.getJSONArray("readings").getJSONObject(2).put("date", "yesterday") }, JournalCodec.JournalException.Kind.DAMAGED)
        refused(edited { it.getJSONArray("readings").getJSONObject(2).getJSONArray("cards").remove(0) }, JournalCodec.JournalException.Kind.DAMAGED)
        refused(edited { it.getJSONArray("readings").getJSONObject(1).put("id", 1_760_700_000_000) }, JournalCodec.JournalException.Kind.DAMAGED)
    }
}
