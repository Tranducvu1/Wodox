package com.wodox.core.extension

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.OpenableColumns
import java.io.File

fun Uri.saveTo(context: Context, fileName: String, folderPath: String): File? {
    return try {
        val folder = File(folderPath)
        if (!folder.exists()) {
            folder.mkdirs()
        }

        val file = File(folder, fileName)

        val inputStream = context.contentResolver.openInputStream(this) ?: return null

        inputStream.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        file
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}


fun Bitmap.saveTo(file: File): Boolean {
    return try {
        file.outputStream().use { output ->
            this.compress(Bitmap.CompressFormat.PNG, 100, output)
        }
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

fun Uri.fileName(context: Context): String? {
    return when (this.scheme) {
        "content" -> {
            var fileName = "Unknown"
            context.contentResolver.query(this, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (cursor.moveToFirst() && nameIndex != -1) {
                    fileName = cursor.getString(nameIndex)
                }
            }
            fileName
        }
        "file" -> {
            this.path?.substringAfterLast('/') ?: "Unknown"
        }
        else -> "Unknown"
    }
}