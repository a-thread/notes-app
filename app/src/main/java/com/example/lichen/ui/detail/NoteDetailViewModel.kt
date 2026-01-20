package com.example.lichen.ui.detail

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lichen.domain.model.Note
import com.example.lichen.domain.model.NoteBody
import com.example.lichen.domain.repository.NoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.UUID

enum class EditorMode { ReadOnly, Edit }

class NoteDetailViewModel(
    private val repository: NoteRepository,
    private val userId: UUID,
    existingNote: Note? = null
) : ViewModel() {

    val isNewNote: Boolean = existingNote == null

    private val noteId: UUID = existingNote?.id ?: UUID.randomUUID()

    private val originalTitle: String = existingNote?.title.orEmpty()
    private var originalText: String =
        (existingNote?.body as? NoteBody.Text)?.text.orEmpty()

    private val createdAt: Instant = existingNote?.createdAt ?: Instant.now()
    private val createdBy: UUID = existingNote?.createdBy ?: userId

    private val _mode = MutableStateFlow(
        if (isNewNote) EditorMode.Edit else EditorMode.ReadOnly
    )
    val mode: StateFlow<EditorMode> = _mode

    private val _title = MutableStateFlow(originalTitle)
    val title: StateFlow<String> = _title

    private val _text = MutableStateFlow(TextFieldValue(originalText))
    val text: StateFlow<TextFieldValue> = _text

    fun enterEditMode() {
        _mode.value = EditorMode.Edit
    }

    fun onTitleChange(value: String) {
        _title.value = value
    }

    fun onTextChange(value: TextFieldValue) {
        _text.value = value
    }

    fun discardChanges(): Boolean {
        if (isNewNote) {
            // New note → discard means leave entirely
            return true
        }

        // Existing note → revert changes and stay
        _title.value = originalTitle
        _text.value = TextFieldValue(originalText)
        _mode.value = EditorMode.ReadOnly
        return false
    }

    fun toggleChecklistItem(lineIndex: Int) {
        val current = _text.value
        val lines = current.text.lines().toMutableList()

        if (lineIndex !in lines.indices) return

        val line = lines[lineIndex]

        lines[lineIndex] = when {
            line.startsWith("- [x] ") ->
                line.replaceFirst("- [x] ", "- [ ] ")

            line.startsWith("- [ ] ") ->
                line.replaceFirst("- [ ] ", "- [x] ")

            else -> line
        }

        val updatedText = lines.joinToString("\n")

        _text.value = current.copy(text = updatedText)
        originalText = updatedText

        viewModelScope.launch {
            persist(updatedText)
        }
    }


    fun saveAndExit(onDone: () -> Unit) {
        viewModelScope.launch {
            persist(text.value.text)
            _mode.value = EditorMode.ReadOnly
            onDone()
        }
    }

    private suspend fun persist(bodyText: String) {
        repository.saveNote(
            Note(
                id = noteId,
                userId = userId,              // ✅ REAL USER
                title = title.value.ifBlank { "Untitled" },
                body = NoteBody.Text(bodyText),
                createdAt = createdAt,
                createdBy = createdBy,
                updatedAt = Instant.now(),
                updatedBy = userId,           // ✅ REAL USER
                isPublic = false
            )
        )
    }
}
