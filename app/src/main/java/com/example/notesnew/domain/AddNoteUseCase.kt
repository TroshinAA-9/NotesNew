package com.example.notesnew.domain

class AddNoteUseCase(
    private val repository: NotesRepository
) {

    operator fun invoke(note: Note) {
        repository.addNote(note)
    }
}