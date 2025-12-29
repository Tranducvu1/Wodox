package com.wodox.domain.home.model.local

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class SupportLevel(val value: Int, val displayName: String) : Parcelable {
    NONE(0, "None"),
    LOW(2, "Low"),
    MEDIUM(5, "Medium"),
    HIGH(7, "High"),
    CRITICAL(10, "Critical");

    companion object {
        fun fromValue(value: Int): SupportLevel {
            return values().find { it.value == value } ?: NONE
        }
    }
}