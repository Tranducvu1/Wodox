package com.wodox.data.remote.model.model.response

import android.annotation.SuppressLint
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Parcelize
@Serializable
data class YoutubeTranscriptResponseDto(
    @SerializedName("data")
    var data: List<YoutubeTranscriptDataDto>
) : Parcelable, java.io.Serializable {

}