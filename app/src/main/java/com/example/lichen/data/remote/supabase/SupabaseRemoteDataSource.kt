package com.example.lichen.data.remote.supabase

import com.example.lichen.domain.model.Note
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import java.util.UUID

class SupabaseNoteRemoteDataSource(
    private val client: SupabaseClient
) {

    suspend fun fetchNotes(userId: UUID): List<SupabaseNoteDto> =
        client.from("note")
            .select {
                filter {
                    eq("user_id", userId.toString())
                }
            }
            .decodeList()

    suspend fun upsertNote(note: Note) {
        client.from("note")
            .upsert(note.toSupabaseDto())
    }

    suspend fun deleteNote(id: UUID) {
        client.from("note")
            .delete {
                filter {
                    eq("id", id.toString())
                }
            }
    }
}
