package com.athread.lichen.di

import android.content.Context
import androidx.room.Room
import com.athread.lichen.data.local.database.AppDatabase
import com.athread.lichen.data.remote.supabase.SupabaseClientProvider
import com.athread.lichen.data.remote.supabase.SupabaseNoteRemoteDataSource
import com.athread.lichen.data.repository.NoteRepositoryImpl
import com.athread.lichen.data.repository.AuthRepositoryImpl
import com.athread.lichen.domain.repository.AuthRepository
import com.athread.lichen.domain.repository.NoteRepository
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
