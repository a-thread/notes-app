package com.example.notes.ui.notedetail.logic

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

/**
 * Editor text transformation:
 * - "- item" → "- [ ] item"
 * - Enter continues checklist
 * - Enter on empty checklist exits list
 * - Backspace collapses bullet cleanly
 * - IME-safe
 */
fun transformEditorInput(
    oldValue: TextFieldValue,
    newValue: TextFieldValue
): TextFieldValue {
    val oldText = oldValue.text
    val newText = newValue.text
    val newCursor = newValue.selection.start.coerceIn(0, newText.length)

    // ---------- ENTER ----------
    val insertedNewline =
        newText.length > oldText.length &&
                newCursor > 0 &&
                newText.getOrNull(newCursor - 1) == '\n'

    if (insertedNewline) {
        val prevLineStart =
            newText.lastIndexOf('\n', (newCursor - 2).coerceAtLeast(0))
                .let { if (it == -1) 0 else it + 1 }

        val prevLineEnd = (newCursor - 1).coerceAtLeast(prevLineStart)
        val prevLine =
            if (prevLineStart <= prevLineEnd)
                newText.substring(prevLineStart, prevLineEnd)
            else ""

        // ---------- PROMOTE "- item" → checklist and continue ----------
        if (
            prevLine.startsWith("- ") &&
            !prevLine.startsWith("- [ ] ") &&
            !prevLine.startsWith("- [x] ") &&
            prevLine.length > 2
        ) {
            val promotedLine = "- [ ] " + prevLine.drop(2)

            val updatedText = buildString {
                append(newText.substring(0, prevLineStart))
                append(promotedLine)
                append('\n')
                append("- [ ] ")
                append(newText.substring(newCursor))
            }

            val newCursorPos =
                prevLineStart +
                        promotedLine.length +
                        1 + // newline
                        6 // "- [ ] "

            return TextFieldValue(
                text = updatedText,
                selection = TextRange(newCursorPos)
            )
        }

        // ---------- EXIT empty checklist ----------
        if (prevLine == "- [ ] " || prevLine == "- ") {
            return newValue
        }

        // ---------- CONTINUE checklist ----------
        if (
            prevLine.startsWith("- [ ] ") ||
            prevLine.startsWith("- [x] ")
        ) {
            val updatedText = buildString {
                append(newText.substring(0, newCursor))
                append("- [ ] ")
                append(newText.substring(newCursor))
            }

            return TextFieldValue(
                text = updatedText,
                selection = TextRange(newCursor + 6)
            )
        }

        // ---------- CONTINUE normal bullet ----------
        if (prevLine.startsWith("- ")) {
            val updatedText = buildString {
                append(newText.substring(0, newCursor))
                append("- ")
                append(newText.substring(newCursor))
            }

            return TextFieldValue(
                text = updatedText,
                selection = TextRange(newCursor + 2)
            )
        }
    }

    // ---------- BACKSPACE ----------
    val didDelete = newText.length < oldText.length
    if (didDelete) {
        val lineStart =
            newText.lastIndexOf('\n', (newCursor - 1).coerceAtLeast(0))
                .let { if (it == -1) 0 else it + 1 }

        val beforeCursor =
            if (lineStart <= newCursor)
                newText.substring(lineStart, newCursor)
            else ""

        if (beforeCursor == "-" || beforeCursor.isEmpty()) {
            val oldCursor = oldValue.selection.start.coerceIn(0, oldText.length)
            val oldLineStart =
                oldText.lastIndexOf('\n', (oldCursor - 1).coerceAtLeast(0))
                    .let { if (it == -1) 0 else it + 1 }

            val oldPrefix =
                oldText.substring(
                    oldLineStart,
                    (oldLineStart + 2).coerceAtMost(oldText.length)
                )

            if (oldPrefix == "- ") {
                var updated = newText

                if (lineStart < updated.length && updated[lineStart] == '-') {
                    updated = updated.removeRange(lineStart, lineStart + 1)
                }
                if (lineStart < updated.length && updated[lineStart] == ' ') {
                    updated = updated.removeRange(lineStart, lineStart + 1)
                }

                return TextFieldValue(
                    text = updated,
                    selection = TextRange(lineStart)
                )
            }
        }
    }

    return newValue
}
