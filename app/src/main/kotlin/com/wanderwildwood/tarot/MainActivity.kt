package com.wanderwildwood.tarot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import com.mudita.mmd.ThemeMMD
import com.wanderwildwood.tarot.ui.TarotApp
import com.wanderwildwood.tarot.ui.monochrome

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalComposeUiApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val store = Store(this)
        setContent {
            ThemeMMD(colorScheme = monochrome) {
                // A debug build names its places for the emulator tests; a release names none.
                Box(Modifier.semantics { testTagsAsResourceId = BuildConfig.DEBUG }) {
                    TarotApp(store)
                }
            }
        }
    }
}
