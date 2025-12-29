package com.wodox.data.remote.model.model.request

import android.annotation.SuppressLint
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Parcelize
@Serializable
data class ChatMessageContentImageDto(
    @SerializedName("url")
    val url: String,
) : Parcelable, java.io.Serializable
