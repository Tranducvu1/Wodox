package com.wodox.data.home.datasource.remote.interceptor

import android.content.Context
import com.wodox.config.model.Config
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class RetryInterceptor(val context: Context,private val fallbackBaseUrl:String): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        var response: Response? = null
        try {
            response = chain.proceed(originalRequest)
            if(!response.isSuccessful){
                response.close()
                try {
                    response = retryWithFallback(chain, originalRequest)
                }catch (_: Exception){

                }
            }
        }catch (e: Exception){
            response?.close()
            response = retryWithFallback(chain, originalRequest)
        }
        return response

    }

    private fun retryWithFallback(chain: Interceptor.Chain, originalRequest: Request): Response {
        val newUrl = originalRequest.url.toString().replace(Config.API.BASE_URL, fallbackBaseUrl)
        val newRequest = originalRequest.newBuilder().url(newUrl).build()
        return chain.proceed(newRequest)
    }
}