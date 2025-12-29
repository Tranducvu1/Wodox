package com.starnest.data.common.util

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log

class DrawableUtil {
    companion object {
        fun getDrawableAsset(context: Context, name: String, subfolder: String? = null): Drawable? {
            return try {
                val inputStream = if (subfolder != null) {
                    context.assets.open("$subfolder/$name")
                } else {
                    context.assets.open(name)
                }

                Drawable.createFromStream(inputStream, null)
            } catch (e: Exception) {
                Log.d("getDrawableAsset", "$name $subfolder")
                e.printStackTrace()
                null
            }
        }
    }
}