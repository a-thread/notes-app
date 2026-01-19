package com.example.notes.ui.shared.model

import com.example.notes.ui.detail.model.ChecklistItem

sealed interface EditorBlock {

    data class TextBlock(
        val text: String,
        val startLine: Int,
        val endLine: Int
    ) : EditorBlock

    data class ChecklistBlock(
        val items: List<ChecklistItem>
    ) : EditorBlock
}