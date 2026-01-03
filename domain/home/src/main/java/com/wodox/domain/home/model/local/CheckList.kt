package com.wodox.domain.home.model.local

import java.io.Serializable
import java.util.Date
import java.util.UUID

data class CheckList(
    var id: String = UUID.randomUUID().toString(),

    var taskId: String = "",

    var description: String? = null,

    var createdAt: Date = Date(),

    var updatedAt: Date = Date(),

    var deletedAt: Date? = null,
) : Serializable