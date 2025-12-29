package com.wodox.data.remote.model.model.response

import android.annotation.SuppressLint
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Parcelize
@Serializable
data class TextCompletionDataDto(
    @SerializedName("id")
    var id: String? = null,
    @SerializedName("choices")
    var choices: List<TextCompletionChoiceDto> = arrayListOf()
) : Parcelable, java.io.Serializable {
    val output: String?
        get() = choices.firstOrNull()?.message?.content ?: choices.firstOrNull()?.delta?.content

}