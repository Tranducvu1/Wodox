package com.wodox.main.ui.main.aiassistant

import android.app.Application
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.wodox.core.base.viewmodel.BaseUiStateViewModel
import com.wodox.core.extension.serializable
import com.wodox.domain.base.Result
import com.wodox.domain.home.model.local.AiChat
import com.wodox.domain.home.model.local.Task
import com.wodox.domain.home.usecase.aichat.DeleteAllChatsByTaskUseCase
import com.wodox.domain.home.usecase.aichat.DeleteChatHistoryUseCase
import com.wodox.domain.home.usecase.aichat.GetChatsByTaskIdUseCase
import com.wodox.domain.home.usecase.aichat.SaveChatHistoryParams
import com.wodox.domain.home.usecase.aichat.SaveChatHistoryUseCase
import com.wodox.domain.home.usecase.task.AskAIUseCase
import com.wodox.model.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AIBottomSheetViewModel @Inject constructor(
    val app: Application,
    private val askAIUseCase: AskAIUseCase,
    private val saveChatHistoryUseCase: SaveChatHistoryUseCase,
    private val getChatsByTaskIdUseCase: GetChatsByTaskIdUseCase,
    private val deleteChatHistoryUseCase: DeleteChatHistoryUseCase,
    private val deleteAllChatsByTaskUseCase: DeleteAllChatsByTaskUseCase,
) : BaseUiStateViewModel<AIBottomSheetUiState, AIBottomSheetUiEvent, AIBottomSheetUiAction>(app) {

    private val _chatHistory = MutableStateFlow<List<AiChat>>(emptyList())
    val chatHistory: StateFlow<List<AiChat>> = _chatHistory.asStateFlow()

    override fun initialState(): AIBottomSheetUiState = AIBottomSheetUiState()

    val task by lazy {
        data?.serializable<Task>(Constants.Intents.TASK)
    }

    override fun onCreate() {
        super.onCreate()
        loadChatHistory()
        val text = "Hello, I have a question"
        handleAskAI(text)

    }


    override fun handleAction(action: AIBottomSheetUiAction) {
        super.handleAction(action)
        when (action) {
            is AIBottomSheetUiAction.SendMessage -> handleAskAI(action.message)
            AIBottomSheetUiAction.LoadHistory -> loadChatHistory()
        }
    }

    private fun handleAskAI(message: String) {
        viewModelScope.launch {
            askAIUseCase(message).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        updateState { it.copy(isLoading = true, error = null) }
                    }

                    is Result.Success -> {
                        val response = result.data
                        updateState {
                            it.copy(
                                isLoading = false, aiResponse = response, error = null
                            )
                        }
                        response?.let {
                            saveChatToDatabase(message, it)
                            sendEvent(AIBottomSheetUiEvent.HandleGenerate(it))
                        }
                    }

                    is Result.Error -> {
                        updateState {
                            it.copy(
                                isLoading = false, error = result.toString()
                            )
                        }
                        sendEvent(AIBottomSheetUiEvent.Error(result.toString()))
                    }
                }
            }
        }
    }

    private fun saveChatToDatabase(userMessage: String, aiResponse: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val params = SaveChatHistoryParams(
                userMessage = userMessage, aiResponse = aiResponse, taskId = task?.id?.toString()
            )
            saveChatHistoryUseCase(params)
            loadChatHistory()
        }
    }

    private fun loadChatHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            val taskId = task?.id?.toString() ?: return@launch
            Log.d("AiResponseVM", "Loading chat history for taskId: $taskId")
            getChatsByTaskIdUseCase(taskId).collect { chats ->
                Log.d("AiResponseVM", "Received chats: $chats")
                _chatHistory.value = chats
            }
        }
    }

    fun deleteChat(chatId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteChatHistoryUseCase(chatId)
            loadChatHistory()
        }
    }

    fun deleteAllChats() {
        viewModelScope.launch(Dispatchers.IO) {
            val taskId = task?.id?.toString() ?: return@launch
            deleteAllChatsByTaskUseCase(taskId)
            _chatHistory.value = emptyList()
        }
    }
}
