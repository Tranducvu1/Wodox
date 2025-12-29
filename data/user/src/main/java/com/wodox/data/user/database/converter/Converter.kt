package com.wodox.data.user.database.converter

import androidx.room.TypeConverter
import java.util.Date


class Converter {

    @TypeConverter
    fun fromDate(date: Date?): Long? = date?.time

    @TypeConverter
    fun toDate(timestamp: Long?): Date? = timestamp?.let { Date(it) }
}
