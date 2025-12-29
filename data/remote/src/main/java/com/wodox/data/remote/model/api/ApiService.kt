package com.wodox.data.remote.model.api

import com.wodox.data.remote.model.model.request.ChatRequestDto
import com.wodox.data.remote.model.model.request.TextCompletionRequestDto
import com.wodox.data.remote.model.model.response.ResponseDataDto
import com.starnest.domain.remote.model.request.TextCompletionRequest
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.POST
import retrofit2.http.Streaming

interface ApiService {
    @Streaming
    @POST("/api/completions/v3/stream")
    fun chatStream(
        @HeaderMap header: Map<String, String>,
        @Body request: ChatRequestDto
    ): Call<ResponseBody>

    @POST("/api/completions/v3")
    fun createChatCompletion(
        @HeaderMap header: Map<String, String>,
        @Body request: TextCompletionRequestDto
    ): Call<ResponseDataDto>

    @POST("/api/completions/v3")
    fun getSuggestions(
        @HeaderMap header: Map<String, String>,
        @Body request: TextCompletionRequestDto
    ): Call<ResponseDataDto>

    @POST("/api/completions/v2")
    fun createChatCompletion(
        @HeaderMap header: Map<String, String>,
        @Body request: TextCompletionRequest,
    ): Call<ResponseDataDto>



}