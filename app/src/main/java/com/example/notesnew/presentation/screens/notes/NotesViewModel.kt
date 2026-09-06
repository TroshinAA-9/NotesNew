package com.example.notesnew.presentation.screens.notes

import androidx.lifecycle.ViewModel
import com.example.notesnew.data.NotesRepositoryImpl
import com.example.notesnew.domain.AddNoteUseCase
import com.example.notesnew.domain.DeleteNoteUseCase
import com.example.notesnew.domain.EditNoteUseCase
import com.example.notesnew.domain.GetAllNotesUseCase
import com.example.notesnew.domain.GetNoteUseCase
import com.example.notesnew.domain.Note
import com.example.notesnew.domain.SearchNoteUseCase
import com.example.notesnew.domain.SwitchPinnedStatusUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class NotesViewModel : ViewModel() {

    val repository = NotesRepositoryImpl

    private val addNoteUseCase = AddNoteUseCase(repository)
    private val deleteNoteUseCase = DeleteNoteUseCase(repository)
    private val editNoteUseCase = EditNoteUseCase(repository)
    private val getNoteUseCase = GetNoteUseCase(repository)
    private val getAllNoteUseCase = GetAllNotesUseCase(repository)
    private val searchNoteUseCase = SearchNoteUseCase(repository)
    private val switchPinnedStatusUseCase = SwitchPinnedStatusUseCase(repository)

    private val _state = MutableStateFlow(NotesScreenState())
    val state = _state.asStateFlow()

    private val query = MutableStateFlow("")
    private val scope = CoroutineScope(Dispatchers.IO)


    init {
        @OptIn(ExperimentalCoroutinesApi::class)
        query.flatMapLatest { searchQuery ->
            if (searchQuery.isBlank()) {
                getAllNoteUseCase()
            } else {
                searchNoteUseCase(searchQuery)
            }
        }
            .onEach { notes ->
                val pinnedNotes = notes.filter { it.isPinned }
                val otherNotes = notes.filter { !it.isPinned }
                _state.update { it.copy(pinnedNotes = pinnedNotes, otherNotes = otherNotes) }
            }
            .launchIn(scope)
    }

    fun processCommand(command: NotesCommand) {
        when (command) {
            is NotesCommand.DeleteNote -> {
                deleteNoteUseCase(command.noteId)
            }

            is NotesCommand.EditNote -> {
                val title = command.note.title
                editNoteUseCase(command.note.copy(title = "$title is edited!"))
            }

            is NotesCommand.InputSearchQuery -> {
                query.value = command.query
            }

            is NotesCommand.SwitchPinnedStatus -> {
                switchPinnedStatusUseCase(command.noteId)
            }
        }
    }
}

sealed interface NotesCommand {
    data class InputSearchQuery(val query: String) : NotesCommand

    data class SwitchPinnedStatus(val noteId: Int) : NotesCommand

    data class DeleteNote(val noteId: Int) : NotesCommand

    data class EditNote(val note: Note) : NotesCommand
}

data class NotesScreenState(
    val query: String = "",
    val pinnedNotes: List<Note> = listOf(),
    val otherNotes: List<Note> = listOf(),
)