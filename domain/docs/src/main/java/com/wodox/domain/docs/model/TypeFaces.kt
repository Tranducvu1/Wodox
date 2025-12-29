package com.wodox.domain.docs.model

import android.content.Context
import android.graphics.Typeface
import java.util.Hashtable


object TypeFaces {
    private val cache = Hashtable<String, Typeface>()
    fun getTypeFace(context: Context, assetPath: String): Typeface? {
        synchronized(cache) {
            if (!cache.containsKey(assetPath)) {
                try {
                    val typeFace = Typeface.createFromAsset(
                        context.assets, assetPath
                    )
                    cache[assetPath] = typeFace
                } catch (e: Exception) {
                    return null
                }
            }
            return cache[assetPath]
        }
    }
}