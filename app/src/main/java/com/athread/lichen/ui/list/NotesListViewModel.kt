package com.athread.lichen.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.athread.lichen.domain.model.Note
import com.athread.lichen.domain.repository.NoteRepository
import com.athread.lichen.domain.model.NotesSort
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield

enum class NotesLayoutMode {
    GRID,
    LIST
}

sealed interface NotesUiState {
    data object Loading : NotesUiState
    data object Empty : NotesUiState
    data class Content(val notes: List<Note>) : NotesUiState
}

class NotesListViewModel(
    private val repository: NoteRepository
) : ViewModel() {

    private val isSyncing = MutableStateFlow(true)
    val sort = MutableStateFlow(NotesSort.DATE_NEWEST)

    fun setSort(sort: NotesSort) {
        this.sort.value = sort
    }
    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<NotesUiState> =
        combine(
            sort.flatMapLatest { sort ->
                repository.observeNotes(sort)
            },
            isSyncing
        ) { notes, syncing ->
            when {
                notes.isNotEmpty() -> NotesUiState.Content(notes)
                syncing -> NotesUiState.Loading
                else -> NotesUiState.Empty
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = NotesUiState.Loading
        )

    val layoutMode = MutableStateFlow(NotesLayoutMode.GRID)

    private var recentlyDeletedNote: Note? = null

    init {
        viewModelScope.launch {
            // allow first frame
            yield()
            try {
                repository.syncFromRemote()
            } finally {
                isSyncing.value = false
            }
        }
    }

    fun toggleLayout() {
        layoutMode.value =
            if (layoutMode.value == NotesLayoutMode.GRID)
                NotesLayoutMode.LIST
            else
                NotesLayoutMode.GRID
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            recentlyDeletedNote = note
            repository.deleteNote(note.id)
        }
    }

    fun undoDelete() {
        recentlyDeletedNote?.let { note ->
            viewModelScope.launch {
                repository.saveNote(note)
                recentlyDeletedNote = null
            }
        }
    }

    fun importTextFile(
        fileName: String,
        content: String
    ) {
        viewModelScope.launch {
            repository.importNoteFromTextFile(fileName, content)
        }
    }

    suspend fun exportAllNotes(): String =
        repository.exportNotesAsText()
}
