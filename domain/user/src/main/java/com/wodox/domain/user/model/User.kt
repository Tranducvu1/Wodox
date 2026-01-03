package com.wodox.domain.user.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date
import java.util.UUID

@Parcelize
data class User(
    var id: UUID = UUID.randomUUID(),
    var email: String = "",
    var password: String = "",
    var name: String = "",
    var avatar: String = "",
    var isActive: Boolean = false,
    var createdAt: Date = Date(),
    var updatedAt: Date = Date(),
    var skillLevel: SkillLevel = SkillLevel.FRESHER,
    var deletedAt: Date? = null,
    var bio :String? = "",
    var phone :String?= ""
): Parcelable