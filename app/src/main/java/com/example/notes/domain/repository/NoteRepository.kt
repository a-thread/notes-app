package com.example.notes.domain.repository

import com.example.notes.domain.model.Note
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface NoteRepository {
    fun observeNotes(): Flow<List<Note>>

    suspend fun syncFromRemote()

    suspend fun getNoteById(id: UUID): Note?

    suspend fun saveNote(note: Note)

    suspend fun deleteNote(id: UUID)
}