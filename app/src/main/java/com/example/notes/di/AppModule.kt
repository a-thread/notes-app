package com.example.notes.di

import android.content.Context
import androidx.room.Room
import com.example.notes.data.local.database.AppDatabase
import com.example.notes.data.repository.NoteRepositoryImpl
import com.example.notes.domain.repository.NoteRepository

object AppModule {

    fun provideDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "notes.db"
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    fun provideNoteRepository(context: Context): NoteRepository {
        val database = provideDatabase(context)
        return NoteRepositoryImpl(database.noteDao())
    }
}
