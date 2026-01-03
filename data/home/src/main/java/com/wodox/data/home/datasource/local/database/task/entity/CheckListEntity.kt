package com.wodox.data.home.datasource.local.database.task.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.firebase.firestore.IgnoreExtraProperties
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
@IgnoreExtraProperties
@Entity(
    tableName = "CheckList",
    indices = [
        Index(value = ["id"], unique = true),
        Index(value = ["taskId"])
    ],
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
    var id: String = "",

    var taskId: String = "",

    var description: String? = null,

    var createdAt: Date? = null,

    var updatedAt: Date? = null,

    var deletedAt: Date? = null,
) : Parcelable {
    constructor() : this(
        id = "",
        taskId = "",
        description = null,
        createdAt = null,
        updatedAt = null,
        deletedAt = null
    )
}