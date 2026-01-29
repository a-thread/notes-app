package com.athread.lichen.data.local.dao

import androidx.room.*
import com.athread.lichen.data.local.entity.NoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Query("""
        SELECT * FROM note
        WHERE userId = :userId
        ORDER BY updatedAt DESC
    """)
    fun observeByUserId(userId: String): Flow<List<NoteEntity>>

    @Query("""
    SELECT * FROM note
    WHERE userId = :userId
    ORDER BY updatedAt DESC
""")
    fun observeNewest(userId: String): Flow<List<NoteEntity>>

    @Query("""
    SELECT * FROM note
    WHERE userId = :userId
    ORDER BY updatedAt ASC
""")
    fun observeOldest(userId: String): Flow<List<NoteEntity>>

    @Query("""
    SELECT * FROM note
    WHERE userId = :userId
    ORDER BY title COLLATE NOCASE ASC
""")
    fun observeTitleAsc(userId: String): Flow<List<NoteEntity>>

    @Query("""
    SELECT * FROM note
    WHERE userId = :userId
    ORDER BY title COLLATE NOCASE DESC
""")
    fun observeTitleDesc(userId: String): Flow<List<NoteEntity>>


    @Query("SELECT * FROM note WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): NoteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(note: NoteEntity)

    @Query("DELETE FROM note WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM note WHERE userId = :userId")
    suspend fun deleteAllForUser(userId: String)

    @Query("""
        DELETE FROM note
        WHERE userId = :userId
        AND id NOT IN (:ids)
    """)
    suspend fun deleteNotInIds(
        userId: String,
        ids: Set<String>
    )
}
