package com.wodox.data.home.datasource.local.database.task.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.wodox.data.home.datasource.local.database.task.entity.UserFriendEntity
import com.wodox.domain.home.model.local.FriendStatus
import kotlinx.coroutines.flow.Flow
import java.util.Date
import java.util.UUID

@Dao
interface UserFriendDao {

    @Query("""
        SELECT * FROM UserFriend
        WHERE deletedAt IS NULL
        ORDER BY createdAt ASC
    """)
    fun getAll(): Flow<List<UserFriendEntity>>

    @Query("""
        SELECT * FROM UserFriend
        WHERE userId = :userId
        AND status = :status
        AND deletedAt IS NULL
        ORDER BY createdAt ASC
    """)
    fun getFriendSent(
        userId: UUID,
        status: FriendStatus = FriendStatus.PENDING
    ): Flow<List<UserFriendEntity>>

    @Query("""
        SELECT * FROM UserFriend
        WHERE friendId = :userId
        AND status = :status
        AND deletedAt IS NULL
        ORDER BY createdAt ASC
    """)
    fun getReceivedRequests(
        userId: UUID,
        status: FriendStatus = FriendStatus.PENDING
    ): Flow<List<UserFriendEntity>>

    @Query("""
        SELECT * FROM UserFriend
        WHERE (userId = :userId OR friendId = :userId)
        AND status = :status
        AND deletedAt IS NULL
    """)
    fun getAcceptedFriends(
        userId: UUID,
        status: FriendStatus = FriendStatus.ACCEPTED
    ): Flow<List<UserFriendEntity>>

    @Query("SELECT * FROM UserFriend WHERE id = :relationId LIMIT 1")
    suspend fun getById(relationId: UUID): UserFriendEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entity: UserFriendEntity): Long

    @Update
    suspend fun update(entity: UserFriendEntity): Int

    @Transaction
    suspend fun save(entity: UserFriendEntity): Long {
        val result = insert(entity)
        return if (result == -1L) {
            update(entity).toLong()
        } else result
    }

    @Query("""
        UPDATE UserFriend
        SET deletedAt = :deletedAt
        WHERE id = :relationId
    """)
    suspend fun softDelete(
        relationId: UUID,
        deletedAt: Date = Date()
    ): Int

    @Query("""
        SELECT * FROM UserFriend
        WHERE 
            (userId = :userId AND friendId = :friendId)
            OR
            (userId = :friendId AND friendId = :userId)
        LIMIT 1
    """)
    suspend fun findRelation(userId: UUID, friendId: UUID): UserFriendEntity?
}
