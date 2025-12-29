package com.wodox.data.remote.model.model.response

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable


@SuppressLint("UnsafeOptInUsageError")
@Parcelize
@Serializable
data class YoutubeTranscriptDataDto(
    var text: String, val duration: Double, val offset: Double, val lang: String?,
    var isSelected: Boolean = false
) :
    Parcelable {
}