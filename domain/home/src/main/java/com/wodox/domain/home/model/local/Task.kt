package com.wodox.domain.home.model.local

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable
import java.util.Date
import java.util.UUID

@Parcelize
data class Task(
    val id: UUID = UUID.randomUUID(),
    val projectId: UUID? = null,
    val ownerId: UUID,
    val title: String,
    val description: String? = null,
    val status: TaskStatus = TaskStatus.TODO,
    val priority: Priority = Priority.MEDIUM,
    val difficulty: Difficulty = Difficulty.NORMAL,
    val support: SupportLevel = SupportLevel.NONE,
    val startAt: Date? = null,
    val dueAt: Date? = null,
    val isFavourite: Boolean = false,
    var isFirstOfDay: Boolean = false,
    val createdAt: Date = Date(),
    val skillLevel: SkillLevel = SkillLevel.FRESHER,
    val updatedAt: Date = Date(),
    var deletedAt: Date? = null,
    val assignedUserIds: List<UUID> = emptyList(),
    val calculatedPriority: Double = 0.0
) : Parcelable, Serializable

