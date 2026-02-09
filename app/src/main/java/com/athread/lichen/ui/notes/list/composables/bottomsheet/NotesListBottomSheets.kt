package com.athread.lichen.ui.notes.list.composables.bottomsheet

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.athread.lichen.domain.model.NotesSort
import com.athread.lichen.ui.list.SortBottomSheet
import com.athread.lichen.ui.notes.shared.theme.ThemeViewModel
import com.athread.lichen.ui.notes.shared.theme.isDarkTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesListBottomSheets(
    showMainSheet: Boolean,
    showSortSheet: Boolean,
    currentSort: NotesSort,
    themeViewModel: ThemeViewModel,
    onDismissMain: () -> Unit,
    onDismissSort: () -> Unit,
    onImport: () -> Unit,
    onExport: () -> Unit,
    onLogout: () -> Unit,
    onSortSelected: (NotesSort) -> Unit
) {
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    if (showMainSheet) {
        val isDarkMode = themeViewModel.isDarkTheme()

        ModalBottomSheet(
            onDismissRequest = onDismissMain,
            sheetState = bottomSheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 0.dp
        ) {
            BottomSheetContent(
                isDarkMode = isDarkMode,
                currentSort = currentSort,
                onToggleDarkMode = {
                    if (isDarkMode) themeViewModel.setLight()
                    else themeViewModel.setDark()
                    onDismissMain()
                },
                onImport = {
                    onDismissMain()
                    onImport()
                },
                onExport = {
                    onDismissMain()
                    onExport()
                },
                onOpenSort = {
                    onDismissMain()
                    onDismissSort() // ensures only one sheet at a time
                },
                onLogout = {
                    onDismissMain()
                    onLogout()
                }
            )
        }
    }

    if (showSortSheet) {
        ModalBottomSheet(
            onDismissRequest = onDismissSort,
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 0.dp
        ) {
            SortBottomSheet(
                currentSort = currentSort,
                onSortSelected = {
                    onSortSelected(it)
                    onDismissSort()
                },
                onDismiss = onDismissSort
            )
        }
    }
}
