package com.wodox.data.remote.model.model.request

import android.annotation.SuppressLint
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Parcelize
@Serializable
data class ChatMessageDto(
    @SerializedName("role")
    var role: String = "",
    @SerializedName("content")
    var content: List<ChatMessageContentDto> = arrayListOf()
) : Parcelable, java.io.Serializable {

}