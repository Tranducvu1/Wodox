package com.wodox.domain.home.model.local

data class TaskScoreWeight(
    val priority: Int = 4,
    val deadline: Int = 3,
    val difficulty: Int = 2,
    val support: Int = 1
)
