package com.wodox.core.base.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.wodox.core.base.viewmodel.BaseUiStateViewModel
import kotlin.reflect.KClass

abstract class BaseFragment<VB : ViewBinding, VM : ViewModel>(
    private val vmClass: KClass<VM>
) : Fragment() {

    protected lateinit var binding: VB

    protected val viewModel: VM by lazy {
        ViewModelProvider(this)[vmClass.java]
    }

    @LayoutRes
    abstract fun layoutId(): Int

    abstract fun initialize()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val method = bindingClass().getMethod(
            "inflate",
            LayoutInflater::class.java,
            ViewGroup::class.java,
            Boolean::class.java
        )
        @Suppress("UNCHECKED_CAST")
        binding = method.invoke(null, inflater, container, false) as VB
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (viewModel is BaseUiStateViewModel<*, *, *>) {
            (viewModel as BaseUiStateViewModel<*, *, *>).data = arguments
        }

        initialize()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    @Suppress("UNCHECKED_CAST")
    private fun bindingClass(): Class<*> {
        val superclass = this::class.java.genericSuperclass as java.lang.reflect.ParameterizedType
        return superclass.actualTypeArguments[0] as Class<*>
    }
}