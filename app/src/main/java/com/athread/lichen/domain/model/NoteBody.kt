package com.athread.lichen.domain.model

import kotlinx.serialization.Serializable

@Serializable
sealed class NoteBody {
    @Serializable
    data class Text(val text: String) : NoteBody()
}
