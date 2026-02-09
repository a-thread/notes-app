package com.athread.lichen.ui.notes.shared.model

enum class BlockTextStyle {
    NORMAL,
    H1,
    H2,
    H3,
}

data class SelectionFormatting(
    val bold: Boolean = false,
    val italic: Boolean = false,
    val code: Boolean = false,
    val textStyle: BlockTextStyle = BlockTextStyle.NORMAL,
    val bullet: Boolean = false,
    val checklist: Boolean = false
)
