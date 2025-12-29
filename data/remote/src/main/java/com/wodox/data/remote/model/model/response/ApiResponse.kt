package com.wodox.data.remote.model.model.response

import android.annotation.SuppressLint
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Parcelize
@Serializable
data class ResponseDataDto(
    @SerializedName("data")
    var data: TextCompletionDataDto?
) : Parcelable, java.io.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Parcelize
@Serializable
data class ResponseUploadDto(
    @SerializedName("data")
    var data: UploadDataDto?
) : Parcelable, java.io.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
@Parcelize
data class ResponseSearchDto(
    @SerializedName("data")
    @Contextual
    var data: ResponseSearchDataDto? = null
) : Parcelable, java.io.Serializable

@Parcelize
data class ResponseSearchDataDto(
    @SerializedName("searchParameters")
    var searchParameters: SearchParametersDto
) : Parcelable, java.io.Serializable

@Parcelize
data class SearchParametersDto(
    @SerializedName("q")
    var q: String,
    @SerializedName("type")
    var type: String,
    @SerializedName("engine")
    var engine: String,
) : Parcelable, java.io.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Parcelize
@Serializable
data class ResponseEmbeddingDto(
    @SerializedName("data")
    var data: EmbeddingDataDto?
) : Parcelable, java.io.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Parcelize
@Serializable
data class EmbeddingDataDto(
    @SerializedName("data")
    var data: ArrayList<EmbeddingValueDto>
) : Parcelable, java.io.Serializable


@SuppressLint("UnsafeOptInUsageError")
@Parcelize
@Serializable
data class UploadDataDto(
    @SerializedName("fileName")
    var fileName: String,
    @SerializedName("filePath")
    var filePath: String,
    @SerializedName("originalName")
    var originalName: String
) : Parcelable, java.io.Serializable


@SuppressLint("UnsafeOptInUsageError")
@Parcelize
@Serializable
data class ResponseErrorDto(
    @SerializedName("message")
    var message: String?,
    @SerializedName("statusCode")
    var statusCode: Int?,
    @SerializedName("error")
    var error: String?
) : Parcelable, java.io.Serializable
