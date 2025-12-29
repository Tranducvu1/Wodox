package com.starnest.data.studio.datasource.converter

import androidx.room.TypeConverter
import com.starnest.domain.common.model.Sender


object SenderConverter {
    @TypeConverter
    fun fromSender(value: String): Sender {
        return Sender.entries.first { it.value == value }
    }

    @TypeConverter
    fun senderToString(value: Sender): String {
        return value.value
    }
}