package com.starnest.data.studio.datasource.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.starnest.data.studio.datasource.entity.DrawLayerEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface DrawLayerDao {
    @Transaction
    @Query("""
    SELECT * FROM DrawLayerEntity
    WHERE 
        deletedAt IS NULL
    ORDER BY createdAt ASC
    """)
    fun getAllDrawItem(): PagingSource<Int, DrawLayerEntity>

    @Transaction
    suspend fun save(data: DrawLayerEntity): Long {
        val id = insert(data)
        return if (id == -1L) {
            val result = update(data)

            return result.toLong()
        } else {
            id
        }
    }

    @Insert(onConflict = OnConflictStrategy.Companion.IGNORE)
    suspend fun insert(drawLayerEntity: DrawLayerEntity): Long

    @Update
    suspend fun update(drawLayerEntity: DrawLayerEntity): Int

    @Query("SELECT * FROM DrawLayerEntity WHERE deletedAt IS NULL")
    fun getDrawItems(): List<DrawLayerEntity>

    @Query("""
    SELECT * FROM DrawLayerEntity
    WHERE drawId = :drawId
      AND deletedAt IS NULL
    ORDER BY createdAt ASC
""")
    fun getDrawLayersByDrawId(drawId: UUID): Flow<List<DrawLayerEntity>>

}