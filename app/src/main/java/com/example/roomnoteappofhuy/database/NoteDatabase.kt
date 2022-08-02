package com.example.roomnoteappofhuy.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.roomnoteappofhuy.DAO.NoteDAO
import com.example.roomnoteappofhuy.NoteConverters
import com.example.roomnoteappofhuy.model.Note

@Database(
    entities = [Note::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(NoteConverters::class)
abstract class NoteDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDAO

    companion object {
        @Volatile
        private var INSTANCE: NoteDatabase? = null

        fun getDatabase(context: Context): NoteDatabase {
            if (INSTANCE == null) {
                synchronized(this) {
                    // Pass the database to the INSTANCE
                    INSTANCE = buildDatabase(context)
                }
            }
            // Return database.
            return INSTANCE!!
        }

        private val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // The following query will add a new column called lastUpdate to the notes database
                database.execSQL("ALTER TABLE notes ADD COLUMN lastUpdate INTEGER NOT NULL DEFAULT 0")
            }
        }

        private fun buildDatabase(context: Context): NoteDatabase? {
            return Room.databaseBuilder(
                context.applicationContext,
                NoteDatabase::class.java,
                "notes"
            )
                //.addMigrations(MIGRATION_1_2)
                .build()
        }
    }

}