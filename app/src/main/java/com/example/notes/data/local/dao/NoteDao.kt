package com.example.notes.data.local.dao

import androidx.room.*
import com.example.notes.data.local.entity.NoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Query("""
        SELECT * FROM note
        WHERE userId = :userId
        ORDER BY updatedAt DESC
    """)
    fun observeByUserId(userId: String): Flow<List<NoteEntity>>

    @Query("SELECT * FROM note WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): NoteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(note: NoteEntity)

    @Query("DELETE FROM note WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM note WHERE userId = :userId")
    suspend fun deleteAllForUser(userId: String)
}
