package com.wodox.common.extension

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.DisplayMetrics
import android.util.Size
import android.view.WindowInsets
import android.view.WindowManager
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import com.wodox.common.ui.dialog.AppDialogFragment
import com.wodox.core.app.AbstractApplication
import com.wodox.core.extension.color
import com.wodox.core.extension.showAllowingStateLoss

val Context.currentActivity: FragmentActivity?
    get() = (applicationContext as? AbstractApplication)?.currentActivity

fun Context.screenSize(): Size {
    val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val windowMetrics = windowManager.currentWindowMetrics
        val insets = windowMetrics.windowInsets
            .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
        val bounds = windowMetrics.bounds

        Size(
            bounds.width() - insets.left - insets.right,
            bounds.height() - insets.top - insets.bottom
        )
    } else {
        val displayMetrics = DisplayMetrics()
        @Suppress("DEPRECATION")
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        Size(displayMetrics.widthPixels, displayMetrics.heightPixels)
    }
}

fun Context.showMoreColor(
    defaultColor: Int = Color.BLACK,
    success: (Int) -> Unit,
    cancel: (() -> Unit)? = null
) {
    val activity = this as? FragmentActivity ?: return

    val dialog = ColorPickerDialog.newBuilder()
        .setColor(defaultColor)
        .setShowAlphaSlider(true)
        .setDialogId(1001)
        .create()

    dialog.setColorPickerDialogListener(object : ColorPickerDialogListener {
        override fun onColorSelected(dialogId: Int, color: Int) {
            success.invoke(color)
        }

        override fun onDialogDismissed(dialogId: Int) {
            cancel?.invoke()
        }
    })

    dialog.showAllowingStateLoss(activity.supportFragmentManager,"")
}


fun Context.showDefaultDialog(
    fragmentManager: FragmentManager,
    title: String = "",
    message: String = "",
    positiveTitle: String = getString(com.wodox.core.R.string.ok),
    positiveCallback: (() -> Unit)? = null,
    negativeTitle: String? = null,
    negativeCallback: (() -> Unit)? = null,
    dismissCallback: (() -> Unit)? = null,
    isDismissClickOutside: Boolean = true,
    maxWidth: Int = -1,
    isDeleteDialog: Boolean = false
) {

    AppDialogFragment.newInstance(
        title, message, positiveTitle, negativeTitle,
        callback = object : AppDialogFragment.DialogCallback {
            override fun onNegativeClick() {
                negativeCallback?.invoke()
            }

            override fun onPositiveClick() {
                positiveCallback?.invoke()
            }

            override fun onCancel() {
                dismissCallback?.invoke()
            }
        },
        isDismissClickOutside = isDismissClickOutside,
        maxWidth = maxWidth,
        isDeleteDialog = isDeleteDialog
    ).apply {
        showAllowingStateLoss(fragmentManager, "")
    }
}


