package com.wodox.data.remote.model.model.response

import android.annotation.SuppressLint
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Parcelize
@Serializable
class TextCompletionChoiceDto(
    @SerializedName("message")
    var message: TextCompletionMessageResponseDto? = null,
    @SerializedName("delta")
    var delta: TextCompletionMessageResponseDto? = null,
) : Parcelable, java.io.Serializable {
}

