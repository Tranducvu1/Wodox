package com.wodox.data.user.database.converter

import java.util.UUID

class UUIDConverter {
    fun fromUUID(uuid: UUID?): String? = uuid?.toString()
    fun toUUID(uuid: String?): UUID? = uuid?.let { UUID.fromString(it) }
}
