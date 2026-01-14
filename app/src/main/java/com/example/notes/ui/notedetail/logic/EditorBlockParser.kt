package com.example.notes.ui.notedetail.logic

import androidx.compose.ui.text.input.TextFieldValue
import com.example.notes.ui.shared.model.EditorBlock
import com.example.notes.ui.notedetail.model.ChecklistItem
import kotlin.collections.plusAssign

fun TextFieldValue.parseEditorBlocks(): List<EditorBlock> {
    val lines = text.lines()
    val blocks = mutableListOf<EditorBlock>()

    var index = 0

    while (index < lines.size) {
        val line = lines[index]

        // Checklist block
        if (line.startsWith("- [")) {
            val items = mutableListOf<ChecklistItem>()

            while (index < lines.size && lines[index].startsWith("- [")) {
                val checked = lines[index].startsWith("- [x]")
                val itemText = lines[index].drop(6)
                items += ChecklistItem(
                    text = itemText,
                    checked = checked,
                    lineIndex = index
                )
                index++
            }

            blocks += EditorBlock.ChecklistBlock(items)
            continue
        }

        // Text block
        val start = index
        val buffer = StringBuilder()

        while (index < lines.size && !lines[index].startsWith("- [")) {
            buffer.appendLine(lines[index])
            index++
        }

        blocks += EditorBlock.TextBlock(
            text = buffer.toString().trimEnd(),
            startLine = start,
            endLine = index - 1
        )
    }

    return blocks
}
