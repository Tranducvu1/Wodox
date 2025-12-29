package com.wodox.common.ui.dialog


import android.content.DialogInterface
import android.os.Bundle
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.wodox.resources.R
import com.wodox.common.databinding.FragmentAppDialogBinding
import com.wodox.core.base.fragment.BaseDialogFragment
import com.wodox.core.base.viewmodel.BaseViewModel
import com.wodox.core.extension.debounceClick
import com.wodox.core.extension.gone
import com.wodox.core.extension.screenWidth
import com.wodox.core.extension.show
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.min

@AndroidEntryPoint
class AppDialogFragment : BaseDialogFragment<FragmentAppDialogBinding, BaseViewModel>(
    BaseViewModel::class
) {

    private var dialogCallback: DialogCallback? = null

    private val title by lazy { arguments?.getString(TITLE_DIALOG) }

    private val messageDialog by lazy { arguments?.getString(MESSAGE_DIALOG) }

    private val positiveTitle by lazy { arguments?.getString(POSITIVE_TITLE) }

    private val negativeTitle by lazy { arguments?.getString(NEGATIVE_TITLE) }

    private val isDismissOutside by lazy { arguments?.getBoolean(DISMISS_OUTSIDE) }
    private val isDeleteDialog by lazy { arguments?.getBoolean(IS_DELETE_DIALOG) == true }

    private val maxWidth by lazy { arguments?.getInt(MAX_WIDTH) ?: -1 }


    interface DialogCallback {
        fun onPositiveClick() {

        }

        fun onNegativeClick() {

        }

        fun onCancel() {

        }
    }

    override fun layoutId(): Int = com.wodox.common.R.layout.fragment_app_dialog

    override fun initialize() {
        setupView()
        setupAction()
    }

    private fun setupView() {
        val width =
            requireActivity().screenWidth - requireContext().resources.getDimension(com.wodox.core.R.dimen.dp_24)
                .toInt()

        setSize(
            min(
                width, if (maxWidth == -1) {
                    requireContext().resources.getDimension(com.wodox.core.R.dimen.dp_400)
                        .toInt()
                } else {
                    maxWidth
                }
            ),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        binding.apply {
            tvTitle.text = title
            setText(positiveTitle, tvPositive)
            setText(negativeTitle, tvNegative)
            setText(messageDialog, tvMessage)
            isCancelable = isDismissOutside == true

            tvTitle.gone(title.isNullOrEmpty())
            tvMessage.gone(messageDialog.isNullOrEmpty())
            shadowNegative.gone(negativeTitle.isNullOrEmpty())
            shadowPositive.gone(positiveTitle.isNullOrEmpty())


            ctContainer.background = ContextCompat.getDrawable(
                this@AppDialogFragment.requireContext(), if (isDeleteDialog) {
                    R.drawable.bg_dialog_delete
                } else {
                    R.drawable.bg_dialog_app
                }
            )
            if (isDeleteDialog) {
                val background = ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.bg_button_delete_dialog
                )

                tvPositive.background = background
            }
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)

        dialogCallback?.onCancel()
    }

    fun setText(text: String?, textView: TextView) {
        text?.let {
            textView.show()
            textView.text = it
        }
    }

    private fun setupAction() {
        binding.apply {
            tvPositive.debounceClick {
                dialogCallback?.onPositiveClick()
                dismissAllowingStateLoss()
            }
            tvNegative.debounceClick {
                dialogCallback?.onNegativeClick()
                dismissAllowingStateLoss()
            }
        }
    }

    companion object {
        const val MESSAGE_DIALOG = "MESSAGE_DIALOG"
        const val TITLE_DIALOG = "TITLE_DIALOG"
        const val POSITIVE_TITLE = "POSITIVE_TITLE"
        const val NEGATIVE_TITLE = "NEGATIVE_TITLE"
        const val DISMISS_OUTSIDE = "DISMISS_OUTSIDE"
        const val IS_DELETE_DIALOG = "IS_DELETE_DIALOG"
        const val MAX_WIDTH = "MAX_WIDTH"

        @JvmStatic
        fun newInstance(
            title: String? = null,
            message: String? = null,
            positiveTitle: String = "Ok",
            negativeTitle: String? = null,
            callback: DialogCallback? = null,
            isDismissClickOutside: Boolean = false,
            isDeleteDialog: Boolean = false,
            maxWidth: Int = -1,
        ) = AppDialogFragment().apply {
            arguments = Bundle().apply {
                putString(TITLE_DIALOG, title)
                putString(MESSAGE_DIALOG, message)
                putString(POSITIVE_TITLE, positiveTitle)
                putString(NEGATIVE_TITLE, negativeTitle)
                putBoolean(DISMISS_OUTSIDE, isDismissClickOutside)
                putBoolean(IS_DELETE_DIALOG, isDeleteDialog)
                putInt(MAX_WIDTH, maxWidth)
            }
            dialogCallback = callback
        }
    }
}