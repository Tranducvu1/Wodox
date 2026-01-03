package com.wodox.auth.ui.sigin

import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import com.wodox.core.extension.debounceClick
import com.wodox.core.extension.gone
import com.wodox.core.extension.launchWhenStarted
import com.wodox.core.extension.show
import dagger.hilt.android.AndroidEntryPoint
import com.wodox.auth.R
import com.wodox.auth.databinding.ActivtySignInOutLayoutBinding
import com.wodox.common.navigation.AuthNavigator
import com.wodox.common.navigation.MainNavigator
import com.wodox.core.base.activity.BaseActivity
import com.wodox.core.extension.toast
import javax.inject.Inject

@AndroidEntryPoint
class SignInActivity : BaseActivity<ActivtySignInOutLayoutBinding, SignUpViewModel>(
    SignUpViewModel::class
) {
    @Inject
    lateinit var mainNavigator: MainNavigator

    @Inject
    lateinit var authNavigator: AuthNavigator
    override fun layoutId(): Int = R.layout.activty_sign_in_out_layout

    override fun initialize() {
        setupUI()
        setupAction()
        observer()
    }

    private fun setupUI() {
        if (viewModel.isShowSignUp) {
            binding.etFullName.show()
            binding.btnSignIn.text = getString(com.wodox.resources.R.string.sign_up)
            binding.tvSignInTitle.text = getString(com.wodox.resources.R.string.sign_up)
        } else {
            binding.etFullName.gone()
            binding.btnSignIn.text = getString(com.wodox.resources.R.string.sign_in)
        }
    }

    private fun setupAction() {
        binding.ivBack.debounceClick {
            authNavigator.showWelcomeClear(context = this@SignInActivity)
        }
        binding.btnSignIn.isEnabled = false
        binding.btnSignIn.alpha = 0.5f

        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

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
                val password = binding.etPassword.text.toString().trim()
                val isEnabled = email.isNotEmpty() && password.isNotEmpty()
                binding.btnSignIn.isEnabled = isEnabled
                binding.btnSignIn.alpha = if (isEnabled) 1f else 0.5f
            }
        }
        binding.etEmail.addTextChangedListener(watcher)
        binding.etPassword.addTextChangedListener(watcher)
        binding.btnSignIn.debounceClick {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val fullName = binding.etFullName.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this@SignInActivity, "Please fill in all fields", Toast.LENGTH_SHORT)
                    .show()
                return@debounceClick
            }

            if (viewModel.isShowSignUp) {
                viewModel.dispatch(SignUpUiAction.SignUp(email, password, fullName))
            } else {
                viewModel.dispatch(SignUpUiAction.SignIn(email, password))
            }
        }
    }

    private fun observer() {
        launchWhenStarted {
            viewModel.uiEvent.collect { event ->
                when (event) {
                    is SignUpUiEvent.Loading -> {
                        showLoading(event.loading)
                    }

                    is SignUpUiEvent.Success -> {
                        showLoading(false)
                        Toast.makeText(this@SignInActivity, event.message, Toast.LENGTH_SHORT)
                            .show()
                        mainNavigator.showMain(this@SignInActivity, true)
                    }

                    is SignUpUiEvent.Error -> {
                        showLoading(false)
                        toast("Inccorect email or password.Please try again")
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.loadingOverlay.visibility =
            if (isLoading) View.VISIBLE else View.GONE
        binding.llInput.visibility =
            if (!isLoading) View.VISIBLE else View.GONE
    }
}
