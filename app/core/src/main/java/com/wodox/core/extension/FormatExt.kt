package com.wodox.core.extension


import com.wodox.core.data.model.DatePattern
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Date.format(datePattern: DatePattern): String {
    return try {
        val sdf = SimpleDateFormat(datePattern.pattern, Locale.getDefault())
        sdf.format(this)
    } catch (e: Exception) {
        ""
    }
}

fun Date.format(pattern: String): String {
    return try {
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        sdf.format(this)
    } catch (e: Exception) {
        ""
    }
}

fun String.toDate(datePattern: DatePattern): Date? {
    return try {
        val sdf = SimpleDateFormat(datePattern.pattern, Locale.getDefault())
        sdf.parse(this)
    } catch (e: Exception) {
        null
    }
}