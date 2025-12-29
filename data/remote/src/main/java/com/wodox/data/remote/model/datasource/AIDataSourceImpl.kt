package com.wodox.data.remote.model.datasource

import com.google.gson.Gson
import com.starnest.domain.common.model.Sender
import com.wodox.data.remote.extension.parseError
import com.wodox.data.remote.model.api.ApiService
import com.wodox.data.remote.model.model.request.ChatRequestDto
import com.wodox.data.remote.model.model.request.TextCompletionRequestDto
import com.wodox.data.remote.model.model.response.ChatSuggestionResponseDto
import com.wodox.data.remote.model.model.response.TextCompletionChoiceDto
import com.wodox.data.remote.model.model.response.TextCompletionDataDto
import com.wodox.data.remote.model.model.response.TextCompletionMessageResponseDto
import com.wodox.data.remote.model.model.response.TextCompletionResponseDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import org.json.JSONException
import retrofit2.awaitResponse
import java.util.UUID
import javax.inject.Inject
import kotlin.text.iterator

class SSE(val data: String) {
    fun toBytes(): ByteArray {
        return String.format("data: %s\n\n", data).toByteArray()
    }

    val isDone: Boolean
        get() = DONE_DATA.equals(data, ignoreCase = true)

    companion object {
        private const val DONE_DATA = "[DONE]"
    }
}

class SSEFormatException : Exception("Invalid sse format!") {
}

class AIChatDataSourceImpl @Inject constructor(
    private val openAIApi: ApiService,
    private val gson: Gson
) : AIDataSource {
    override fun chatWithStream(
        groupId: String,
        headers: Map<String, String>,
        request: ChatRequestDto
    ): Flow<Pair<String, TextCompletionResponseDto>> = callbackFlow {
        try {
            val response = openAIApi.chatStream(
                headers, request
            ).execute()

            if (response.isSuccessful) {
                val data = response.body()?.byteStream()?.bufferedReader() ?: throw Exception()
                try {
                    val chunkId = UUID.randomUUID().toString()

                    var sse: SSE? = null
                    while (true) {
                        val line = withContext(Dispatchers.IO) {
                            data.readLine()
                        } ?: continue

                        if (line.startsWith("data:")) {
                            val substring = line.substring(5).trim { it <= ' ' }
                            sse = SSE(substring)
                        } else if (line == "" && sse != null) {
                            if (sse.isDone) {
                                break
                            }

                            val chunk = gson.fromJson(
                                sse.data, TextCompletionDataDto::class.java
                            )

                            val output = chunk.output ?: ""
                            for (character in output) {
                                trySend(
                                    Pair(
                                        groupId,
                                        TextCompletionResponseDto(
                                            data = TextCompletionDataDto().apply {
                                                id = chunkId
                                                choices = arrayListOf(
                                                    TextCompletionChoiceDto().apply {
                                                        message = TextCompletionMessageResponseDto(
                                                            role = Sender.ASSISTANT.value,
                                                            content = character.toString()
                                                        )
                                                    }
                                                )
                                            }
                                        )
                                    )
                                )
                                delay(10)
                            }

                        } else {
                            throw SSEFormatException()
                        }
                    }


                    close()
                } catch (e: Exception) {
                    trySend(
                        Pair(
                            groupId, TextCompletionResponseDto(
                                data = null, error = e.localizedMessage
                            )
                        )
                    )

                    close()
                    throw Exception(e)
                } finally {
                    withContext(Dispatchers.IO) {
                        data.close()
                    }
                }
            } else {
                try {
                    trySend(
                        Pair(
                            groupId, TextCompletionResponseDto(
                                data = null,
                                error = response.errorBody()?.parseError()?.message
                                    ?: "There is something went wrong. Please try again!"
                            )
                        )
                    )
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                close()
            }
        } catch (e: Exception) {
            trySend(
                Pair(
                    groupId, TextCompletionResponseDto(
                        data = null, error = e.localizedMessage
                    )
                )
            )
            close()
        }
    }

    override suspend fun textCompletions(
        headers: Map<String, String>,
        request: TextCompletionRequestDto
    ): TextCompletionResponseDto {
        try {
            val response = openAIApi.createChatCompletion(
                header = headers,
                request = request
            ).execute()

            if (!response.isSuccessful) {
                return TextCompletionResponseDto(
                    error = response.errorBody()?.parseError()?.message
                        ?: "There is something went wrong. Please try again!"
                )
            } else {
                val result = response.body()?.data

                return if (result != null) {
                    TextCompletionResponseDto(
                        data = result
                    )
                } else {
                    TextCompletionResponseDto(
                        error = "Something when wrong"
                    )
                }
            }
        } catch (e: Exception) {
            return TextCompletionResponseDto(
                error = "There is something went wrong. Please try again!"
            )
        }
    }

    override suspend fun getSuggestions(
        headers: Map<String, String>,
        request: TextCompletionRequestDto
    ): ArrayList<String> {
        val result = ArrayList<String>()
        try {

            val response = withTimeoutOrNull(10_000) {
                openAIApi.getSuggestions(
                    header = headers,
                    request = request
                ).awaitResponse()
            } ?: return arrayListOf()


            if (response.isSuccessful) {
                val resultText =
                    response.body()?.data?.choices?.firstOrNull()?.message?.content ?: ""

                val parser = ChatSuggestionResponseDto.parse(resultText, gson)
                val suggestions = parser?.suggestions ?: arrayListOf()
                result.addAll(suggestions)
            }
        } catch (_: Exception) {
            return arrayListOf()
        }

        return result
    }
}