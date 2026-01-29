package com.athread.lichen.data.repository

import kotlinx.coroutines.withContext
import android.util.Log
import com.athread.lichen.data.local.dao.NoteDao
import com.athread.lichen.data.mapper.toDomain
import com.athread.lichen.data.mapper.toEntity
import com.athread.lichen.data.remote.supabase.SupabaseNoteRemoteDataSource
import com.athread.lichen.domain.model.Note
import com.athread.lichen.domain.model.NoteBody
import com.athread.lichen.domain.repository.NoteRepository
import com.athread.lichen.domain.repository.AuthRepository
import com.athread.lichen.domain.model.NotesSort
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
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


    override fun observeNotes(sort: NotesSort): Flow<List<Note>> =
        flow {
            val userId = requireUserId().toString()

            emitAll(
                when (sort) {
                    NotesSort.DATE_NEWEST ->
                        noteDao.observeNewest(userId)

                    NotesSort.DATE_OLDEST ->
                        noteDao.observeOldest(userId)

                    NotesSort.TITLE_ASC ->
                        noteDao.observeTitleAsc(userId)

                    NotesSort.TITLE_DESC ->
                        noteDao.observeTitleDesc(userId)
                }.map { entities ->
                    entities.map { it.toDomain() }
                }
            )
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

    override suspend fun exportNoteAsText(
        id: UUID
    ): Pair<String, String> {
        val note = noteDao.getById(id.toString())
            ?.toDomain()
            ?: error("Note not found")

        val title = note.title.ifBlank { "Untitled" }
        val body = (note.body as NoteBody.Text).text

        val safeFileName =
            title.replace(Regex("""[\\/:*?"<>|]"""), "_")

        return "$safeFileName.txt" to body
    }


    override suspend fun exportNotesAsText(): String {
        val userId = requireUserId().toString()
        val notes = noteDao.getAllForUser(userId)
            .map { it.toDomain() }

        val builder = StringBuilder()

        builder.appendLine("# Lichen Notes Export")
        builder.appendLine("# version: 1")
        builder.appendLine()

        notes.forEach { note ->
            builder.appendLine("---")
            builder.appendLine("id: ${note.id}")
            builder.appendLine("createdAt: ${note.createdAt}")
            builder.appendLine("updatedAt: ${note.updatedAt}")
            builder.appendLine("---")
            builder.appendLine()
            builder.appendLine("# ${note.title}")
            builder.appendLine(
                (note.body as NoteBody.Text).text
            )
            builder.appendLine()
        }

        return builder.toString()
    }

    override suspend fun importNoteFromTextFile(
        fileName: String,
        content: String
    ) {
        val userId = requireUserId()
        val now = Instant.now()

        val title = fileName
            .removeSuffix(".txt")
            .ifBlank { "Untitled" }

        val note = Note(
            id = UUID.randomUUID(),
            userId = userId,
            title = title,
            body = NoteBody.Text(content.trim()),
            createdAt = now,
            createdBy = userId,
            updatedAt = now,
            updatedBy = userId,
            isPublic = false
        )

        noteDao.upsert(note.toEntity())

        externalScope.launch {
            try {
                remote.upsertNote(note)
            } catch (e: Exception) {
                Log.e("NoteRepository", "Remote import failed", e)
            }
        }
    }
}
