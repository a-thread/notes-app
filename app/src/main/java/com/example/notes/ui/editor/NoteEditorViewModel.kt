package com.example.notes.ui.editor

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.domain.model.Note
import com.example.notes.domain.model.NoteBody
import com.example.notes.domain.repository.NoteRepository
import kotlinx.coroutines.flow.*
import java.time.Instant
import java.util.UUID

class NoteEditorViewModel(
    private val repository: NoteRepository,
    private val existingNote: Note? = null
) : ViewModel() {

    private val noteId = existingNote?.id ?: UUID.randomUUID()
    private val userId = existingNote?.userId ?: UUID.randomUUID()

    private val _title = MutableStateFlow(existingNote?.title ?: "")
    val title: StateFlow<String> = _title

    private val _text = MutableStateFlow(
        TextFieldValue(
            (existingNote?.body as? NoteBody.Text)?.text.orEmpty()
        )
    )
    val text: StateFlow<TextFieldValue> = _text

    init {
        autosave()
    }

    fun onTitleChange(value: String) {
        _title.value = value
    }

    // 🔑 CHANGE: accept TextFieldValue
    fun onTextChange(value: TextFieldValue) {
        _text.value = value
    }

    private fun autosave() {
        combine(title, text) { t, b -> t to b.text }
            .debounce(400)
            .distinctUntilChanged()
            .onEach { saveInternal() }
            .launchIn(viewModelScope)
    }

    private suspend fun saveInternal() {
        val now = Instant.now()

        repository.saveNote(
            Note(
                id = noteId,
                userId = userId,
                title = title.value.ifBlank { "Untitled" },
                body = NoteBody.Text(text.value.text),
                createdAt = existingNote?.createdAt ?: now,
                createdBy = existingNote?.createdBy ?: userId,
                updatedAt = now,
                updatedBy = userId,
                isPublic = false
            )
        )
    }
}
