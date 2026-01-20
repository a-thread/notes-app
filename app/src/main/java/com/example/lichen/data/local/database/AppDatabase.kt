package com.example.lichen.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.lichen.data.local.dao.NoteDao
import com.example.lichen.data.local.entity.NoteEntity

@Database(
    entities = [NoteEntity::class],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
}
