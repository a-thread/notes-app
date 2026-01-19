package com.example.notes.data.repository

import kotlinx.coroutines.withContext
import android.util.Log
import com.example.notes.data.local.dao.NoteDao
import com.example.notes.data.mapper.toDomain
import com.example.notes.data.mapper.toEntity
import com.example.notes.data.remote.supabase.SupabaseNoteRemoteDataSource
import com.example.notes.domain.model.Note
import com.example.notes.domain.repository.NoteRepository
import com.example.notes.domain.repository.AuthRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.UUID

class NoteRepositoryImpl(
    private val noteDao: NoteDao,
    private val remote: SupabaseNoteRemoteDataSource,
    private val authRepository: AuthRepository,
    private val externalScope: CoroutineScope
) : NoteRepository {

    /**
     * Centralized guard — keeps auth rules consistent
     */
    private suspend fun requireUserId(): UUID =
        authRepository.userId.first()
            ?: error("No authenticated user")


    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeNotes(): Flow<List<Note>> =
        authRepository.userId.flatMapLatest { userId ->
            if (userId == null) {
                flowOf(emptyList())
            } else {
                noteDao.observeByUserId(userId.toString())
                    .map { it.map { entity -> entity.toDomain() } }
            }
        }

    override suspend fun getNoteById(id: UUID): Note? =
        noteDao.getById(id.toString())?.toDomain()

    override suspend fun saveNote(note: Note) {
        val userId = requireUserId()
        val now = Instant.now()

        val safeNote = note.copy(
            userId = userId,
            createdBy = note.createdBy.takeIf { it != UUID(0, 0) } ?: userId,
            updatedBy = userId,
            updatedAt = now
        )

        noteDao.upsert(safeNote.toEntity())

        externalScope.launch {
            try {
                remote.upsertNote(safeNote)
            } catch (e: Exception) {
                Log.e("NoteRepository", "Remote save failed", e)
            }
        }
    }


    override suspend fun deleteNote(id: UUID) {
        requireUserId()

        noteDao.deleteById(id.toString())

        externalScope.launch {
            try {
                remote.deleteNote(id)
            } catch (e: Exception) {
                Log.e("NoteRepository", "Remote delete failed", e)
            }
        }
    }

    override suspend fun syncFromRemote() {
        val userId = requireUserId()

        withContext(Dispatchers.IO) {
            val remoteNotes = remote.fetchNotes(userId)

            // Upsert first (UI fills in immediately)
            remoteNotes.forEach {
                noteDao.upsert(it.toEntity())
            }

            // Then clean up missing notes (optional)
            val remoteIds = remoteNotes.map { it.id }.toSet()
            noteDao.deleteNotInIds(userId.toString(), remoteIds)
        }
    }
}
