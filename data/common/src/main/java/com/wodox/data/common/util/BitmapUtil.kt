package com.starnest.data.common.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory


class BitmapUtil {
    companion object {
        fun getBitmapAsset(context: Context, name: String, subfolder: String? = null, imageResize: Int? = null): Bitmap? {
            return try {
                val inputStream = if (subfolder != null) {
                    context.assets.open("$subfolder/$name")
                } else {
                    context.assets.open(name)
                }

                if (imageResize != null) {
                    val opts = BitmapFactory.Options()
                    opts.inJustDecodeBounds = false
                    opts.inSampleSize = imageResize
                    BitmapFactory.decodeStream(inputStream, null, opts)
                } else {
                    BitmapFactory.decodeStream(inputStream)
                }

            } catch (e: Exception) {
                null
            }
        }
    }
}