package com.example.notes.ui.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.domain.model.Note
import com.example.notes.domain.model.NoteBody
import com.example.notes.domain.model.NoteType
import com.example.notes.domain.repository.NoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.UUID

class NoteEditorViewModel(
    private val repository: NoteRepository,
    private val existingNote: Note? = null
) : ViewModel() {

    private val noteId: UUID = existingNote?.id ?: UUID.randomUUID()
    private val userId: UUID = existingNote?.userId ?: UUID.randomUUID()

    private val _title = MutableStateFlow(existingNote?.title ?: "")
    val title: StateFlow<String> = _title

    private val _type = MutableStateFlow(existingNote?.type ?: NoteType.TEXT)
    val type: StateFlow<NoteType> = _type

    private val _body = MutableStateFlow<NoteBody>(
        existingNote?.body ?: NoteBody.Text("")
    )
    val body: StateFlow<NoteBody> = _body

    fun onTitleChange(newTitle: String) {
        _title.value = newTitle
    }

    fun onTextChange(text: String) {
        _body.value = NoteBody.Text(text)
        _type.value = NoteType.TEXT
    }

    fun onBulletedListChange(items: List<String>) {
        _body.value = NoteBody.BulletedList(items)
        _type.value = NoteType.BULLETED_LIST
    }

    fun saveNote() {
        viewModelScope.launch {
            val now = Instant.now()

            val note = Note(
                id = noteId,
                userId = userId,
                title = title.value.ifBlank { "Untitled" },
                type = type.value,
                body = body.value,
                createdAt = existingNote?.createdAt ?: now,
                createdBy = existingNote?.createdBy ?: userId,
                updatedAt = now,
                updatedBy = userId,
                isPublic = false
            )

            repository.saveNote(note)
        }
    }
}
