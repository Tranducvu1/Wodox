package com.wodox.data.home.datasource.local.database.task.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.Date
import java.util.UUID

@Parcelize
@Entity(
    tableName = "Attachment",
    foreignKeys = [
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["id"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = SubTaskEntity::class,
            parentColumns = ["id"],
            childColumns = ["subTaskId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)

data class AttachmentEntity(
    @PrimaryKey
    var id: UUID = UUID.randomUUID(),

    var taskId: UUID= UUID.randomUUID(),

    var subTaskId:UUID? = null,

    var name: String? = null,

    var uri: String? = null,

    var type: String? = null,

    var url: String? = null,

    var createdAt: Date = Date(),

    var updatedAt: Date = Date(),

    var deletedAt: Date? = null,
) : Parcelable
