package com.wodox.data.remote.model.model.request

import android.annotation.SuppressLint
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable


@SuppressLint("UnsafeOptInUsageError")
@Parcelize
@Serializable
data class ChatMessageContentDto(
    @SerializedName("type")
    val type: String,
    @SerializedName("text")
    var text: String? = null,
    @SerializedName("image_url")
    val imageUrl: ChatMessageContentImageDto? = null,
) : Parcelable, java.io.Serializable {
}