package com.wodox.ui.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.map
import com.wodox.domain.chat.usecase.GetMarkTaskNotificationsReadUseCase
import com.wodox.core.base.viewmodel.BaseUiStateViewModel
import com.wodox.core.base.viewmodel.EmptyUiEvent
import com.wodox.domain.home.model.local.Task
import com.wodox.domain.home.usecase.GetAllTaskUseCase
import com.wodox.domain.home.usecase.GetTaskByTaskIdUseCase
import com.wodox.domain.home.usecase.SaveTaskUseCase
import com.wodox.domain.home.usecase.taskassign.GetTaskAssignByUserIdUseCase
import com.wodox.domain.user.usecase.GetUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    val app: Application,
    private val getAllTaskUseCase: GetAllTaskUseCase,
    private val saveTaskUseCase: SaveTaskUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val getTaskAssignByUserIdUseCase: GetTaskAssignByUserIdUseCase,
    private val getTaskByTaskIdUseCase: GetTaskByTaskIdUseCase,
    private val getMarkTaskNotificationsReadUseCase: GetMarkTaskNotificationsReadUseCase,
) : BaseUiStateViewModel<HomeUiState, EmptyUiEvent, HomeUiAction>(app) {

    private val searchQueryFlow = MutableStateFlow("")
    private val refreshTrigger = MutableStateFlow(0)

    // Thay thế phần taskFlow trong HomeViewModel
    val taskFlow: Flow<PagingData<Task>> = flow {
        val userId = getUserUseCase() ?: run {
            Log.e("HomeViewModel", "❌ getUserUseCase returned null!")
            return@flow
        }

        Log.d("HomeViewModel", "✅ Current User ID: $userId")

        refreshTrigger
            .flatMapLatest {
                Log.d("HomeViewModel", "🔄 Refresh triggered")

                searchQueryFlow.flatMapLatest { query ->
                    Log.d("HomeViewModel", "🔍 Search Query: '$query'")

                    getAllTaskUseCase(userId).map { pagingData ->
                        Log.d("HomeViewModel", "📦 Received paging data from getAllTaskUseCase")

                        val allTaskAssignments = getTaskAssignByUserIdUseCase(userId) ?: emptySet()
                        val assignedTaskIds = allTaskAssignments.map { it.taskId }.toSet()

                        Log.d("HomeViewModel", "📌 Assigned Task IDs (${assignedTaskIds.size}): $assignedTaskIds")

                        var originalCount = 0
                        var afterSearchCount = 0
                        var afterOwnershipCount = 0

                        // Log và track tasks
                        var trackedData = pagingData.map { task ->
                            originalCount++
                            Log.d("HomeViewModel", "📄 Task #$originalCount: '${task.title}' (Priority Score: %.2f)".format(task.calculatedPriority))
                            task
                        }
                        Log.d("HomeViewModel", "📊 Total original tasks: $originalCount")

                        // Filter by search query
                        trackedData = if (query.isEmpty()) {
                            Log.d("HomeViewModel", "⚡ No search query - keeping all tasks")
                            trackedData
                        } else {
                            trackedData.filter { task ->
                                val matches = task.title.contains(query, ignoreCase = true)
                                if (matches) {
                                    afterSearchCount++
                                    Log.d("HomeViewModel", "✓ Search match #$afterSearchCount: '${task.title}'")
                                } else {
                                    Log.d("HomeViewModel", "✗ Search no match: '${task.title}'")
                                }
                                matches
                            }
                        }

                        if (query.isNotEmpty()) {
                            Log.d("HomeViewModel", "📊 After search filter: $afterSearchCount tasks")
                        }

                        // Filter by ownership
                        trackedData = trackedData.filter { task ->
                            val isOwner = task.ownerId == userId
                            val isAssigned = assignedTaskIds.contains(task.id)
                            val shouldShow = isOwner || isAssigned

                            if (shouldShow) {
                                afterOwnershipCount++
                                Log.d(
                                    "HomeViewModel",
                                    "✅ Task SHOWN #$afterOwnershipCount: '${task.title}'\n" +
                                            "   └─ Priority Score: %.2f\n".format(task.calculatedPriority) +
                                            "   └─ ID: ${task.id}\n" +
                                            "   └─ Is Owner: $isOwner | Is Assigned: $isAssigned"
                                )
                            }

                            shouldShow
                        }

                        // ✅ SẮP XẾP LẠI THEO PRIORITY TRƯỚC KHI TRẢ VỀ
                        trackedData = trackedData.map { task ->
                            // Sắp xếp ngay trong map transformation
                            task
                        }.map { pagingData ->
                            // Dùng sortedByDescending để đảm bảo thứ tự
                            pagingData
                        }

                        Log.d("HomeViewModel", """
                        ═══════════════════════════════════════
                        📈 SUMMARY:
                        - Original tasks: $originalCount
                        - After search: ${if (query.isEmpty()) "N/A" else "$afterSearchCount"}
                        - After ownership: $afterOwnershipCount
                        - Tasks are sorted by calculated priority
                        ═══════════════════════════════════════
                    """.trimIndent())

                        trackedData
                    }
                }
            }
            .collect { emit(it) }
    }.cachedIn(viewModelScope)

    override fun handleAction(action: HomeUiAction) {
        when (action) {
            is HomeUiAction.UpdateFavourite -> handleFavourite(action.task)
            is HomeUiAction.UpdateSearchQuery -> updateSearchQuery(action.query)
        }
    }

    override fun initialState(): HomeUiState = HomeUiState()

    private fun handleFavourite(task: Task) {
        val newTask = task.copy(isFavourite = !task.isFavourite)
        viewModelScope.launch(Dispatchers.IO) {
            saveTaskUseCase(newTask)
        }
    }

    private fun updateSearchQuery(query: String) {
        Log.d("HomeViewModel", "🔍 Updating search query from '${searchQueryFlow.value}' to '$query'")
        searchQueryFlow.value = query
    }
}