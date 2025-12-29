package com.wodox.core.extension

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

fun DialogFragment.showAllowingStateLoss(
    manager: FragmentManager,
    tag: String?
) {
    val transaction = manager.beginTransaction()
    transaction.add(this, tag)
    transaction.commitAllowingStateLoss()
}

fun DialogFragment.showAllowingStateLoss(manager: FragmentManager) {
    showAllowingStateLoss(manager, null)
}

fun DialogFragment.showAllowingStateLoss(
    manager: FragmentManager,
    tag: String?,
    configure: (FragmentTransaction) -> Unit
) {
    val transaction = manager.beginTransaction()
    configure(transaction)
    transaction.add(this, tag)
    transaction.commitAllowingStateLoss()
}

fun Fragment.showSafe(
    manager: FragmentManager,
    tag: String?
) {
    if (!isAdded && !manager.isStateSaved) {
        val transaction = manager.beginTransaction()
        transaction.add(this, tag)
        transaction.commit()
    }
}

fun DialogFragment.dismissSafe() {
    if (isAdded) {
        try {
            dismissAllowingStateLoss()
        } catch (e: Exception) {
            // Handle exception silently
        }
    }
}