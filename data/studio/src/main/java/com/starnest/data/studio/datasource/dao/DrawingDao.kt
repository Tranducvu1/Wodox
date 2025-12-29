package com.starnest.data.studio.datasource.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.starnest.data.studio.datasource.entity.DrawEntity
import com.starnest.data.studio.datasource.entity.DrawEntityAndLayer
import java.util.UUID

@Dao
interface DrawingDao {

    @Query("""
    SELECT * FROM DrawEntity
    WHERE 
        deletedAt IS NULL
    ORDER BY createdAt ASC
    """)
    fun getAllDrawItem(): PagingSource<Int, DrawEntityAndLayer>

    @Transaction
    suspend fun save(data: DrawEntity): Long {
        val id = insert(data)
        return if (id == -1L) {
            val result = update(data)

            return result.toLong()
        } else {
            id
        }
    }

    @Insert(onConflict = OnConflictStrategy.Companion.IGNORE)
    suspend fun insert(drawItemEntity: DrawEntity): Long

    @Update
    suspend fun update(drawItemEntity: DrawEntity): Int

    @Query("SELECT * FROM DrawEntity WHERE deletedAt IS NULL")
    fun getDrawItems(): List<DrawEntity>

    @Transaction
    @Query("select * from DrawEntity where deletedAt is null and categoryId = :id order by createdAt DESC")
    fun getDrawItemByCategoryId(id: UUID): PagingSource<Int, DrawEntityAndLayer>
}