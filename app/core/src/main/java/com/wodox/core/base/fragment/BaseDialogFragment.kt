package com.wodox.core.base.fragment


import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wodox.core.base.viewmodel.BaseUiStateViewModel
import kotlin.reflect.KClass


abstract class BaseDialogFragment<VB : ViewDataBinding, VM : ViewModel>(
    private val viewModelClass: KClass<VM>
) : DialogFragment() {

    private var _binding: VB? = null
    protected val binding: VB
        get() = _binding
            ?: throw IllegalStateException("Binding is only valid between onCreateView and onDestroyView")

    protected lateinit var viewModel: VM
        private set

    private var dialogWidth: Int = ViewGroup.LayoutParams.WRAP_CONTENT
    private var dialogHeight: Int = ViewGroup.LayoutParams.WRAP_CONTENT


    abstract fun layoutId(): Int

    abstract fun initialize()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        // Remove dialog title
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)

        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, layoutId(), container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        viewModel = ViewModelProvider(this)[viewModelClass.java]

        if (viewModel is BaseUiStateViewModel<*, *, *>) {
            (viewModel as BaseUiStateViewModel<*, *, *>).data = arguments
        }
        // Setup binding lifecycle
        binding.lifecycleOwner = viewLifecycleOwner

        // Setup dialog window properties
        setupDialog()

        // Call child class initialization
        initialize()
    }

    override fun onStart() {
        super.onStart()

        // Apply custom dialog size if set
        dialog?.window?.setLayout(dialogWidth, dialogHeight)
    }


    private fun setupDialog() {
        dialog?.window?.apply {
            // Set transparent background for rounded corners
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            // Set default width to match parent with padding
            setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }

    protected fun setSize(width: Int, height: Int) {
        this.dialogWidth = width
        this.dialogHeight = height
    }


    override fun setCancelable(cancelable: Boolean) {
        isCancelable = cancelable
    }

    protected fun setCanceledOnTouchOutside(cancel: Boolean) {
        dialog?.setCanceledOnTouchOutside(cancel)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    fun showSafely(fragmentManager: FragmentManager, tag: String? = null) {
        if (!isAdded && !isVisible && !isRemoving) {
            try {
                show(fragmentManager, tag ?: this::class.java.simpleName)
            } catch (e: IllegalStateException) {
                // Fragment manager has been destroyed
                e.printStackTrace()
            }
        }
    }


    fun dismissSafely() {
        try {
            if (isAdded && !isRemoving) {
                dismissAllowingStateLoss()
            }
        } catch (e: IllegalStateException) {
            // Fragment not attached
            e.printStackTrace()
        }
    }
}