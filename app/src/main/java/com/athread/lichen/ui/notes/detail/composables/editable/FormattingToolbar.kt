package com.athread.lichen.ui.notes.detail.composables.editable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.athread.lichen.ui.notes.shared.model.BlockTextStyle
import com.athread.lichen.ui.notes.shared.util.detectFormatting

@Composable
fun FormattingToolbar(
    body: TextFieldValue,
    onApply: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier
) {
    val formatting = remember(body) {
        detectFormatting(body)
    }
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            /* ---------- TEXT STYLE ---------- */

            TextStyleDropdown(
                currentStyle = formatting.textStyle,
                onChange = { style ->
                    onApply(applyTextStyle(body, style))
                }
            )
            ToolbarDivider()

            /* ---------- INLINE ---------- */

            ToolbarIcon(
                icon = Icons.Default.FormatBold,
                selected = formatting.bold,
                onClick = {
                    onApply(toggleWrap(body, "**", formatting.bold))
                }
            )
            ToolbarIcon(
                icon = Icons.Default.FormatItalic,
                selected = formatting.italic,
                onClick = {
                    onApply(toggleWrap(body, "*", formatting.italic))
                }
            )
            ToolbarDivider()

            /* ---------- LISTS ---------- */

            ToolbarIcon(
                icon = Icons.AutoMirrored.Filled.FormatListBulleted,
                selected = formatting.bullet,
                onClick = {
                    onApply(toggleWrap(body, "- ", formatting.bullet))
                }
            )

            ToolbarIcon(
                icon = Icons.Default.Checklist,
                selected = formatting.checklist,
                onClick = {
                    onApply(toggleWrap(body, "- [ ] ", formatting.checklist))
                }
            )

            ToolbarDivider()

            /* ---------- CODE ---------- */

            ToolbarIcon(
                icon = Icons.Default.Code,
                selected = formatting.code,
                onClick = {
                    onApply(toggleWrap(body, "`", formatting.code))
                }
            )

        }
    }
}

@Composable
private fun ToolbarIcon(
    icon: ImageVector,
    selected: Boolean = false,
    onClick: () -> Unit
) {
    val background =
        if (selected)
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        else
            Color.Transparent

    val tint =
        if (selected)
            MaterialTheme.colorScheme.onSurface
        else
            MaterialTheme.colorScheme.onSurfaceVariant

    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(36.dp)
            .background(
                color = background,
                shape = MaterialTheme.shapes.small
            )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint
        )
    }
}

@Composable
private fun ToolbarDivider() {
    Spacer(Modifier.width(6.dp))
    VerticalDivider(
        modifier = Modifier.height(20.dp),
        thickness = 1.dp,
        color = MaterialTheme.colorScheme.outlineVariant
    )
    Spacer(Modifier.width(6.dp))
}

/* ---------- TEXT STYLE DROPDOWN ---------- */
@Composable
fun TextStyleDropdown(
    currentStyle: BlockTextStyle,
    onChange: (BlockTextStyle) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val label =
        when (currentStyle) {
            BlockTextStyle.NORMAL -> "Normal"
            BlockTextStyle.H1 -> "Heading 1"
            BlockTextStyle.H2 -> "Heading 2"
            BlockTextStyle.H3 -> "Heading 3"
        }

    Box {
        TextButton(onClick = { expanded = true }) {
            Text(label)
            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Normal") },
                onClick = {
                    expanded = false
                    onChange(BlockTextStyle.NORMAL)
                }
            )
            DropdownMenuItem(
                text = { Text("Heading 1") },
                onClick = {
                    expanded = false
                    onChange(BlockTextStyle.H1)
                }
            )
            DropdownMenuItem(
                text = { Text("Heading 2") },
                onClick = {
                    expanded = false
                    onChange(BlockTextStyle.H2)
                }
            )
            DropdownMenuItem(
                text = { Text("Heading 3") },
                onClick = {
                    expanded = false
                    onChange(BlockTextStyle.H3)
                }
            )
        }
    }
}

/* ---------- TEXT TRANSFORMS ---------- */
fun applyTextStyle(
    value: TextFieldValue,
    style: BlockTextStyle
): TextFieldValue {
    val text = value.text
    val cursor = value.selection.start

    val lineStart =
        text.lastIndexOf('\n', cursor - 1)
            .let { if (it == -1) 0 else it + 1 }

    val lineEnd =
        text.indexOf('\n', lineStart)
            .let { if (it == -1) text.length else it }

    val line = text.substring(lineStart, lineEnd)

    val stripped =
        line
            .removePrefix("### ")
            .removePrefix("## ")
            .removePrefix("# ")

    val newPrefix =
        when (style) {
            BlockTextStyle.NORMAL -> ""
            BlockTextStyle.H1 -> "# "
            BlockTextStyle.H2 -> "## "
            BlockTextStyle.H3 -> "### "
        }

    val newLine = newPrefix + stripped

    val newText =
        text.substring(0, lineStart) +
                newLine +
                text.substring(lineEnd)

    return value.copy(
        text = newText,
        selection = TextRange(cursor + (newPrefix.length - (line.length - stripped.length)))
    )
}


private fun toggleWrap(
    value: TextFieldValue,
    marker: String,
    isActive: Boolean
): TextFieldValue {
    val text = value.text
    val sel = value.selection

    val cursor = sel.start

    if (!isActive) {
        // APPLY
        if (sel.collapsed) return value

        return value.copy(
            text =
                text.substring(0, sel.start) +
                        marker +
                        text.substring(sel.start, sel.end) +
                        marker +
                        text.substring(sel.end),
            selection = TextRange(
                sel.start + marker.length,
                sel.end + marker.length
            )
        )
    }

    // REMOVE
    val open = text.lastIndexOf(marker, cursor)
    val close = text.indexOf(marker, open + marker.length)

    if (open == -1 || close == -1) return value

    val newText =
        text.removeRange(close, close + marker.length)
            .removeRange(open, open + marker.length)

    val newCursor =
        (cursor - marker.length).coerceAtLeast(open)

    return value.copy(
        text = newText,
        selection = TextRange(newCursor)
    )
}