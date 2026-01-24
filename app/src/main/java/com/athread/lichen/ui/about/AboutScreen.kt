package com.athread.lichen.ui.about

import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.athread.lichen.BuildConfig
import com.athread.lichen.ui.shared.composable.DetailScaffold

@Composable
fun AboutScreen(
    onBack: () -> Unit
) {
    DetailScaffold(
        title = "com.athread.lichen",
        subtitle = "Ideas, resilient by design.",
        onBack = onBack
    ) {

        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth(0.4f)
                .padding(vertical = 32.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )

        // First paragraph — slightly emphasized
        AboutParagraph(
            text = "Lichen is built around a simple belief: ideas should survive change.",
            style = MaterialTheme.typography.bodyLarge
        )

        AboutParagraph(
            "Thoughts evolve. Notes are rewritten, reorganized, and reframed as understanding deepens. Too often, this process risks losing meaning along the way."
        )

        AboutParagraph(
            "Lichen is designed for thinking over time. It supports revision without loss, flexibility without fragility, and ideas that persist as they change."
        )

        Spacer(Modifier.height(32.dp))

        Text(
            text = "Version ${BuildConfig.VERSION_NAME}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun AboutParagraph(
    text: String,
    style: TextStyle = MaterialTheme.typography.bodyMedium
) {
    Text(
        text = text,
        style = style,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp)
    )
}
