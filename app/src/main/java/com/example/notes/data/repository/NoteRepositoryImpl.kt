package com.example.notes.data.repository

import com.example.notes.data.local.dao.NoteDao
import com.example.notes.data.mapper.toDomain
import com.example.notes.data.mapper.toEntity
import com.example.notes.domain.model.Note
import com.example.notes.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class NoteRepositoryImpl(
    private val noteDao: NoteDao
) : NoteRepository {

    override fun observeNotes(): Flow<List<Note>> {
        return noteDao.observeAll()
            .map { entities ->
                entities.map { it.toDomain() }
            }
    }

    override suspend fun getNoteById(id: UUID): Note? {
        return noteDao.getById(id.toString())
            ?.toDomain()
    }

    override suspend fun saveNote(note: Note) {
        noteDao.upsert(note.toEntity())
    }

    override suspend fun deleteNote(id: UUID) {
        noteDao.deleteById(id.toString())
    }
}