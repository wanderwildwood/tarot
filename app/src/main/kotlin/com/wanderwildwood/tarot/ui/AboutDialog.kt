package com.wanderwildwood.tarot.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mudita.mmd.components.buttons.OutlinedButtonMMD
import com.mudita.mmd.components.text.TextMMD
import com.wanderwildwood.tarot.BuildConfig
import com.wanderwildwood.tarot.R

/**
 * What this is, its privacy and terms, what it is made of, and where its source is. The text
 * runs to more than a dialog holds, so it turns pages like any list, with Close under it.
 */
@Composable
fun AboutDialog(onDismiss: () -> Unit) {
    val screen = LocalConfiguration.current.screenHeightDp
    EInkDialog(onDismiss = onDismiss) {
        TextMMD(
            text = stringResource(R.string.about_title, BuildConfig.VERSION_NAME),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
        )
        Spacer(Modifier.height(8.dp))
        PagedColumn(Modifier.fillMaxWidth().heightIn(max = (screen * 0.6f).dp).testTag("about")) {
            item { TextMMD(text = stringResource(R.string.about_what), style = MaterialTheme.typography.bodyMedium) }
            item { Section(R.string.about_privacy_title, R.string.about_privacy) }
            item { Section(R.string.about_terms_title, R.string.about_terms) }
            item { Heading(stringResource(R.string.about_open_title)) }
            for (line in listOf(
                R.string.about_licence, R.string.about_art, R.string.about_meanings,
                R.string.about_components, R.string.about_icons,
            )) {
                item(key = line) {
                    TextMMD(
                        text = stringResource(line),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 6.dp),
                    )
                }
            }
            item { LinkRow(stringResource(R.string.about_source), "https://github.com/wanderwildwood/tarot") }
            item { Llama() }
        }
        Spacer(Modifier.height(12.dp))
        HouseButton(stringResource(R.string.about_close), onClick = onDismiss, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun Section(title: Int, body: Int) {
    Heading(stringResource(title))
    TextMMD(text = stringResource(body), style = MaterialTheme.typography.bodyMedium)
}

/**
 * Opens [url] in a browser, and says so in plain words when there is none. Only a real browser
 * is asked: the system's bare WebView shell answers a web address on some phones and then
 * crashes, so it counts as no browser, and the address is shown instead. Never a WebView here.
 */
fun openLink(context: android.content.Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).addCategory(Intent.CATEGORY_BROWSABLE)
    val handler = context.packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
    val pkg = handler?.activityInfo?.packageName
    val shown = url.removePrefix("https://")
    if (pkg == null || pkg == "android" || pkg.contains("webview_shell")) {
        Toast.makeText(context, context.getString(R.string.about_no_browser_address, shown), Toast.LENGTH_LONG).show()
        return
    }
    try {
        context.startActivity(intent)
    } catch (_: android.content.ActivityNotFoundException) {
        Toast.makeText(context, context.getString(R.string.about_no_browser_address, shown), Toast.LENGTH_LONG).show()
    }
}

/** A line that is an address and opens it, with a target a thumb can find. */
@Composable
private fun LinkRow(text: String, url: String) {
    val context = LocalContext.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).clickable { openLink(context, url) },
    ) {
        TextMMD(text = text, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
    }
}

/**
 * A llama at the foot of the About, which opens the page a donation goes to. The site's
 * address opens the site; the llama and its words open the page. Straight to the checkout,
 * the short square.link form the site itself links to.
 */
@Composable
private fun Llama() {
    val context = LocalContext.current
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        TextMMD(
            text = "wanderthe.dev",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.heightIn(min = 48.dp).clickable { openLink(context, "https://wanderthe.dev") }
                .padding(top = 12.dp, end = 12.dp),
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.heightIn(min = 48.dp).clickable { openLink(context, "https://square.link/u/AGu8oT10") },
        ) {
            Image(painter = painterResource(R.drawable.llama), contentDescription = null, modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(6.dp))
            TextMMD(text = stringResource(R.string.about_feed_the_llamas), style = MaterialTheme.typography.bodyMedium)
        }
    }
}
