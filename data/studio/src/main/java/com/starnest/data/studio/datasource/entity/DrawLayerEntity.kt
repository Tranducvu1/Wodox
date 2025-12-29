package com.starnest.data.studio.datasource.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

@Entity(
    tableName = "DrawLayerEntity",
    foreignKeys = [
        ForeignKey(
            entity = DrawEntity::class,
            parentColumns = ["id"],
            childColumns = ["drawId"],
        )
    ]
)

data class DrawLayerEntity(
    @PrimaryKey()
    var id: UUID = UUID.randomUUID(),
    var drawId : UUID,
    var name: String,
    var background: String,
    var snapshot:String,
    var rect:String,
    var isHidden:String,
    var createdAt: Date = Date(),
    var updatedAt: Date = Date(),
    var deletedAt: Date? = null,
)

