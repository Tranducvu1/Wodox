package com.wodox.core.extension

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun ensureBackgroundThread(block: suspend CoroutineScope.() -> Unit) {
    CoroutineScope(Dispatchers.IO).launch {
        block()
    }
}