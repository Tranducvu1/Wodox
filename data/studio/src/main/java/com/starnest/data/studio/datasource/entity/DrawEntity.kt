package com.starnest.data.studio.datasource.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.Date
import java.util.UUID

@Parcelize
@Entity(
    tableName = "DrawEntity",
    foreignKeys = [
        ForeignKey(
            entity = CategoryStudioEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
        )
    ]
)

data class DrawEntity(
    @PrimaryKey()
    var id: UUID = UUID.randomUUID(),
    var categoryId : UUID,
    var rect: Boolean,
    var isFavourite: Boolean,
    var createdAt: Date = Date(),
    var updatedAt: Date = Date(),
    var deletedAt: Date? = null,
): Parcelable {
    @Ignore
    var name: String = ""
}

