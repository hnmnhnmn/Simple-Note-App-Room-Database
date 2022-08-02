package com.example.roomnoteappofhuy.DAO

import androidx.room.*
import com.example.roomnoteappofhuy.model.Note
import kotlinx.coroutines.flow.Flow
@Dao
interface NoteDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addNote(note: Note)

    @Query("SELECT * FROM notes ORDER BY dateAdded DESC")
    fun getNotes(): Flow<List<Note>>

    @Update
    suspend fun updateNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)





}