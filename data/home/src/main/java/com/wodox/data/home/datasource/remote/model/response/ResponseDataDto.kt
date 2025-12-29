package com.wodox.data.home.datasource.remote.model.response


import com.google.gson.annotations.SerializedName

data class ResponseDataDto(
    @SerializedName("data")
    val data: TextCompletionDataDto?,

    @SerializedName("error")
    val error: String?
)