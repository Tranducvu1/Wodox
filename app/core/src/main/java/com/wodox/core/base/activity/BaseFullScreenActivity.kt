package com.wodox.core.base.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import kotlin.reflect.KClass


abstract class BaseFullScreenActivity<S : ViewDataBinding, VM : Any>(
    private val viewModelClass: KClass<VM>
) : AppCompatActivity() {

    protected lateinit var binding: S
    protected lateinit var viewModel: VM
    protected val sharePrefs: SharedPreferences? by lazy {
        getSharedPreferences(
            "app_prefs",
            MODE_PRIVATE
        )
    }

    abstract val contentView: View

    abstract fun layoutId(): Int

    abstract fun initialize()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set full screen
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        // Initialize DataBinding
        binding = DataBindingUtil.setContentView(this, layoutId())
        binding.lifecycleOwner = this

        // Initialize
        initialize()
    }


    protected inline fun <reified T : AppCompatActivity> openActivity() {
        startActivity(Intent(this, T::class.java))
    }

    protected inline fun <reified T : AppCompatActivity> openActivity(
        extras: Bundle.() -> Unit
    ) {
        val intent = Intent(this, T::class.java)
        intent.putExtras(Bundle().apply(extras))
        startActivity(intent)
    }


    protected fun View.show() {
        this.visibility = View.VISIBLE
    }

    protected fun View.hide() {
        this.visibility = View.GONE
    }

    protected fun View.invisible() {
        this.visibility = View.INVISIBLE
    }

    protected fun View.isVisible(): Boolean {
        return this.visibility == View.VISIBLE
    }
}