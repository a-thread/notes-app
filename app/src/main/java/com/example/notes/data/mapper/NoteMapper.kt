package com.example.notes.data.mapper

import com.example.notes.data.local.entity.NoteEntity
import com.example.notes.domain.model.*
import kotlinx.serialization.json.Json
import java.time.Instant
import java.util.UUID

private val json = Json {
    ignoreUnknownKeys = true
    classDiscriminator = "type"
}

fun NoteEntity.toDomain(): Note {
    return Note(
        id = UUID.fromString(id),
        userId = UUID.fromString(userId),
        title = title,
        type = NoteType.valueOf(type),
        body = json.decodeFromString(NoteBody.serializer(), bodyJson),

        createdAt = Instant.ofEpochMilli(createdAt),
        createdBy = UUID.fromString(createdBy),

        updatedAt = Instant.ofEpochMilli(updatedAt),
        updatedBy = UUID.fromString(updatedBy),

        isPublic = isPublic
    )
}

fun Note.toEntity(): NoteEntity {
    return NoteEntity(
        id = id.toString(),
        userId = userId.toString(),
        title = title,
        type = type.name,
        bodyJson = json.encodeToString(NoteBody.serializer(), body),

        createdAt = createdAt.toEpochMilli(),
        createdBy = createdBy.toString(),

        updatedAt = updatedAt.toEpochMilli(),
        updatedBy = updatedBy.toString(),

        isPublic = isPublic
    )
}
