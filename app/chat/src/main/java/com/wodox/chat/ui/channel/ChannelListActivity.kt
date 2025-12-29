package com.wodox.chat.ui.channel

import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.wodox.chat.R
import com.wodox.chat.databinding.ActivityChannelListBinding
import com.wodox.chat.databinding.BottomSheetSearchChannelsBinding
import com.wodox.chat.databinding.DialogCreateChannelBinding
import com.wodox.common.navigation.ChatNavigator
import com.wodox.core.base.activity.BaseActivity
import com.wodox.core.extension.debounceClick
import com.wodox.core.extension.launchWhenStarted
import com.wodox.core.extension.show
import com.wodox.core.extension.toast
import com.wodox.domain.chat.model.Channel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChannelListActivity :
    BaseActivity<ActivityChannelListBinding, ChannelListViewModel>(ChannelListViewModel::class) {

    override fun layoutId(): Int = R.layout.activity_channel_list

    @Inject
    lateinit var chatNavigator: ChatNavigator

    private lateinit var channelAdapter: ChannelAdapter

    override fun initialize() {
        setupRecyclerView()
        setupActions()
        observeState()
        observeEvents()
        setupUi()
    }

    private fun setupUi() {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this@ChannelListActivity
    }

    private fun setupRecyclerView() {
        channelAdapter = ChannelAdapter(
            onChannelClick = { channel ->
                chatNavigator.openChannelChat(this, channel.id)
            },
            onJoinClick = { channel ->
                showJoinConfirmation(channel)
            }
        )

        binding.rvChannels.apply {
            layoutManager = LinearLayoutManager(this@ChannelListActivity)
            adapter = channelAdapter
        }
    }


    private fun showJoinConfirmation(channel: Channel) {
        if (channel.isJoined) {
            toast("You have already joined this channel")
            return
        }

        val message = buildString {
            append("Do you want to join ")
            append(channel.name)
            append("?")

            if (channel.isPrivate) {
                append("\n\nThis is a private channel.")
            }

            if (channel.description != null) {
                append("\n\n")
                append(channel.description)
            }
        }

        AlertDialog.Builder(this)
            .setTitle("Join Channel")
            .setMessage(message)
            .setPositiveButton("Join") { dialog, _ ->
                viewModel.dispatch(ChannelListUiAction.JoinChannel(channel.id))
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }

    private fun setupActions() {
        binding.apply {
            ivAdd.debounceClick {
                showCreateChannelDialog()
            }

            ivBack.debounceClick {
                finish()
            }

            fabCreateChannel.debounceClick {
                showCreateChannelDialog()
            }
            ivSearch.debounceClick {
                showSearchDialog()
            }

            tabLayout.addOnTabSelectedListener(object :
                com.google.android.material.tabs.TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: com.google.android.material.tabs.TabLayout.Tab?) {
                    when (tab?.position) {
                        0 -> this@ChannelListActivity.viewModel.dispatch(ChannelListUiAction.LoadAllChannels)
                        1 -> this@ChannelListActivity.viewModel.dispatch(ChannelListUiAction.LoadJoinedChannels)
                        2 -> this@ChannelListActivity.viewModel.dispatch(ChannelListUiAction.LoadMyChannels)
                    }
                }

                override fun onTabUnselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}
                override fun onTabReselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}
            })
        }
    }

    private fun showSearchDialog() {
        val bottomSheetDialog =
            BottomSheetDialog(this, com.wodox.core.R.style.BottomSheetDialogTheme)
        val binding = BottomSheetSearchChannelsBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(binding.root)

        val currentQuery = viewModel.uiState.value.searchQuery
        if (currentQuery.isNotEmpty()) {
            binding.etSearch.setText(currentQuery)
            binding.etSearch.setSelection(currentQuery.length)
            binding.btnClearText.visibility = android.view.View.VISIBLE
        }

        binding.etSearch.requestFocus()
        bottomSheetDialog.window?.setSoftInputMode(
            android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
        )

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                binding.btnClearText.visibility = if (s.isNullOrEmpty()) {
                    android.view.View.GONE
                } else {
                    android.view.View.VISIBLE
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.btnClearText.debounceClick {
            binding.etSearch.text?.clear()
        }


        binding.chipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
             when {
                binding.chipPublic.isChecked -> "public"
                binding.chipPrivate.isChecked -> "private"
                binding.chipPopular.isChecked -> "popular"
                else -> "all"
            }
        }

        binding.btnSearch.debounceClick {
            val query = binding.etSearch.text.toString().trim()

            if (query.isNotEmpty()) {
                viewModel.dispatch(ChannelListUiAction.SearchChannels(query))
                toast("🔍 Searching for: $query")
            } else {
                viewModel.dispatch(ChannelListUiAction.LoadAllChannels)
                toast("Showing all channels")
            }

            bottomSheetDialog.dismiss()
        }

        binding.btnCancel.debounceClick {
            bottomSheetDialog.dismiss()
        }

        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                binding.btnSearch.performClick()
                true
            } else {
                false
            }
        }

        bottomSheetDialog.show()
    }

    private fun showCreateChannelDialog() {
        val dialogBinding = DialogCreateChannelBinding.inflate(layoutInflater)
        val dialog = MaterialAlertDialogBuilder(this)
            .setView(dialogBinding.root)
            .create()

        dialogBinding.apply {
            btnCancel.debounceClick {
                dialog.dismiss()
            }

            btnCreate.debounceClick {
                val name = etChannelName.text.toString().trim()
                val description = etChannelDescription.text.toString().trim()
                val isPrivate = switchPrivate.isChecked

                if (name.isEmpty()) {
                    tilChannelName.error = "Channel name is required"
                    return@debounceClick
                }

                viewModel.dispatch(
                    ChannelListUiAction.CreateChannel(
                        name = name,
                        description = description.ifEmpty { null },
                        isPrivate = isPrivate
                    )
                )
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    private fun observeState() {
        launchWhenStarted {
            viewModel.uiState.collect { state ->
                channelAdapter.submitList(state.channels)

                binding.progressBar.show(state.isLoading)
                binding.layoutEmpty.show(state.channels.isEmpty() && !state.isLoading)

                if (!state.error.isNullOrEmpty()) {
                    toast(state.error)
                }

                if (state.searchQuery.isNotEmpty()) {
                    binding.tvTitle.text = "Search: ${state.searchQuery}"
                } else {
                    binding.tvTitle.text = "Channels"
                }
            }
        }
    }

    private fun observeEvents() {
        launchWhenStarted {
            viewModel.uiEvent.collect { event ->
                when (event) {
                    is ChannelListUiEvent.ChannelCreated -> {
                        toast("✅ Channel '${event.channel.name}' created successfully")
                    }

                    is ChannelListUiEvent.ChannelJoined -> {
                        toast("✅ Joined channel successfully!")
                    }

                    is ChannelListUiEvent.ChannelLeft -> {
                        toast("👋 Left channel")
                    }

                    is ChannelListUiEvent.Error -> {
                        toast(event.message)
                    }
                }
            }
        }
    }
}