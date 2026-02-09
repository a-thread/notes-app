package com.athread.lichen.ui.notes.detail.composables.readonly

import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.athread.lichen.ui.notes.shared.model.EditorBlock
import com.athread.lichen.ui.notes.shared.util.parseEditorBlocks

@Composable
fun ReadOnlyNote(
    title: String,
    body: String,
    onEdit: () -> Unit,
    onToggleChecklist: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {

        /* ---------- TITLE ---------- */

        if (title.isNotBlank()) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.2.sp
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(20.dp))
        }

        /* ---------- BODY ---------- */

        val blocks = remember(body) {
            TextFieldValue(body).parseEditorBlocks()
        }

        blocks.forEachIndexed { index, block ->
            when (block) {

                /* ---------- HEADINGS ---------- */

                is EditorBlock.HeadingBlock -> {
                    if (index != 0) Spacer(Modifier.height(20.dp))

                    val style = when (block.level) {
                        1 -> MaterialTheme.typography.headlineSmall
                        2 -> MaterialTheme.typography.titleLarge
                        else -> MaterialTheme.typography.titleMedium
                    }

                    Text(
                        text = block.text,
                        style = style.copy(
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 0.15.sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    if (block.level == 1) {
                        Spacer(Modifier.height(6.dp))
                        HorizontalDivider(
                            modifier = Modifier.width(48.dp),
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                    }

                    Spacer(Modifier.height(12.dp))
                }

                /* ---------- PARAGRAPH ---------- */

                is EditorBlock.TextBlock -> {
                    if (block.text.isNotBlank()) {
                        Text(
                            text = renderInlineMarkdown(block.text),
                            style = MaterialTheme.typography.bodyLarge.copy(
                                lineHeight = 24.sp
                            ),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(Modifier.height(14.dp))
                    }
                }

                /* ---------- BULLET LIST (NESTED) ---------- */

                is EditorBlock.BulletListBlock -> {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        block.items.forEach { raw ->
                            val level = indentLevel(raw)
                            val text = stripBulletPrefix(raw)

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = (level * 16).dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = "•",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(top = 2.dp)
                                )

                                Text(
                                    text = renderInlineMarkdown(text),
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        lineHeight = 22.sp
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }

                /* ---------- CHECKLIST (NESTED) ---------- */

                is EditorBlock.ChecklistBlock -> {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        block.items.forEach { item ->
                            val level = indentLevel(item.text)

                            Box(
                                modifier = Modifier.padding(start = (level * 16).dp)
                            ) {
                                ChecklistItemRow(
                                    item = item,
                                    onToggle = { onToggleChecklist(item.lineIndex) },
                                    onEdit = onEdit
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }

                /* ---------- DIVIDER ---------- */

                is EditorBlock.DividerBlock -> {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 20.dp),
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                }
            }
        }
    }
}

/* ---------- NESTING HELPERS ---------- */

private fun indentLevel(line: String): Int {
    val leadingSpaces = line.takeWhile { it == ' ' }.length
    return leadingSpaces / 2
}

private fun stripBulletPrefix(line: String): String =
    line.trimStart().removePrefix("- ").trimStart()

/* ---------- INLINE MARKDOWN ---------- */

@Composable
private fun renderInlineMarkdown(text: String): AnnotatedString {
    return buildAnnotatedString {
        var i = 0
        while (i < text.length) {

            // **bold**
            if (text.startsWith("**", i)) {
                val end = text.indexOf("**", i + 2)
                if (end != -1) {
                    withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) {
                        append(text.substring(i + 2, end))
                    }
                    i = end + 2
                    continue
                }
            }

            // *italic*
            if (text.startsWith("*", i)) {
                val end = text.indexOf("*", i + 1)
                if (end != -1) {
                    withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                        append(text.substring(i + 1, end))
                    }
                    i = end + 1
                    continue
                }
            }

            // `code`
            if (text.startsWith("`", i)) {
                val end = text.indexOf("`", i + 1)
                if (end != -1) {
                    withStyle(
                        SpanStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 14.sp,
                            background = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        append(" ${text.substring(i + 1, end)} ")
                    }
                    i = end + 1
                    continue
                }
            }

            append(text[i])
            i++
        }
    }
}
