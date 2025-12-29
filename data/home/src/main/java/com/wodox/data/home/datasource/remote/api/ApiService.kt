package com.wodox.data.home.datasource.remote.api

import com.wodox.data.home.datasource.remote.model.request.TextCompletionRequestDto
import com.wodox.data.home.datasource.remote.model.response.ResponseDataDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.POST

interface ApiService {
    @POST("/api/completions/v3")
    fun createChatCompletionV2(
        @HeaderMap header: Map<String, String>,
        @Body request: TextCompletionRequestDto,
    ): Call<ResponseDataDto>
}