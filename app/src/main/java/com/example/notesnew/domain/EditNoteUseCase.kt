package com.example.notesnew.domain

class EditNoteUseCase(
    private val repository: NotesRepository
) {

    operator fun invoke(noteId: Int) {
        repository.editNote(noteId)
    }
}