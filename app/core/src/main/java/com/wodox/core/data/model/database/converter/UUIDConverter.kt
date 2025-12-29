//package com.wodox.core.data.model.database.converter
//
//import androidx.room.TypeConverter
//import java.util.UUID
//
//class UUIDConverter {
//    @TypeConverter
//    fun fromUUID(uuid: UUID?): String? = uuid?.toString()
//
//    @TypeConverter
//    fun toUUID(uuid: String?): UUID? = uuid?.let { UUID.fromString(it) }
//}