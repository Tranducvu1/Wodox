package com.wodox.core.base.fragment


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.wodox.core.base.viewmodel.BaseUiStateViewModel
import kotlin.reflect.KClass

abstract class BaseBottomSheetDialogFragment<VB : ViewDataBinding, VM : ViewModel>(
    private val viewModelClass: KClass<VM>
) : BottomSheetDialogFragment() {

    private var _binding: VB? = null
    protected val binding get() = _binding!!

    protected lateinit var viewModel: VM

    abstract fun layoutId(): Int
    abstract fun initialize()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[viewModelClass.java]
    }

    override fun onStart() {
        super.onStart()

        val bottomSheet = dialog?.findViewById<View>(
            com.google.android.material.R.id.design_bottom_sheet
        ) ?: return
        bottomSheet.background = null
        binding.root.clipToOutline = true
        val behavior = BottomSheetBehavior.from(bottomSheet)

        val height = (requireContext().screenHeight * 0.9f).toInt()

        bottomSheet.layoutParams = bottomSheet.layoutParams.apply {
            this.height = height
        }

        behavior.apply {
            peekHeight = height
            state = BottomSheetBehavior.STATE_EXPANDED
            skipCollapsed = true
            isDraggable = true
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, layoutId(), container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (viewModel is BaseUiStateViewModel<*, *, *>) {
            (viewModel as BaseUiStateViewModel<*, *, *>).data = arguments
        }
        initialize()
    }

    protected fun setSize(width: Int, height: Int) {
        dialog?.window?.setLayout(width, height)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

/**
 * Screen height extension
 */
val Context.screenHeight: Int
    get() = resources.displayMetrics.heightPixels