package com.wodox.core.extension


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import java.io.Serializable

inline fun <reified T : Activity> Context.openActivity(
    vararg args: Pair<String, Any?>
) {
    val intent = Intent(this, T::class.java)

    if (args.isNotEmpty()) {
        args.forEach { (key, value) ->
            when (value) {
                is String -> intent.putExtra(key, value)
                is Int -> intent.putExtra(key, value)
                is Long -> intent.putExtra(key, value)
                is Boolean -> intent.putExtra(key, value)
                is Float -> intent.putExtra(key, value)
                is Double -> intent.putExtra(key, value)
                is Char -> intent.putExtra(key, value)
                is Byte -> intent.putExtra(key, value)
                is Short -> intent.putExtra(key, value)
                is IntArray -> intent.putExtra(key, value)
                is LongArray -> intent.putExtra(key, value)
                is FloatArray -> intent.putExtra(key, value)
                is DoubleArray -> intent.putExtra(key, value)
                is BooleanArray -> intent.putExtra(key, value)
                is CharArray -> intent.putExtra(key, value)
                is ByteArray -> intent.putExtra(key, value)
                is ShortArray -> intent.putExtra(key, value)
                is ArrayList<*> -> {
                    when {
                        value.firstOrNull() is String ->
                            intent.putStringArrayListExtra(key, value as ArrayList<String>)
                        value.firstOrNull() is Int ->
                            intent.putIntegerArrayListExtra(key, value as ArrayList<Int>)
                        value.firstOrNull() is CharSequence ->
                            intent.putCharSequenceArrayListExtra(key, value as ArrayList<CharSequence>)
                    }
                }
                is Array<*> -> {
                    when {
                        value.firstOrNull() is String ->
                            intent.putExtra(key, value)
                        value.firstOrNull() is CharSequence ->
                            intent.putExtra(key, value as Array<*>)
                    }
                }
                is Bundle -> intent.putExtra(key, value)
                is Serializable -> intent.putExtra(key, value)
                is Parcelable -> intent.putExtra(key, value)
            }
        }
    }

    startActivity(intent)
}


inline fun <reified T : Activity> Activity.openActivityAndFinish(
    vararg args: Pair<String, Any?>
) {
    openActivity<T>(*args)
    finish()
}


inline fun <reified T : Activity> Activity.openActivityForResult(
    requestCode: Int,
    vararg args: Pair<String, Any?>
) {
    val intent = Intent(this, T::class.java)

    if (args.isNotEmpty()) {
        args.forEach { (key, value) ->
            when (value) {
                is String -> intent.putExtra(key, value)
                is Int -> intent.putExtra(key, value)
                is Long -> intent.putExtra(key, value)
                is Boolean -> intent.putExtra(key, value)
                is Float -> intent.putExtra(key, value)
                is Double -> intent.putExtra(key, value)
                is Bundle -> intent.putExtra(key, value)
                is Serializable -> intent.putExtra(key, value)
                is Parcelable -> intent.putExtra(key, value)
            }
        }
    }

    startActivityForResult(intent, requestCode)
}

inline fun <reified T : Activity> Context.openActivityWithFlags(
    flags: Int,
    vararg args: Pair<String, Any?>
) {
    val intent = Intent(this, T::class.java).apply {
        this.flags = flags
    }

    if (args.isNotEmpty()) {
        args.forEach { (key, value) ->
            when (value) {
                is String -> intent.putExtra(key, value)
                is Int -> intent.putExtra(key, value)
                is Long -> intent.putExtra(key, value)
                is Boolean -> intent.putExtra(key, value)
                is Serializable -> intent.putExtra(key, value)
            }
        }
    }

    startActivity(intent)
}