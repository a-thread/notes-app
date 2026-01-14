package com.example.notes.ui.notedetail

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.domain.model.Note
import com.example.notes.domain.model.NoteBody
import com.example.notes.domain.repository.NoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.UUID

enum class EditorMode { ReadOnly, Edit }

class NoteDetailViewModel(
    private val repository: NoteRepository,
    private val existingNote: Note? = null
) : ViewModel() {

    val isNewNote: Boolean = existingNote == null
    private val noteId = existingNote?.id ?: UUID.randomUUID()
    private val userId = existingNote?.userId ?: UUID.randomUUID()
    private val originalTitle = existingNote?.title ?: ""
    private var originalText = (existingNote?.body as? NoteBody.Text)?.text ?: ""
    private val _editSession = MutableStateFlow(0)

    private val _mode = MutableStateFlow(
        if (existingNote == null) EditorMode.Edit else EditorMode.ReadOnly
    )
    val mode: StateFlow<EditorMode> = _mode

    private val _title = MutableStateFlow(originalTitle)
    val title: StateFlow<String> = _title

    private val _text = MutableStateFlow(TextFieldValue(originalText))
    val text: StateFlow<TextFieldValue> = _text

    fun enterEditMode() {
        _mode.value = EditorMode.Edit
        _editSession.value++
    }

    fun onTitleChange(value: String) {
        _title.value = value
    }

    fun onTextChange(value: TextFieldValue) {
        _text.value = value
    }

    fun discardChanges() {
        _title.value = originalTitle
        _text.value = TextFieldValue(originalText)
        _mode.value = EditorMode.ReadOnly
    }

    fun toggleChecklistItem(lineIndex: Int) {
        val current = _text.value
        val lines = current.text.lines().toMutableList()
        if (lineIndex !in lines.indices) return

        val line = lines[lineIndex]
        lines[lineIndex] = when {
            line.startsWith("- [x] ") -> line.replaceFirst("- [x] ", "- [ ] ")
            line.startsWith("- [ ] ") -> line.replaceFirst("- [ ] ", "- [x] ")
            else -> line
        }

        val updatedText = lines.joinToString("\n")

        _text.value = current.copy(text = updatedText)

        originalText = updatedText

        viewModelScope.launch {
            persistChecklistToggle(updatedText)
        }
    }

    private suspend fun persistChecklistToggle(updatedText: String) {
        val now = Instant.now()

        repository.saveNote(
            Note(
                id = noteId,
                userId = userId,
                title = title.value.ifBlank { "Untitled" },
                body = NoteBody.Text(updatedText),
                createdAt = existingNote?.createdAt ?: now,
                createdBy = existingNote?.createdBy ?: userId,
                updatedAt = now,
                updatedBy = userId,
                isPublic = false
            )
        )
    }


    fun saveAndExit(onDone: () -> Unit) {
        viewModelScope.launch {
            saveInternal()
            _mode.value = EditorMode.ReadOnly
            onDone()
        }
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
