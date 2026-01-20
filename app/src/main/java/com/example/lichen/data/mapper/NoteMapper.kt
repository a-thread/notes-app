package com.example.lichen.data.mapper

import com.example.lichen.data.local.entity.NoteEntity
import com.example.lichen.data.remote.supabase.SupabaseNoteDto
import com.example.lichen.domain.model.Note
import com.example.lichen.domain.model.NoteBody
import java.time.Instant
import java.util.UUID

/* ───────────────────────────────
 * Room → Domain
 * ─────────────────────────────── */

fun NoteEntity.toDomain(): Note =
    Note(
        id = UUID.fromString(id),
        userId = UUID.fromString(userId),
        title = title,
        body = NoteBody.Text(body),
        createdAt = Instant.ofEpochMilli(createdAt),
        createdBy = UUID.fromString(createdBy),
        updatedAt = Instant.ofEpochMilli(updatedAt),
        updatedBy = UUID.fromString(updatedBy),
        isPublic = isPublic
    )

/* ───────────────────────────────
 * Domain → Room
 * ─────────────────────────────── */

fun Note.toEntity(): NoteEntity =
    NoteEntity(
        id = id.toString(),
        userId = userId.toString(),
        title = title,
        body = (body as NoteBody.Text).text,
        createdAt = createdAt.toEpochMilli(),
        createdBy = createdBy.toString(),
        updatedAt = updatedAt.toEpochMilli(),
        updatedBy = updatedBy.toString(),
        isPublic = isPublic
    )

/* ───────────────────────────────
 * Supabase DTO → Room
 * ─────────────────────────────── */

fun SupabaseNoteDto.toEntity(): NoteEntity =
    NoteEntity(
        id = id,
        userId = user_id,
        title = title,
        body = body,
        createdAt = Instant.parse(created_at).toEpochMilli(),
        createdBy = created_by,
        updatedAt = Instant.parse(updated_at).toEpochMilli(),
        updatedBy = updated_by,
        isPublic = is_public
    )
