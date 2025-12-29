package com.wodox.core.extension

import android.util.Base64
import java.io.File

fun File.toBase64(): String {
    return try {
        val bytes = this.readBytes()
        Base64.encodeToString(bytes, Base64.DEFAULT)
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }
}

fun ByteArray.toBase64(): String {
    return try {
        Base64.encodeToString(this, Base64.DEFAULT)
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }
}

fun String.toBase64(): String {
    return try {
        Base64.encodeToString(this.toByteArray(), Base64.DEFAULT)
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }
}


fun String.fromBase64(): String {
    return try {
        val decodedBytes = Base64.decode(this, Base64.DEFAULT)
        String(decodedBytes)
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }
}


fun String.fromBase64ToByteArray(): ByteArray {
    return try {
        Base64.decode(this, Base64.DEFAULT)
    } catch (e: Exception) {
        e.printStackTrace()
        byteArrayOf()
    }
}