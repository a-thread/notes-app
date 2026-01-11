package com.example.notes.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class NoteBody {

    @Serializable
    @SerialName("TEXT")
    data class Text(
        val text: String
    ) : NoteBody()

    @Serializable
    @SerialName("BULLETED_LIST")
    data class BulletedList(
        val items: List<String>
    ) : NoteBody()
}
