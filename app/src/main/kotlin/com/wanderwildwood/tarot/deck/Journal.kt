package com.wanderwildwood.tarot.deck

import org.json.JSONArray
import org.json.JSONObject

/**
 * One reading as kept: when, which spread, the cards in the spread's order with their
 * orientation, and a note. [id] is the moment it was drawn, in milliseconds, and is unique.
 */
data class Entry(
    val id: Long,
    val epochDay: Long,
    val spread: Spread,
    val cards: List<Drawn>,
    val note: String = "",
) {
    init {
        require(cards.size == spread.size) { "${spread.key} takes ${spread.size} cards" }
    }
}

/** The journal in and out of JSON. The app's own file and the export share the shape. */
object JournalCodec {
    const val VERSION = 1

    fun encode(entries: List<Entry>, names: ((Int) -> String)? = null): String {
        val list = JSONArray()
        for (e in entries) {
            val cards = JSONArray()
            for (d in e.cards) {
                cards.put(
                    JSONObject()
                        .put("card", Deck.id(d.card))
                        .put("reversed", d.reversed)
                        .also { if (names != null) it.put("name", names(d.card)) },
                )
            }
            list.put(
                JSONObject()
                    .put("id", e.id)
                    .put("date", java.time.LocalDate.ofEpochDay(e.epochDay).toString())
                    .put("spread", e.spread.key)
                    .put("cards", cards)
                    .put("note", e.note),
            )
        }
        return JSONObject().put("version", VERSION).put("readings", list).toString(1)
    }

    /** Entries that cannot be read are skipped rather than losing the rest. */
    fun decode(text: String): List<Entry> {
        if (text.isBlank()) return emptyList()
        val list = JSONObject(text).optJSONArray("readings") ?: return emptyList()
        val out = ArrayList<Entry>(list.length())
        for (i in 0 until list.length()) {
            runCatching {
                val o = list.getJSONObject(i)
                val spread = Spread.of(o.getString("spread")) ?: return@runCatching
                val cs = o.getJSONArray("cards")
                val cards = (0 until cs.length()).map { k ->
                    val c = cs.getJSONObject(k)
                    val card = Deck.ids.indexOf(c.getString("card"))
                    Drawn(card, c.optBoolean("reversed"))
                }
                out += Entry(
                    id = o.getLong("id"),
                    epochDay = java.time.LocalDate.parse(o.getString("date")).toEpochDay(),
                    spread = spread,
                    cards = cards,
                    note = o.optString("note"),
                )
            }
        }
        return out
    }
}
