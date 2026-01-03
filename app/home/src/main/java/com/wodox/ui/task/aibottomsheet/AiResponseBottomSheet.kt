package com.wodox.ui.task.aibottomsheet

import android.os.Bundle
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wodox.core.extension.debounceClick
import com.wodox.core.base.fragment.BaseBottomSheetDialogFragment
import com.wodox.core.extension.addSpaceDecoration
import com.wodox.core.extension.launchWhenStarted
import com.wodox.core.util.showKeyboard
import com.wodox.domain.home.model.local.Task
import com.wodox.home.R
import com.wodox.home.databinding.FragmentAiBottomSheetBinding
import com.wodox.model.Constants
import com.wodox.model.Message
import dagger.hilt.android.AndroidEntryPoint
import java.util.Date

@AndroidEntryPoint
class AiResponseBottomSheet :
    BaseBottomSheetDialogFragment<FragmentAiBottomSheetBinding, AiResponseViewModel>(AiResponseViewModel::class) {

    private lateinit var adapterMessage: MessageAdapter

    override fun layoutId(): Int = R.layout.fragment_ai_bottom_sheet

    override fun initialize() {
        setupAdapter()
        setupUi()
        setupAction()
        observer()
        setupRecycleView()
        loadChatHistoryFromDatabase()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    private fun setupAdapter() {
        adapterMessage = MessageAdapter(
            context = requireContext(),
            onTypingComplete = {
                binding.rvMessages.scrollToPosition(adapterMessage.list.size - 1)
            },
            onDeleteMessage = { position ->
                viewModel.deleteChat(position.toString())
            }
        )
    }

    private fun setupUi() {
        binding.etInput.requestFocus()
        binding.etInput.showKeyboard()
    }

    private fun setupAction() {
        binding.etInput.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEND) {
                sendUserMessage()
                true
            } else false
        }

        binding.ivSend.debounceClick {
            sendUserMessage()
        }
        binding.ivBack.debounceClick {
            dismissAllowingStateLoss()
        }
        binding.ivMenu.debounceClick {
            showDeleteAllDialog()
        }
    }

    private fun sendUserMessage() {
        val message = binding.etInput.text.toString().trim()
        if (message.isNotEmpty()) {
            val userMessage = Message(
                text = message,
                isUser = true,
                timestamp = System.currentTimeMillis()
            )
            adapterMessage.addMessage(userMessage)
            binding.rvMessages.scrollToPosition(adapterMessage.list.size - 1)
            viewModel.dispatch(AiResponseUiAction.SendMessage(message))
            binding.etInput.text?.clear()
        }
    }

    private fun observer() {
        // ✅ Observe AI responses
        launchWhenStarted {
            viewModel.uiEvent.collect { event ->
                when (event) {
                    is AiResponseUiEvent.HandleGenerate -> {
                        showAiResponseWithTyping(event.message)
                    }

                    is AiResponseUiEvent.Error -> {
                        showAiResponse("An error has occurred: ${event.error}")
                    }
                }
            }
        }

        // ✅ Observe chat history changes
        launchWhenStarted {
            viewModel.chatHistory.collect { chats ->
                if (chats.isNotEmpty()) {
                    // Sort by timestamp
                    val sortedChats = chats.sortedBy { it.timestamp }

                    // Convert to Message list
                    val messages = sortedChats.flatMap { chat ->
                        listOf(
                            Message(
                                text = chat.userMessage,
                                isUser = true,
                                timestamp = chat.timestamp
                            ),
                            Message(
                                text = chat.aiResponse,
                                isUser = false,
                                timestamp = chat.timestamp + 1
                            )
                        )
                    }

                    adapterMessage.updateMessages(messages)
                    binding.rvMessages.scrollToPosition(adapterMessage.list.size - 1)
                }
            }
        }
    }

    private fun loadChatHistoryFromDatabase() {
        viewModel.dispatch(AiResponseUiAction.LoadHistory)
    }

    private fun showAiResponseWithTyping(message: String) {
        val aiMessage = Message(
            text = message,
            isUser = false,
            timestamp = System.currentTimeMillis()
        )
        adapterMessage.addAIMessageWithTyping(aiMessage.text)
    }

    private fun showAiResponse(message: String) {
        val aiMessage = Message(
            text = message,
            isUser = false,
            timestamp = System.currentTimeMillis()
        )
        adapterMessage.addMessage(aiMessage)
        binding.rvMessages.scrollToPosition(adapterMessage.list.size - 1)
    }

    private fun setupRecycleView() {
        binding.lifecycleOwner = this
        val spacing = resources.getDimensionPixelSize(com.wodox.core.R.dimen.dp_6)

        binding.rvMessages.apply {
            layoutManager = LinearLayoutManager(
                context,
                RecyclerView.VERTICAL,
                false
            )
            adapter = adapterMessage
            addSpaceDecoration(spacing, false)
        }
    }

    private fun showDeleteAllDialog() {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Delete All Messages?")
            .setMessage("Are you sure you want to delete all chat history?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteAllChats()
                adapterMessage.clear()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapterMessage.cleanup()
    }

    companion object {
        @JvmStatic
        fun newInstance(task: Task?) = AiResponseBottomSheet().apply {
            arguments = Bundle().apply {
                putSerializable(Constants.Intents.TASK, task)
            }
        }
    }
}
