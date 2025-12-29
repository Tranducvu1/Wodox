package com.wodox.core.data.util


import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.lang.reflect.Type

object JsonUtils {

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

    inline fun <reified T> parse(json: String): T? {
        return try {
            if (json.isEmpty()) return null
            provideGson().fromJson(json, T::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    fun <T> parse(json: String, typeOfT: Type): T? {
        return try {
            if (json.isEmpty()) return null
            provideGson().fromJson(json, typeOfT)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun toJson(obj: Any?): String {
        return try {
            provideGson().toJson(obj)
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    fun toPrettyJson(obj: Any?): String {
        return try {
            val gson = GsonBuilder()
                .setPrettyPrinting()
                .create()
            gson.toJson(obj)
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }
}