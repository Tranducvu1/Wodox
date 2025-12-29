package com.starnest.data.studio.datasource.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.starnest.data.studio.datasource.entity.CategoryStudioEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface CategoryStudioDao {
    @Query(
        """
    SELECT * FROM CategoryStudio
    WHERE 
        deletedAt IS NULL 
        AND isHidden = 0
    ORDER BY createdAt ASC
    """
    )
    fun getAllCategory(): Flow<List<CategoryStudioEntity>>

    @Query(
        """
    SELECT * FROM CategoryStudio
    WHERE 
        deletedAt IS NULL
    ORDER BY createdAt ASC
    """
    )
    fun getAllShowCategory(): Flow<List<CategoryStudioEntity>>

    @Transaction
    suspend fun save(data: CategoryStudioEntity): Long {
        val id = insert(data)
        return if (id == -1L) {
            val result = update(data)

            return result.toLong()
        } else {
            id
        }
    }

    @Insert(onConflict = OnConflictStrategy.Companion.IGNORE)
    suspend fun insert(categoryEntity: CategoryStudioEntity): Long

    @Update
    suspend fun update(categoryEntity: CategoryStudioEntity): Int

    @Query(
        """
    SELECT * FROM CategoryStudio
    WHERE id = :categoryId AND deletedAt IS NULL
    LIMIT 1
    """
    )
    fun getCategoryById(categoryId: UUID): CategoryStudioEntity
}
