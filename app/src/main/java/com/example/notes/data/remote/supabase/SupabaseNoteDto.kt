package com.example.notes.data.remote.supabase

import kotlinx.serialization.Serializable

@Suppress("PropertyName")
@Serializable
data class SupabaseNoteDto(
    val id: String,
    val user_id: String,
    val title: String,
    val body: String,
    val created_at: String,
    val created_by: String,
    val updated_at: String,
    val updated_by: String,
    val is_public: Boolean
)
