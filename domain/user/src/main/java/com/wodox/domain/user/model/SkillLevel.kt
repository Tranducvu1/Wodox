package com.wodox.domain.user.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class SkillLevel(val displayName: String, val minScore: Double) : Parcelable {
    INTERN("Intern", 0.0),
    FRESHER("Fresher", 2.0),
    JUNIOR("Junior", 4.0),
    MEDIUM("Medium", 6.0),
    SENIOR("Senior", 8.0),
    EXPERT("Expert", 9.5);

    companion object {
        fun fromScore(score: Double): SkillLevel {
            return values()
                .reversed()
                .firstOrNull { score >= it.minScore }
                ?: INTERN
        }

        fun fromValue(value: Double): SkillLevel {
            return fromScore(value)
        }
    }
}