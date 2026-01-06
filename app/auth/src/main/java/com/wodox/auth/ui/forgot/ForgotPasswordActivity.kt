package com.wodox.auth.ui.forgot

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import com.wodox.core.extension.debounceClick
import com.wodox.core.extension.launchWhenStarted
import dagger.hilt.android.AndroidEntryPoint
import com.wodox.auth.R
import com.wodox.auth.databinding.ActivityForgotPasswordLayoutBinding
import com.wodox.common.navigation.AuthNavigator
import com.wodox.core.base.activity.BaseActivity
import com.wodox.core.extension.show
import com.wodox.core.extension.toast
import javax.inject.Inject

@AndroidEntryPoint
class ForgotPasswordActivity :
    BaseActivity<ActivityForgotPasswordLayoutBinding, ForgotPasswordViewModel>(
        ForgotPasswordViewModel::class
    ) {
    @Inject
    lateinit var authNavigator: AuthNavigator

    override fun layoutId(): Int = R.layout.activity_forgot_password_layout

    override fun initialize() {
        setupUI()
        setupAction()
        observer()
    }

    private fun setupUI() {
        binding.btnResetPassword.isEnabled = false
        binding.btnResetPassword.alpha = 0.5f
    }

    private fun setupAction() {
        binding.ivBack.debounceClick {
            authNavigator.showSignInClear(context = this@ForgotPasswordActivity)
        }

        val emailWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                val email = binding.etEmail.text.toString().trim()
                val isEnabled = email.isNotEmpty()
                binding.btnResetPassword.isEnabled = isEnabled
                binding.btnResetPassword.alpha = if (isEnabled) 1f else 0.5f
            }
        }

        binding.etEmail.addTextChangedListener(emailWatcher)
        binding.tvBackToSignIn.debounceClick {
            authNavigator.showSignInClear(this@ForgotPasswordActivity)
        }
        binding.ivBack.debounceClick {
            finish()
        }
        binding.btnResetPassword.debounceClick {
            val email = binding.etEmail.text.toString().trim()

            if (email.isEmpty()) {
                Toast.makeText(
                    this@ForgotPasswordActivity,
                    "Please enter your email",
                    Toast.LENGTH_SHORT
                )
                    .show()
                return@debounceClick
            }

            viewModel.dispatch(ForgotPasswordUiAction.SendResetEmail(email))
        }
    }

    private fun observer() {
        launchWhenStarted {
            viewModel.uiEvent.collect { event ->
                when (event) {
                    is ForgotPasswordUiEvent.Loading -> {
                        showLoading(event.loading)
                    }

                    is ForgotPasswordUiEvent.Success -> {
                        showLoading(false)
                        toast(event.message)
                        authNavigator.showSignInClear(context = this@ForgotPasswordActivity)
                    }

                    is ForgotPasswordUiEvent.Error -> {
                        showLoading(false)
                        toast(event.error)
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.loadingOverlay.show(isLoading)
        binding.llInput.show(!isLoading)
    }
}