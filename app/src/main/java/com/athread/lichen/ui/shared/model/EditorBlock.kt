package com.athread.lichen.ui.shared.model

import com.athread.lichen.ui.detail.model.ChecklistItem

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