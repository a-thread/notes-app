package com.athread.lichen.data.remote.supabase

import com.athread.lichen.domain.model.Note
import com.athread.lichen.domain.model.NoteBody

fun Note.toSupabaseDto(): SupabaseNoteDto =
    SupabaseNoteDto(
        id = id.toString(),
        user_id = userId.toString(),
        title = title,
        body = (body as NoteBody.Text).text,
        created_at = createdAt.toString(),
        created_by = createdBy.toString(),
        updated_at = updatedAt.toString(),
        updated_by = updatedBy.toString(),
        is_public = isPublic
    )
