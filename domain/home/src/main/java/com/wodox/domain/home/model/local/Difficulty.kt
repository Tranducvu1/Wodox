package com.wodox.domain.home.model.local

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class Difficulty(val value: Int, val displayName: String) : Parcelable {
    VERY_EASY(1, "Very Easy"),
    EASY(2, "Easy"),
    NORMAL(3, "Normal"),
    HARD(5, "Hard"),
    VERY_HARD(7, "Very Hard"),
    EXPERT(10, "Expert");

    companion object {
        fun fromValue(value: Int): Difficulty {
            return values().find { it.value == value } ?: NORMAL
        }
    }
}