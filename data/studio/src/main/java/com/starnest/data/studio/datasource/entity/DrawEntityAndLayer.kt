package com.starnest.data.studio.datasource.entity

import androidx.room.Embedded
import androidx.room.Relation


data class DrawEntityAndLayer(
    @Embedded val drawEntity: DrawEntity,
    @Relation(
        parentColumn = "id", entityColumn = "drawId", entity = DrawLayerEntity::class
    ) val details: List<DrawLayerEntity> = ArrayList(),
) {
    fun mapToDrawAndLayer(): DrawEntity {
        val drawItem = drawEntity
        drawItem.name = details.firstOrNull()?.name ?: ""
        return drawItem
    }
}