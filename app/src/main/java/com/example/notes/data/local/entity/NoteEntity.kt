package com.example.notes.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "note")
data class NoteEntity(
    @PrimaryKey
    val id: String,

    val userId: String,
    val title: String,

    // JSON stored as raw String
    val bodyJson: String,

    val createdAt: Long,
    val createdBy: String,

    val updatedAt: Long,
    val updatedBy: String,

    val isPublic: Boolean
)
