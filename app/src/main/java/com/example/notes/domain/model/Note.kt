package com.example.notes.domain.model

import java.time.Instant
import java.util.UUID

data class Note(
    val id: UUID,
    val userId: UUID,
    val title: String,
    val body: NoteBody,

    val createdAt: Instant,
    val createdBy: UUID,

    val updatedAt: Instant,
    val updatedBy: UUID,

    val isPublic: Boolean
)
