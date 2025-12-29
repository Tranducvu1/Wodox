package com.wodox.domain.home.model.local.response

data class AIAnalysisResponse(
    val skillScore: Double,
    val skillLevel: String,
    val insights: List<String>
)