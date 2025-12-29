package com.wodox.data.home.datasource.local.database.task.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.wodox.domain.home.model.local.TaskStatus
import kotlinx.parcelize.Parcelize
import java.util.Date
import java.util.UUID

@Parcelize
@Entity(
    tableName = "SubTask",
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
data class SubTaskEntity(
    @PrimaryKey
    var id: UUID = UUID.randomUUID(),

    var taskId: UUID,

    var title: String = "",

    var description: String? = null,

    var status: TaskStatus = TaskStatus.TODO,

    var priority: Int = 0,

    var startAt: Date? = null,

    var dueAt: Date? = null,

    var estimateAt: Date? = null,

    var createdAt: Date = Date(),

    var updatedAt: Date = Date(),

    var deletedAt: Date? = null,
) : Parcelable
