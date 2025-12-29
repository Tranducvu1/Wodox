package com.osprey.core.extension


import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass


inline fun <reified VB : ViewBinding, reified VM : ViewModel> Fragment.viewBinding(
    vmClass: KClass<VM>? = null
): ReadOnlyProperty<Fragment, VB> = ReadOnlyProperty { _, _ ->
    val method = VB::class.java.getMethod("bind", android.view.View::class.java)
    @Suppress("UNCHECKED_CAST")
    method.invoke(null, requireView()) as VB
}


inline fun <reified VM : ViewModel> Fragment.getViewModel(
    vmClass: KClass<VM>? = null
): Lazy<VM> {
    return lazy {
        val viewModel: VM by viewModels()
        viewModel
    }
}


inline fun <reified VB : ViewBinding> Fragment.viewBindingWithVM(
    vmClass: KClass<out ViewModel>
): ReadOnlyProperty<Fragment, VB> = ReadOnlyProperty { _, _ ->
    val method = VB::class.java.getMethod("bind", android.view.View::class.java)
    @Suppress("UNCHECKED_CAST")
    method.invoke(null, requireView()) as VB
}