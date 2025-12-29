package com.wodox.data.remote.model.model.request

import android.annotation.SuppressLint
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.starnest.domain.common.model.Sender
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Parcelize
@Serializable
data class TextCompletionMessageDto(
    @SerializedName("role")
    var role: String = "",
    @SerializedName("content")
    var content: String = "",
    @SerializedName("name")
    var name: String? = null,
) : Parcelable, java.io.Serializable {
    val sender: Sender
        get() = Sender.entries.firstOrNull { it.value == role } ?: Sender.ASSISTANT

}