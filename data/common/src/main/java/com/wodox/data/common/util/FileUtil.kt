package com.starnest.data.common.util

import android.content.Context
import androidx.annotation.RawRes
import java.io.*


class FileUtil {
    companion object {
        fun loadJsonFromRaw(context: Context, @RawRes resId: Int): String {
            val raw = context.resources.openRawResource(resId)
            val writer: Writer = StringWriter()
            val buffer = CharArray(1024)
            raw.use { rawData ->
                val reader: Reader = BufferedReader(InputStreamReader(rawData, "UTF-8"))
                var n: Int
                while (reader.read(buffer).also { n = it } != -1) {
                    writer.write(buffer, 0, n)
                }
            }

            return writer.toString()
        }

        fun loadJsonFromAsset(context: Context, fileName: String): String {
            val raw = context.resources.assets.open(fileName)
            val writer: Writer = StringWriter()
            val buffer = CharArray(1024)
            raw.use { rawData ->
                val reader: Reader = BufferedReader(InputStreamReader(rawData, "UTF-8"))
                var n: Int
                while (reader.read(buffer).also { n = it } != -1) {
                    writer.write(buffer, 0, n)
                }
            }

            return writer.toString()
        }

        fun deleteFile(filePath: String) {
            val file = File(filePath)
            file.delete()
        }

        fun deleteFile(file: File) {
            file.delete()
        }

        fun ensureExistDir(dir: String) {
            val file = File(dir)

            if (!file.exists()) {
                file.mkdirs()
            }
        }

        fun ensureExistFile(filePath: String) {
            val file = File(filePath)

            if (!file.exists()) {
                file.createNewFile()
            }
        }
    }
}