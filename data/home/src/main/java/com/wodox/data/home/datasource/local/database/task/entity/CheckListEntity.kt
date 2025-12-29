package com.wodox.data.home.datasource.local.database.task.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.Date
import java.util.UUID


@Parcelize
@Entity(
    tableName = "CheckList",
    indices = [(Index(value = ["id"], unique = true))],
    foreignKeys = [
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["id"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class CheckListEntity(
    @PrimaryKey
    var id: UUID = UUID.randomUUID(),

    var taskId: UUID,

    var description: String? = null,

    var createdAt: Date = Date(),

    var updatedAt: Date = Date(),

    var deletedAt: Date? = null,
) : Parcelable
