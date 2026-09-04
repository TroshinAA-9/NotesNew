package com.example.notesnew.domain

import kotlinx.coroutines.flow.Flow

interface NotesRepository {

    fun addNote(title: String, content: String)

    fun deleteNote(noteId: Int)

    fun editNote(noteId: Int)

    fun getAllNotes(): Flow<List<Note>>

    fun getNote(noteId: Int): Note

    fun searchNotes(query: String): Flow<List<Note>>

    fun switchPinnedStatus(noteId: Int)


}