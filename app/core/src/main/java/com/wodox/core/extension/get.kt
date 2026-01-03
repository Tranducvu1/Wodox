package com.wodox.core.extension


import android.content.SharedPreferences

inline fun <reified T> SharedPreferences.get(key: String, defaultValue: T? = null): T {
    return when (T::class) {
        String::class -> {
            (this.getString(key, defaultValue as? String ?: "") as T)
        }
        Int::class -> {
            (this.getInt(key, defaultValue as? Int ?: 0) as T)
        }
        Boolean::class -> {
            (this.getBoolean(key, defaultValue as? Boolean ?: false) as T)
        }
        Long::class -> {
            (this.getLong(key, defaultValue as? Long ?: 0L) as T)
        }
        Float::class -> {
            (this.getFloat(key, defaultValue as? Float ?: 0f) as T)
        }
        else -> throw IllegalArgumentException("Unsupported type: ${T::class}")
    }
}


inline fun <reified T> SharedPreferences.set(key: String, value: T) {
    val editor = this.edit()
    when (value) {
        is String -> editor.putString(key, value)
        is Int -> editor.putInt(key, value)
        is Boolean -> editor.putBoolean(key, value)
        is Long -> editor.putLong(key, value)
        is Float -> editor.putFloat(key, value)
        else -> throw IllegalArgumentException("Unsupported type: ${value?.javaClass}")
    }
    editor.apply()
}