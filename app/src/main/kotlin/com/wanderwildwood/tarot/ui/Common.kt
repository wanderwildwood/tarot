package com.wanderwildwood.tarot.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mudita.mmd.components.buttons.ButtonMMD
import com.mudita.mmd.components.buttons.OutlinedButtonMMD
import com.mudita.mmd.components.divider.HorizontalDividerMMD
import com.mudita.mmd.components.switcher.SwitchMMD
import com.mudita.mmd.components.text.TextMMD
import kotlinx.coroutines.delay

/** An icon in a top bar, with a target a thumb can find. */
@Composable
fun BarButton(icon: ImageVector, description: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier.size(48.dp).clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = description,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(22.dp),
        )
    }
}

/** A row that says what it is and what it is set to, and does something when pressed. */
@Composable
fun SettingRow(title: String, value: String?, onClick: (() -> Unit)?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .let { if (onClick != null) it.clickable(onClick = onClick) else it }
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            TextMMD(text = title, style = MaterialTheme.typography.bodyMedium)
            if (value != null) TextMMD(text = value, style = MaterialTheme.typography.labelSmall)
        }
        if (onClick != null) Chevron()
    }
}

/** The mark at the end of a row that opens something. */
@Composable
fun Chevron() {
    TextMMD(
        text = "›",
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(start = 12.dp),
    )
}

@Composable
fun SwitchRow(title: String, checked: Boolean, note: String? = null, onChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onChange(!checked) }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            TextMMD(text = title, style = MaterialTheme.typography.bodyMedium)
            if (note != null) TextMMD(text = note, style = MaterialTheme.typography.labelSmall)
        }
        Spacer(Modifier.width(12.dp))
        SwitchMMD(checked = checked, onCheckedChange = null)
    }
}

/**
 * A button that asks once, in its own face: the first press arms it and changes what it says,
 * the second does it. It disarms itself after six seconds, so a stray press does not leave
 * a live trigger for whoever picks the phone up next.
 */
@Composable
fun ArmedRow(label: String, armedLabel: String, onConfirmed: () -> Unit) {
    var armed by remember { mutableStateOf(false) }
    LaunchedEffect(armed) {
        if (!armed) return@LaunchedEffect
        delay(6000)
        armed = false
    }
    WideButton(text = if (armed) armedLabel else label, filled = armed) {
        if (armed) {
            armed = false
            onConfirmed()
        } else {
            armed = true
        }
    }
}

/** A small heading over a group of rows. */
@Composable
fun Heading(text: String, modifier: Modifier = Modifier) {
    TextMMD(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        modifier = modifier.padding(top = 18.dp, bottom = 4.dp),
    )
}

/** A thin rule between rows, as under a top bar. */
@Composable
fun Rule() = HorizontalDividerMMD(thickness = 1.dp)

/** A full-width button. */
@Composable
fun WideButton(text: String, filled: Boolean = false, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val m = modifier.fillMaxWidth().padding(vertical = 4.dp).height(48.dp)
    if (filled) {
        ButtonMMD(onClick = onClick, modifier = m) { TextMMD(text = text, style = MaterialTheme.typography.bodyMedium) }
    } else {
        OutlinedButtonMMD(onClick = onClick, modifier = m) { TextMMD(text = text, style = MaterialTheme.typography.bodyMedium) }
    }
}
