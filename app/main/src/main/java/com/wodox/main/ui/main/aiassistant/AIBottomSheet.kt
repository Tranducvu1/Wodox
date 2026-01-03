package com.wodox.main.ui.main.aiassistant

import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wodox.core.base.fragment.BaseBottomSheetDialogFragment
import com.wodox.core.extension.addSpaceDecoration
import com.wodox.core.extension.debounceClick
import com.wodox.core.extension.launchWhenStarted
import com.wodox.core.util.showKeyboard
import com.wodox.home.R
import com.wodox.main.databinding.AiBottomSheetFragmentBinding
import com.wodox.model.Message
import com.wodox.ui.task.aibottomsheet.MessageAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AIBottomSheet :
    BaseBottomSheetDialogFragment<AiBottomSheetFragmentBinding, AIBottomSheetViewModel>(
        AIBottomSheetViewModel::class
    ) {

    private lateinit var adapterMessage: MessageAdapter

    override fun layoutId(): Int = com.wodox.main.R.layout.ai_bottom_sheet_fragment

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
            viewModel.dispatch(AIBottomSheetUiAction.SendMessage(message))
            binding.etInput.text?.clear()
        }
    }

    private fun observer() {
        launchWhenStarted {
            viewModel.uiEvent.collect { event ->
                when (event) {
                    is AIBottomSheetUiEvent.HandleGenerate -> {
                        showAiResponseWithTyping(event.message)
                    }

                    is AIBottomSheetUiEvent.Error -> {
                        showAiResponse("An error has occurred: ${event.error}")
                    }
                }
            }
        }

        launchWhenStarted {
            viewModel.chatHistory.collect { chats ->
                if (chats.isNotEmpty()) {
                    val sortedChats = chats.sortedBy { it.timestamp }
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
        viewModel.dispatch(AIBottomSheetUiAction.LoadHistory)
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
        fun newInstance() = AIBottomSheet()
    }
}
