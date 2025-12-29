package com.wodox.core.data.util

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.lang.reflect.Type

object GsonUtils {

    private var gson: Gson? = null

    fun provideGson(): Gson {
        synchronized(this) {
            if (gson == null) {
                gson = GsonBuilder()
                    .setLenient()
                    .create()
            }
            return gson!!
        }
    }

    fun toJson(obj: Any?): String {
        return try {
            provideGson().toJson(obj)
        } catch (e: Exception) {
            ""
        }
    }

    fun <T> fromJson(json: String, classOfT: Class<T>): T? {
        return try {
            provideGson().fromJson(json, classOfT)
        } catch (e: Exception) {
            null
        }
    }

    fun <T> fromJson(json: String, typeOfT: Type): T? {
        return try {
            provideGson().fromJson(json, typeOfT)
        } catch (e: Exception) {
            null
        }
    }
}