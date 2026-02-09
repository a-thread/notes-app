package com.athread.lichen.ui.notes.shared.util

import androidx.compose.ui.text.input.TextFieldValue
import com.athread.lichen.ui.notes.shared.model.ChecklistItem
import com.athread.lichen.ui.notes.shared.model.EditorBlock
import kotlin.collections.plusAssign

private const val TAB_WIDTH = 4

private fun String.normalizedIndent(): String {
    val rawIndent = takeWhile { it == ' ' || it == '\t' }
    return buildString {
        rawIndent.forEach { ch ->
            if (ch == '\t') repeat(TAB_WIDTH) { append(' ') }
            else append(' ')
        }
    }
}

fun TextFieldValue.parseEditorBlocks(): List<EditorBlock> {
    val lines = text.lines()
    val blocks = mutableListOf<EditorBlock>()

    var index = 0

    while (index < lines.size) {
        val line = lines[index]

        /* ---------- DIVIDER ---------- */

        if (line.trim() == "---") {
            blocks += EditorBlock.DividerBlock(
                startLine = index,
                endLine = index
            )
            index++
            continue
        }

        /* ---------- HEADING ---------- */

        if (line.trimStart().startsWith("#")) {
            val trimmed = line.trimStart()
            val level = trimmed.takeWhile { it == '#' }.length
            val headingText = trimmed.drop(level).trimStart()

            blocks += EditorBlock.HeadingBlock(
                level = level.coerceAtMost(3),
                text = headingText,
                startLine = index,
                endLine = index
            )
            index++
            continue
        }

        /* ---------- CHECKLIST (NEST + TAB SAFE) ---------- */

        if (line.trimStart().startsWith("- [")) {
            val items = mutableListOf<ChecklistItem>()
            val start = index

            while (index < lines.size && lines[index].trimStart().startsWith("- [")) {
                val rawLine = lines[index]
                val indent = rawLine.normalizedIndent()
                val trimmed = rawLine.trimStart()

                val checked = trimmed.startsWith("- [x]")
                val content =
                    trimmed
                        .removePrefix("- [x] ")
                        .removePrefix("- [ ] ")

                items += ChecklistItem(
                    text = indent + content,
                    checked = checked,
                    lineIndex = index
                )

                index++
            }

            blocks += EditorBlock.ChecklistBlock(
                items = items,
                startLine = start,
                endLine = index - 1
            )
            continue
        }

        /* ---------- BULLET LIST (NEST-AWARE + TAB SAFE) ---------- */

        if (line.trimStart().startsWith("- ")) {
            val items = mutableListOf<String>()
            val start = index

            val baseIndent = line.normalizedIndent().length

            while (index < lines.size) {
                val rawLine = lines[index]

                if (!rawLine.trimStart().startsWith("- ")) break

                val indent = rawLine.normalizedIndent().length
                if (indent < baseIndent) break

                val trimmed = rawLine.trimStart().removePrefix("- ")
                items += " ".repeat(indent) + trimmed

                index++
            }

            blocks += EditorBlock.BulletListBlock(
                items = items,
                startLine = start,
                endLine = index - 1
            )
            continue
        }

        /* ---------- TEXT BLOCK ---------- */

        val start = index
        val buffer = StringBuilder()

        while (
            index < lines.size &&
            lines[index].isNotBlank() &&
            !lines[index].trimStart().startsWith("- [") &&
            !lines[index].trimStart().startsWith("- ") &&
            !lines[index].trimStart().startsWith("#") &&
            lines[index].trim() != "---"
        ) {
            buffer.appendLine(lines[index])
            index++
        }

        val textBlock = buffer.toString().trimEnd()

        if (textBlock.isNotBlank()) {
            blocks += EditorBlock.TextBlock(
                text = textBlock,
                startLine = start,
                endLine = index - 1
            )
        } else {
            index++
        }
    }

    return blocks
}
