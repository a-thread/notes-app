package com.athread.lichen.domain.repository

import com.athread.lichen.domain.model.Note
import com.athread.lichen.domain.model.NotesSort
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface NoteRepository {
    fun observeNotes(sort: NotesSort): Flow<List<Note>>

    suspend fun syncFromRemote()

    suspend fun getNoteById(id: UUID): Note?

    suspend fun saveNote(note: Note)

    suspend fun deleteNote(id: UUID)

    suspend fun exportNoteAsText(id: UUID): Pair<String, String>

    suspend fun exportNotesAsText(): String

    suspend fun importNoteFromTextFile(
        fileName: String,
        content: String
    )
}