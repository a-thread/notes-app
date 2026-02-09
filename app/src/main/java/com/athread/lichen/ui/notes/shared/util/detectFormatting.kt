package com.athread.lichen.ui.notes.shared.util

import androidx.compose.ui.text.input.TextFieldValue
import com.athread.lichen.ui.notes.shared.model.BlockTextStyle
import com.athread.lichen.ui.notes.shared.model.SelectionFormatting

fun detectFormatting(value: TextFieldValue): SelectionFormatting {
    val text = value.text
    val cursor = value.selection.start.coerceIn(0, text.length)

    /* ---------- LINE DETECTION ---------- */
    val lineStart =
        text.lastIndexOf('\n', cursor - 1)
            .let { if (it == -1) 0 else it + 1 }

    val lineEnd =
        text.indexOf('\n', lineStart)
            .let { if (it == -1) text.length else it }

    val line = text.substring(lineStart, lineEnd)

    val textStyle =
        when {
            line.startsWith("### ") -> BlockTextStyle.H3
            line.startsWith("## ") -> BlockTextStyle.H2
            line.startsWith("# ") -> BlockTextStyle.H1
            else -> BlockTextStyle.NORMAL
        }

    val checklist =
        line.startsWith("- [ ] ") || line.startsWith("- [x] ")

    val bullet =
        line.startsWith("- ") && !checklist

    /* ---------- INLINE DETECTION ---------- */
    val bold = isCursorInside(text, cursor, "**")
    val italic = isCursorInsideSingleStar(text, cursor)
    val code = isCursorInside(text, cursor, "`")

    return SelectionFormatting(
        bold = bold,
        italic = italic,
        code = code,
        textStyle = textStyle,
        bullet = bullet,
        checklist = checklist
    )
}

/* ---------- INLINE HELPERS ---------- */
private fun isCursorInside(
    text: String,
    cursor: Int,
    marker: String
): Boolean {
    if (marker.isEmpty()) return false

    val open = text.lastIndexOf(marker, cursor)
    if (open == -1) return false

    val close = text.indexOf(marker, open + marker.length)
    if (close == -1) return false

    val contentStart = open + marker.length
    val contentEnd = close + marker.length // allow cursor AFTER content

    return cursor in contentStart..contentEnd
}

/**
 * Italic detection must be handled separately to avoid:
 * - matching '**'
 * - matching list markers
 * - crashing on cursor boundary updates
 */
private fun isCursorInsideSingleStar(
    text: String,
    cursor: Int
): Boolean {
    if (text.isEmpty()) return false

    // Find '*' before cursor
    val before = text.lastIndexOf('*', cursor - 1)
    if (before == -1) return false

    // Exclude '**'
    if (before > 0 && text[before - 1] == '*') return false

    // Find '*' after cursor
    val after = text.indexOf('*', cursor)
    if (after == -1) return false

    // Exclude '**'
    if (after + 1 < text.length && text[after + 1] == '*') return false

    return before < cursor && after >= cursor
}
