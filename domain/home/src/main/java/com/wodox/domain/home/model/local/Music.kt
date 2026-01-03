package com.wodox.domain.home.model.local

data class Music(
    val id: String,
    val name: String,
    val artist: String,
    val duration: String,
    val albumArtUrl: String,
    val previewUrl: String = ""
)