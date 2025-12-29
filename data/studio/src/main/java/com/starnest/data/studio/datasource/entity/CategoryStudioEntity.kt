package com.starnest.data.studio.datasource.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

@Entity(
    tableName = "CategoryStudio",
    indices = [(Index(value = ["id"], unique = true))]
)
data class CategoryStudioEntity(
    @PrimaryKey()
    var id: UUID = UUID.randomUUID(),
    var name: String,
    var isHidden: Boolean,
    var createdAt: Date = Date(),
    var updatedAt: Date = Date(),
    var deletedAt: Date? = null,
)