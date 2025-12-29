package com.wodox.core.base.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.wodox.core.base.viewmodel.BaseUiStateViewModel
import org.greenrobot.eventbus.EventBus
import kotlin.reflect.KClass

abstract class BaseActivity<VB : ViewDataBinding, VM : BaseUiStateViewModel<*, *, *>>(
    private val viewModelClass: KClass<VM>
) : AppCompatActivity() {

    protected lateinit var binding: VB
    protected lateinit var viewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, layoutId())
        binding.lifecycleOwner = this

        viewModel = ViewModelProvider(this)[viewModelClass.java]

        viewModel.data = intent.extras

        initialize()
    }


    abstract fun layoutId(): Int

    abstract fun initialize()

    protected fun register() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    protected fun unregister() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }
}