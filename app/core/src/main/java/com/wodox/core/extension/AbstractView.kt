package com.wodox.core.extension

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

abstract class AbstractView(
    context: Context,
    attrs: AttributeSet?
) : FrameLayout(context, attrs, 0) {

    private var _binding: ViewDataBinding? = null
    protected val binding: ViewDataBinding
        get() = _binding ?: throw IllegalStateException("Binding is not initialized")

    init {
        _binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            layoutId(),
            this,
            true
        )

        viewInitialized()
    }

    abstract fun layoutId(): Int

    open fun viewBinding(): ViewDataBinding = binding

    open fun viewInitialized() {
        // Override trong subclass
    }

    protected fun setVariable(variableId: Int, value: Any?) {
        binding.setVariable(variableId, value)
    }

    protected fun executePendingBindings() {
        binding.executePendingBindings()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        _binding?.unbind()
        _binding = null
    }
}