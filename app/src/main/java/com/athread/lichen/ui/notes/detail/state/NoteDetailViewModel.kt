package com.athread.lichen.ui.notes.detail.state

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.athread.lichen.domain.model.Note
import com.athread.lichen.domain.model.NoteBody
import com.athread.lichen.domain.repository.NoteRepository
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

    private var originalTitle: String = existingNote?.title.orEmpty()
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

    val hasUnsavedChanges: Boolean
        get() =
            _title.value != originalTitle ||
                    _text.value.text != originalText

    /* ───────────── Mode control ───────────── */

    fun enterEditMode() {
        _mode.value = EditorMode.Edit
    }

    fun exitEditMode() {
        _mode.value = EditorMode.ReadOnly
    }

    /* ───────────── Editing ───────────── */

    fun onTitleChange(value: String) {
        _title.value = value
    }

    fun onTextChange(value: TextFieldValue) {
        _text.value = value
    }

    fun toggleChecklistItem(lineIndex: Int) {
        val current = _text.value
        val lines = current.text.lines().toMutableList()

        if (lineIndex !in lines.indices) return

        lines[lineIndex] = when {
            lines[lineIndex].startsWith("- [x] ") ->
                lines[lineIndex].replaceFirst("- [x] ", "- [ ] ")

            lines[lineIndex].startsWith("- [ ] ") ->
                lines[lineIndex].replaceFirst("- [ ] ", "- [x] ")

            else -> lines[lineIndex]
        }

        _text.value = current.copy(text = lines.joinToString("\n"))
    }

    /* ───────────── Discard ───────────── */

    fun discardChanges(): Boolean {
        if (!hasUnsavedChanges) return true

        if (isNewNote) {
            // Leave editor entirely
            return true
        }

        _title.value = originalTitle
        _text.value = TextFieldValue(originalText)
        _mode.value = EditorMode.ReadOnly
        return false
    }

    /* ───────────── Save ───────────── */

    fun save() {
        viewModelScope.launch {
            val bodyText = _text.value.text
            persist(bodyText)

            commitSavedState()
            _mode.value = EditorMode.ReadOnly
        }
    }

    private fun commitSavedState() {
        originalTitle = _title.value
        originalText = _text.value.text
    }

    /* ───────────── Export ───────────── */

    suspend fun exportNote(): Pair<String, String> {
        return repository.exportNoteAsText(noteId)
    }

    private suspend fun persist(bodyText: String) {
        repository.saveNote(
            Note(
                id = noteId,
                userId = userId,
                title = _title.value.ifBlank { "Untitled" },
                body = NoteBody.Text(bodyText),
                createdAt = createdAt,
                createdBy = createdBy,
                updatedAt = Instant.now(),
                updatedBy = userId,
                isPublic = false
            )
        )
    }
}
