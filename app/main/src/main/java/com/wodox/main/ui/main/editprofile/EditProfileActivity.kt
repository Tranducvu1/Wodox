package com.wodox.main.ui.main.editprofile

import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import com.wodox.core.base.activity.BaseActivity
import com.wodox.core.extension.debounceClick
import com.wodox.core.extension.launchWhenStarted
import com.wodox.core.extension.toast
import com.wodox.main.R
import com.wodox.main.databinding.ActivityEditProfileBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditProfileActivity : BaseActivity<ActivityEditProfileBinding, EditProfileViewModel>(
    EditProfileViewModel::class
) {
    override fun layoutId(): Int = R.layout.activity_edit_profile

    override fun initialize() {
        setupUI()
        setupAction()
        observer()
    }

    private fun setupUI() {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
    }

    private fun setupAction() {
        binding.apply {
            ivClose.debounceClick {
                finish()
            }

            btnSave.debounceClick {
                val fullName = edtFullName.text.toString().trim()
                val email = edtEmail.text.toString().trim()
                val phone = edtPhone.text.toString().trim()
                val bio = edtBio.text.toString().trim()
                if (validateInput(fullName, email, phone)) {
                    this@EditProfileActivity.viewModel.dispatch(
                        EditProfileUiAction.UpdateProfile(
                            fullName = fullName,
                            email = email,
                            phone = phone,
                            bio = bio,
                        )
                    )
                }
            }

            edtFullName.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            edtEmail.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            edtPhone.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {}
            })
        }
    }

    private fun observer() {
        launchWhenStarted {
            viewModel.uiState.collect { state ->
                binding.apply {
                    edtFullName.setText(state.fullName)
                    edtEmail.setText(state.email)
                    edtPhone.setText(state.phone)
                    edtBio.setText(state.bio)
                    tvAvatarLetter.text = state.email.firstOrNull()?.uppercase() ?: "U"
                }
            }
        }

        launchWhenStarted {
            viewModel.uiEvent.collect { event ->
                when (event) {
                    is EditProfileUiEvent.ShowError -> {
                        toast(event.message)
                    }

                    EditProfileUiEvent.UpdateSuccess -> {
                        toast("Profile updated successfully")
                        finish()
                    }

                    is EditProfileUiEvent.Loading -> {
                        binding.btnSave.isEnabled = !event.isLoading
                        binding.progressBar.visibility =
                            if (event.isLoading) View.VISIBLE else View.GONE
                    }
                }
            }
        }
    }

    private fun validateInput(fullName: String, email: String, phone: String): Boolean {
        return when {
            fullName.isEmpty() -> {
                toast("Full name cannot be empty")
                false
            }

            email.isEmpty() -> {
                toast("Email cannot be empty")
                false
            }

            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                toast("Invalid email format")
                false
            }

            phone.isEmpty() -> {
                toast("Phone cannot be empty")
                false
            }

            phone.length < 10 -> {
                toast("Phone number must be at least 10 digits")
                false
            }

            else -> true
        }
    }
}