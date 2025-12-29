package com.osprey.core.extension

import android.os.Handler
import android.os.Looper

fun runDelayed(delayMillis: Long, block: () -> Unit) {
    Handler(Looper.getMainLooper()).postDelayed({
        block()
    }, delayMillis)
}