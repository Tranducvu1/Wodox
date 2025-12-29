package com.wodox.core.extension

import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.OnApplyWindowInsetsListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * Observe keyboard visibility changes (Android 11+)
 */
@RequiresApi(Build.VERSION_CODES.R)
fun View.keyboardVisibilityChanges(): Flow<Boolean> = callbackFlow {

    val listener = OnApplyWindowInsetsListener { _, insets ->
        val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
        val isKeyboardVisible = imeInsets.bottom > 0
        trySend(isKeyboardVisible).isSuccess
        insets
    }

    // Attach listener
    ViewCompat.setOnApplyWindowInsetsListener(this@keyboardVisibilityChanges, listener)

    // Emit initial state
    ViewCompat.getRootWindowInsets(this@keyboardVisibilityChanges)?.let { insets ->
        val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
        trySend(imeInsets.bottom > 0)
    } ?: trySend(false)

    // Clear listener on close
    awaitClose {
        ViewCompat.setOnApplyWindowInsetsListener(this@keyboardVisibilityChanges, null)
    }

}.distinctUntilChanged()

/**
 * Get keyboard height (Android 11+)
 */
@RequiresApi(Build.VERSION_CODES.R)
fun View.getKeyboardHeight(): Int {
    val windowInsets = ViewCompat.getRootWindowInsets(this) ?: return 0
    val imeInsets = windowInsets.getInsets(WindowInsetsCompat.Type.ime())
    val navInsets = windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars())
    return maxOf(0, imeInsets.bottom - navInsets.bottom)
}

/**
 * Check if keyboard is visible (Android 11+)
 */
@RequiresApi(Build.VERSION_CODES.R)
fun View.isKeyboardVisible(): Boolean {
    val windowInsets = ViewCompat.getRootWindowInsets(this) ?: return false
    val imeInsets = windowInsets.getInsets(WindowInsetsCompat.Type.ime())
    return imeInsets.bottom > 0
}
