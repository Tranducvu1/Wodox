package com.wodox.data.remote.extension

import com.wodox.core.data.util.GsonUtils
import com.wodox.data.remote.model.model.response.ResponseErrorDto
import okhttp3.ResponseBody
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

fun ResponseBody.parseError(): ResponseErrorDto? {
    return try {
        val reader = BufferedReader(InputStreamReader(byteStream()))

        GsonUtils.provideGson().fromJson(reader, ResponseErrorDto::class.java)
    } catch (e: IOException) {
        e.printStackTrace()

        ResponseErrorDto(
            message = "There is something went wrong. Please try again!",
            error = "There is something went wrong. Please try again!",
            statusCode = 500
        )
    }

}