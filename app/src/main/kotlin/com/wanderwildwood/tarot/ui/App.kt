package com.wanderwildwood.tarot.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.mudita.mmd.components.text.TextMMD
import com.mudita.mmd.components.top_app_bar.TopAppBarMMD
import com.wanderwildwood.tarot.R
import com.wanderwildwood.tarot.Store
import com.wanderwildwood.tarot.deck.Draw
import com.wanderwildwood.tarot.deck.Drawn
import com.wanderwildwood.tarot.deck.Entry
import com.wanderwildwood.tarot.deck.Spread
import java.time.LocalDate

/** Where the app is. The tabs are one place; everything else is stacked on top of them. */
sealed interface Screen {
    data object Tabs : Screen
    data class Reading(val id: Long) : Screen
    /** A card on its own page; [position] names its place when it came from a reading. */
    data class Card(val drawn: Drawn, val position: String?) : Screen
    data class Zoom(val drawn: Drawn) : Screen
    data object Settings : Screen
    data object Guide : Screen
}

/** The journal and the setting, held so a change redraws what shows them. */
class AppState(val store: Store) {
    var version by mutableIntStateOf(0)
        private set
    val entries get() = version.let { store.entries() }
    var reversals: Boolean
        get() = version.let { store.reversals }
        set(v) { store.reversals = v; version++ }

    fun put(e: Entry) { store.put(e); version++ }
    /** The reading just deleted, kept until the next thing is done so it can be put back. */
    var deleted by mutableStateOf<Entry?>(null)
        private set

    fun delete(id: Long) {
        deleted = store.entries().firstOrNull { it.id == id }
        store.delete(id)
        version++
    }
    fun undelete() { deleted?.let { put(it) }; deleted = null }
    fun forgetDeleted() { deleted = null }

    /** Draws [spread] afresh and keeps it in the journal straight away. */
    fun draw(spread: Spread): Entry {
        val e = Entry(
            id = maxOf(System.currentTimeMillis(), (store.entries().maxOfOrNull { it.id } ?: 0) + 1),
            epochDay = LocalDate.now().toEpochDay(),
            spread = spread,
            cards = Draw.cards(spread.size, store.reversals),
        )
        put(e)
        return e
    }
}

@Composable
fun TarotApp(store: Store) {
    val state = remember { AppState(store) }
    val stack = remember { mutableStateListOf<Screen>(Screen.Tabs) }
    var tab by remember { mutableIntStateOf(0) }
    var about by remember { mutableStateOf(false) }

    fun go(s: Screen) { state.forgetDeleted(); stack.add(s) }
    fun back() { if (stack.size > 1) stack.removeAt(stack.lastIndex) }

    if (stack.size > 1) BackHandler { back() }

    when (val s = stack.last()) {
        Screen.Tabs -> Tabs(state, tab, onTab = { tab = it }, go = ::go)
        is Screen.Reading -> ReadingScreen(state, s.id, onBack = ::back, go = ::go, replace = { stack[stack.lastIndex] = it })
        is Screen.Card -> CardScreen(s.drawn, s.position, onBack = ::back, onZoom = { go(Screen.Zoom(s.drawn)) })
        is Screen.Zoom -> ZoomScreen(s.drawn, onBack = ::back)
        Screen.Settings -> SettingsScreen(state, onBack = ::back, onAbout = { about = true }, onGuide = { go(Screen.Guide) })
        Screen.Guide -> GuideScreen(onBack = ::back)
    }
    if (about) AboutDialog(onDismiss = { about = false })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Tabs(state: AppState, tab: Int, onTab: (Int) -> Unit, go: (Screen) -> Unit) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            TopAppBarMMD(
                title = { TextMMD(text = stringResource(R.string.app_name)) },
                actions = { BarButton(Icons.Settings, stringResource(R.string.cd_settings)) { go(Screen.Settings) } },
            )
        },
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            StoneTabs(
                labels = listOf(
                    stringResource(R.string.tab_today),
                    stringResource(R.string.tab_spread),
                    stringResource(R.string.tab_journal),
                    stringResource(R.string.tab_cards),
                ),
                selected = tab,
                onSelect = { state.forgetDeleted(); onTab(it) },
            )
            state.deleted?.let { UndoBar(onUndo = state::undelete) }
            when (tab) {
                0 -> TodayTab(state, go)
                1 -> SpreadTab(state, go)
                2 -> JournalTab(state, go, onTab)
                else -> CardsTab(go)
            }
        }
    }
}

/** Under the tabs after a reading is deleted, whichever tab is open: the way to put it back. */
@Composable
private fun UndoBar(onUndo: () -> Unit) {
    Column(Modifier.padding(horizontal = 20.dp, vertical = 6.dp).testTag("undo_bar")) {
        TextMMD(text = stringResource(R.string.deleted_done), style = MaterialTheme.typography.bodyMedium)
        HouseButton(stringResource(R.string.deleted_undo), onClick = onUndo, modifier = WideRow)
    }
}
