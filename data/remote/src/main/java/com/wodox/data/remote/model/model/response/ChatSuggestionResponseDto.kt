package com.wodox.data.remote.model.model.response

import android.annotation.SuppressLint
import android.os.Parcelable
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable


@SuppressLint("UnsafeOptInUsageError")
@Parcelize
@Serializable
data class ChatSuggestionResponseDto(
    @SerializedName("suggestions")
    val suggestions: ArrayList<String>
) : java.io.Serializable, Parcelable {
    companion object {
        fun parse(value: String, gson: Gson): ChatSuggestionResponseDto? {
            return try {
                gson.fromJson(value, ChatSuggestionResponseDto::class.java)
            } catch (e: Exception) {
                null
            }
        }
    }
}