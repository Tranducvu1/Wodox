package com.wodox.domain.home.model.local

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class Priority(val value: Int, val displayName: String) : Parcelable {
    LOW(1, "Low"),
    MEDIUM(3, "Medium"),
    HIGH(5, "High"),
    URGENT(7, "Urgent"),
    CRITICAL(10, "Critical");

    companion object {
        fun fromValue(value: Int): Priority {
            return values().find { it.value == value } ?: MEDIUM
        }
    }
}