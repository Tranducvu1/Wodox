package com.wodox.data.home.datasource.local.database.task.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.wodox.data.home.datasource.local.database.task.entity.CheckListEntity
import com.wodox.data.home.datasource.local.database.task.entity.SubTaskEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID


@Dao
interface CheckListDao {
    @Query(
        """
        SELECT * FROM CheckList
        WHERE taskId = :taskId
        AND deletedAt IS NULL
        ORDER BY createdAt ASC
        """
    )
    fun getAllCheckList(taskId: UUID): Flow<List<CheckListEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(checkList: CheckListEntity): Long

    @Update
    suspend fun update(checkList: CheckListEntity): Int

    @Transaction
    suspend fun save(checkList: CheckListEntity): Long {
        val id = insert(checkList)
        return if (id == -1L) {
            update(checkList).toLong()
        } else {
            id
        }
    }
}