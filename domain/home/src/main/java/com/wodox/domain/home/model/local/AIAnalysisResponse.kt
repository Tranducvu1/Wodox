package com.wodox.domain.home.model.local

 data class AIAnalysisResponse(
    val skillScore: Double,
    val skillLevel: String,
    val insights: List<String>
)