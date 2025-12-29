package com.wodox.data.home.datasource.local.database.task.converter

import androidx.room.TypeConverter
import com.wodox.domain.home.model.local.AttachmentType

class AttachmentTypeConverter {
    @TypeConverter
    fun fromType(type: AttachmentType?): String? = type?.name

    @TypeConverter
    fun toType(name: String?): AttachmentType? =
        name?.let { AttachmentType.valueOf(it) }
}
