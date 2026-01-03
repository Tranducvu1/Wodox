package com.wodox.data.home.datasource.local.database.task.mapper

import com.wodox.base.base.AbstractMapper
import com.wodox.data.home.datasource.local.database.task.entity.TaskEntity
import com.wodox.domain.home.model.local.*
import java.util.UUID
import javax.inject.Inject

class TaskMapper @Inject constructor() :
    AbstractMapper<TaskEntity, Task>() {

    override fun mapToDomain(entity: TaskEntity): Task {
        return Task(
            id = entity.id,
            projectId = entity.projectId,
            ownerId = entity.ownerId,
            title = entity.title,
            description = entity.description,
            status = entity.status,
            isFavourite = entity.isFavourite,
            priority = Priority.entries
                .firstOrNull { it.value == entity.priority }
                ?: Priority.MEDIUM,
            difficulty = Difficulty.entries
                .firstOrNull { it.value == entity.difficulty }
                ?: Difficulty.NORMAL,
            support = SupportLevel.entries
                .firstOrNull { it.value == entity.support }
                ?: SupportLevel.NONE,
            startAt = entity.startAt,
            dueAt = entity.dueAt,
            assignedUserIds = entity.assignedUserIds
                .takeIf { it.isNotBlank() }
                ?.split(",")
                ?.mapNotNull {
                    try {
                        UUID.fromString(it.trim())
                    } catch (e: Exception) {
                        null
                    }
                }
                ?: emptyList(),
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            deletedAt = entity.deletedAt,
            calculatedPriority = entity.calculatedPriority
        )
    }

    override fun mapToEntity(domain: Task): TaskEntity {
        return TaskEntity(
            id = domain.id,
            projectId = domain.projectId,
            ownerId = domain.ownerId,
            title = domain.title,
            description = domain.description,
            status = domain.status,
            priority = domain.priority.value,
            difficulty = domain.difficulty.value,
            support = domain.support.value,
            startAt = domain.startAt,
            isFavourite = domain.isFavourite,
            dueAt = domain.dueAt,
            assignedUserIds = domain.assignedUserIds
                .joinToString(",") { it.toString() },
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt,
            deletedAt = domain.deletedAt,
            calculatedPriority = domain.calculatedPriority
        )
    }
}