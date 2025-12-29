package com.wodox.data.home.datasource.local.database.task.converter


import androidx.room.TypeConverter
import com.wodox.domain.home.model.local.TaskStatus
import java.util.*

class Converter {
    @TypeConverter
    fun fromTaskStatus(status: TaskStatus?): String? = status?.name

    @TypeConverter
    fun toTaskStatus(value: String?): TaskStatus? = value?.let { TaskStatus.valueOf(it) }
}
