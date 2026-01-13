package com.example.notes.ui.util

fun String.isListLike(): Boolean {
    val lines = lines()
    if (lines.size < 2) return false

    return lines.count { it.trim().startsWith("-") } >= 2
}

fun String.extractListItems(): List<String> =
    lines()
        .map { it.trim() }
        .filter { it.startsWith("-") }
        .map { it.removePrefix("-").trim() }
