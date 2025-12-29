package com.wodox.data.remote.model.model.response

import android.annotation.SuppressLint
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable


@SuppressLint("UnsafeOptInUsageError")
@Parcelize
@Serializable
data class EmbeddingValueDto(
    @SerializedName("embedding")
    var embedding: ArrayList<Double>
) : Parcelable, java.io.Serializable