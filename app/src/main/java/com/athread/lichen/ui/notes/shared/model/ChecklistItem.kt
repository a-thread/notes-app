package com.athread.lichen.ui.notes.shared.model

data class ChecklistItem(
    val text: String,
    val checked: Boolean,
    val lineIndex: Int
)