package com.example.notes.ui.editor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditorScreen(
    viewModel: NoteEditorViewModel,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    val title by viewModel.title.collectAsState()
    val text by viewModel.text.collectAsState()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onDone) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxSize()
        ) {

            // ---------- Title ----------
            BasicTextField(
                value = title,
                onValueChange = viewModel::onTitleChange,
                textStyle = TextStyle(
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onBackground
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                decorationBox = { inner ->
                    if (title.isEmpty()) {
                        Text(
                            "Title",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    inner()
                }
            )

            // ---------- Body ----------
            BasicTextField(
                value = text,
                onValueChange = { newValue ->
                    viewModel.onTextChange(
                        handleListEditing(
                            oldValue = text,
                            newValue = newValue
                        )
                    )
                },
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    lineHeight = 22.sp,
                    color = MaterialTheme.colorScheme.onBackground
                ),
                modifier = Modifier.fillMaxSize(),
                decorationBox = { inner ->
                    if (text.text.isEmpty()) {
                        Text(
                            "Start typing…",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    inner()
                }
            )
        }
    }
}

private fun handleListEditing(
    oldValue: TextFieldValue,
    newValue: TextFieldValue
): TextFieldValue {
    val oldText = oldValue.text
    val newText = newValue.text
    val newCursor = newValue.selection.start.coerceIn(0, newText.length)

    // ---------- ENTER (IME-safe): detect newline inserted right before cursor ----------
    val insertedNewline =
        newText.length > oldText.length &&
                newCursor > 0 &&
                newText.getOrNull(newCursor - 1) == '\n'

    if (insertedNewline) {
        val prevLineStart = newText.lastIndexOf('\n', (newCursor - 2).coerceAtLeast(0))
            .let { if (it == -1) 0 else it + 1 }

        val prevLineEnd = (newCursor - 1).coerceAtLeast(prevLineStart)
        val prevLine = newText.substring(prevLineStart, prevLineEnd)

        // If previous line is exactly "- ", user wants to exit list
        if (prevLine == "- ") return newValue

        // If previous line starts with "- ", continue list
        if (prevLine.startsWith("- ")) {
            val updatedText = buildString {
                append(newText.substring(0, newCursor))
                append("- ")
                append(newText.substring(newCursor))
            }
            val updatedCursor = (newCursor + 2).coerceIn(0, updatedText.length)

            return TextFieldValue(
                text = updatedText,
                selection = TextRange(updatedCursor)
            )
        }

        return newValue
    }

    // ---------- BACKSPACE (IME-safe): detect deletion ----------
    val didDelete = newText.length < oldText.length
    if (didDelete) {
        // Determine current line start in the *new* text
        val lineStart = newText.lastIndexOf('\n', (newCursor - 1).coerceAtLeast(0))
            .let { if (it == -1) 0 else it + 1 }

        // Text from start of line to cursor
        val beforeCursorOnLine = newText.substring(lineStart, newCursor)

        // We only care when user is basically deleting the bullet prefix.
        // Typical sequence:
        // old: "- " (cursor after space)
        // backspace -> new: "-" (cursor after '-')
        // backspace -> new: "" (cursor at line start)
        if (beforeCursorOnLine == "-" || beforeCursorOnLine.isEmpty()) {
            // Look at what the old line prefix was (safe)
            val oldCursor = oldValue.selection.start.coerceIn(0, oldText.length)
            val oldLineStart = oldText.lastIndexOf('\n', (oldCursor - 1).coerceAtLeast(0))
                .let { if (it == -1) 0 else it + 1 }

            val oldPrefixEnd = (oldLineStart + 2).coerceAtMost(oldText.length)
            val oldPrefix = if (oldPrefixEnd >= oldLineStart) oldText.substring(oldLineStart, oldPrefixEnd) else ""

            // If the old line started with "- ", collapse the bullet entirely
            if (oldPrefix == "- ") {
                var updated = newText

                // Remove '-' if present at line start
                if (lineStart < updated.length && updated[lineStart] == '-') {
                    updated = updated.removeRange(lineStart, lineStart + 1)
                }
                // Remove following space if present
                if (lineStart < updated.length && updated[lineStart] == ' ') {
                    updated = updated.removeRange(lineStart, lineStart + 1)
                }

                val updatedCursor = lineStart.coerceIn(0, updated.length)
                return TextFieldValue(
                    text = updated,
                    selection = TextRange(updatedCursor)
                )
            }
        }
    }

    return newValue
}
