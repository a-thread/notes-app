package com.example.notes.di

import android.content.Context
import androidx.room.Room
import com.example.notes.data.local.database.AppDatabase
import com.example.notes.data.remote.supabase.SupabaseClientProvider
import com.example.notes.data.remote.supabase.SupabaseNoteRemoteDataSource
import com.example.notes.data.repository.NoteRepositoryImpl
import com.example.notes.data.repository.AuthRepositoryImpl
import com.example.notes.domain.repository.AuthRepository
import com.example.notes.domain.repository.NoteRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

object AppModule {

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(
            client = SupabaseClientProvider.client
        )
    }

    fun provideAuthRepository(): AuthRepository = authRepository

    fun provideDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "notes.db"
        )
            .fallbackToDestructiveMigration(false)
            .build()
    }

    fun provideNoteRepository(context: Context): NoteRepository {
        val database = provideDatabase(context)

        val remoteDataSource = SupabaseNoteRemoteDataSource(
            client = SupabaseClientProvider.client
        )

        return NoteRepositoryImpl(
            noteDao = database.noteDao(),
            remote = remoteDataSource,
            authRepository = authRepository,
            externalScope = appScope
        )
    }
}
