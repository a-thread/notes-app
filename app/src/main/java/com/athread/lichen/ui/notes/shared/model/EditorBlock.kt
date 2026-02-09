package com.athread.lichen.ui.notes.shared.model

sealed interface EditorBlock {

    val startLine: Int
    val endLine: Int

    data class TextBlock(
        val text: String,
        override val startLine: Int,
        override val endLine: Int
    ) : EditorBlock

    data class HeadingBlock(
        val level: Int,
        val text: String,
        override val startLine: Int,
        override val endLine: Int
    ) : EditorBlock

    data class BulletListBlock(
        val items: List<String>,
        override val startLine: Int,
        override val endLine: Int
    ) : EditorBlock

    data class ChecklistBlock(
        val items: List<ChecklistItem>,
        override val startLine: Int,
        override val endLine: Int
    ) : EditorBlock

    data class DividerBlock(
        override val startLine: Int,
        override val endLine: Int
    ) : EditorBlock
}
