package com.wodox.mywork.ui

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.wodox.core.base.fragment.BaseFragment
import com.wodox.core.extension.addSpaceDecoration
import com.wodox.core.extension.launchWhenStarted
import com.wodox.domain.home.model.local.TaskStatus
import com.wodox.domain.mywork.TaskSortType
import com.wodox.domain.mywork.TaskViewType
import com.wodox.mywork.R
import com.wodox.mywork.databinding.DialogFilterOptionBinding
import com.wodox.mywork.databinding.DialogSettingsMenuBinding
import com.wodox.mywork.databinding.DialogSortOptionBinding
import com.wodox.mywork.databinding.MyWorkFragmentLayoutBinding
import com.wodox.mywork.model.DayItem
import dagger.hilt.android.AndroidEntryPoint
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import java.util.UUID

@AndroidEntryPoint
class MyWorkFragment : BaseFragment<MyWorkFragmentLayoutBinding, MyWorkViewModel>(
    MyWorkViewModel::class
) {

    private val adapterPagingTask by lazy {
        TaskAdapter(context, object : TaskAdapter.OnItemClickListener {})
    }

    private val adapterPagingDay by lazy {
        DayAdapter(context, object : DayAdapter.OnItemClickListener {
            override fun onDayClicked(date: Date) {
                scrollToTaskForDate(date)
            }
        })
    }

    override fun initialize() {
        setUpRecycleView()
        setUpRecycleViewDay()
        setupUI()
    }

    private fun setupUI() {
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        setupSettingsButton()
        observeUiState()
        observeUiEvent()
    }

    private fun setupSettingsButton() {
        binding.btnSettings.setOnClickListener {
            showSettingsMenu()
        }
    }

    private fun showSettingsMenu() {

        val binding = DialogSettingsMenuBinding.inflate(layoutInflater)

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Options")
            .setView(binding.root)
            .create()

        binding.cardFilter.setOnClickListener {
            dialog.dismiss()
            showFilterOptions()
        }

        binding.cardSort.setOnClickListener {
            dialog.dismiss()
            showSortOptions()
        }

        binding.cardReminder.setOnClickListener {
            dialog.dismiss()
            showSendReminderDialog()
        }

        dialog.show()
    }

    private fun showFilterOptions() {
        val binding = DialogFilterOptionBinding.inflate(layoutInflater)

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Filter Tasks")
            .setView(binding.root)
            .create()

        binding.optionAll.setOnClickListener {
            binding.radioAll.isChecked = true
            viewModel.handleAction(MyWorkUiAction.FilterByStatus(null))
            dialog.dismiss()
        }

        binding.optionCompleted.setOnClickListener {
            binding.radioCompleted.isChecked = true
            viewModel.handleAction(
                MyWorkUiAction.FilterByStatus(TaskStatus.DONE)
            )
            dialog.dismiss()
        }

        binding.optionToDo.setOnClickListener {
            binding.radioToDo.isChecked = true
            viewModel.handleAction(
                MyWorkUiAction.FilterByStatus(TaskStatus.TODO)
            )
            dialog.dismiss()
        }

        binding.optionInProgress.setOnClickListener {
            binding.radioInProgress.isChecked = true
            viewModel.handleAction(
                MyWorkUiAction.FilterByStatus(TaskStatus.IN_PROGRESS)
            )
            dialog.dismiss()
        }

        binding.optionBlocked.setOnClickListener {
            binding.radioBlocked.isChecked = true
            viewModel.handleAction(
                MyWorkUiAction.FilterByStatus(TaskStatus.BLOCKED)
            )
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showSortOptions() {
        val binding = DialogSortOptionBinding.inflate(layoutInflater)

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Sort Tasks")
            .setView(binding.root)
            .create()

        binding.optionDueDate.setOnClickListener {
            binding.radioDueDate.isChecked = true
            viewModel.handleAction(
                MyWorkUiAction.SortTasks(TaskSortType.BY_DATE)
            )
            dialog.dismiss()
        }

        binding.optionPriority.setOnClickListener {
            binding.radioPriority.isChecked = true
            viewModel.handleAction(
                MyWorkUiAction.SortTasks(TaskSortType.BY_PRIORITY)
            )
            dialog.dismiss()
        }

        binding.optionName.setOnClickListener {
            binding.radioName.isChecked = true
            viewModel.handleAction(
                MyWorkUiAction.SortTasks(TaskSortType.BY_NAME)
            )
            dialog.dismiss()
        }

        dialog.show()
    }


    private fun showSendReminderDialog() {
        val tasks = viewModel.uiState.value.tasks
        val task = tasks.firstOrNull() ?: run {
            showMessage("No tasks available to send reminders")
            return
        }

        val dialog = SendReminderDialogFragment.newInstance(
            task.id,
            task.title
        )
        dialog.show(childFragmentManager, "SendReminderDialog")
    }

    private fun observeUiState() {
        launchWhenStarted {
            viewModel.uiState.collect { state ->
                adapterPagingTask.updateList(state.tasks)

                if (state.latestComment != null) {
                    showCommentNotification(state.latestComment)
                } else {
                    hideCommentNotification()
                }

                if (state.error != null) {
                    showError(state.error)
                }
            }
        }
    }

    private fun observeUiEvent() {
        launchWhenStarted {
            viewModel.uiEvent.collect { event ->
                when (event) {
                    is MyWorkUiEvent.ShowMessage -> {
                        showMessage(event.message)
                    }

                    is MyWorkUiEvent.ShowError -> {
                        showError(event.error)
                    }

                    is MyWorkUiEvent.ShowReminderDialog -> {
                        val dialog = SendReminderDialogFragment.newInstance(
                            UUID.fromString(event.taskId),
                            event.taskName
                        )
                        dialog.show(childFragmentManager, "SendReminderDialog")
                    }

                    is MyWorkUiEvent.NotificationSent -> {
                        showMessage(event.message)
                    }

                    MyWorkUiEvent.CloseDialog -> {
                        // Close any open dialogs
                    }
                }
            }
        }
    }

    private fun showCommentNotification(comment: com.wodox.domain.home.model.local.Comment) {
        binding.tvCommentNotification.apply {
            visibility = View.VISIBLE
            text = "${comment.userName}: ${comment.content}\n${comment.createdAt}"
        }
    }

    private fun hideCommentNotification() {
        binding.tvCommentNotification.visibility = View.GONE
    }

    private fun showMessage(message: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(message)
            .setPositiveButton("OK") { _, _ -> }
            .show()
    }

    private fun showError(error: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Error")
            .setMessage(error)
            .setPositiveButton("OK") { _, _ -> }
            .show()
    }

    override fun layoutId(): Int = R.layout.my_work_fragment_layout

    private fun setUpRecycleViewDay() {
        val spacing = resources.getDimensionPixelSize(com.wodox.core.R.dimen.dp_6)

        binding.rvDay.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            adapter = adapterPagingDay
            addSpaceDecoration(spacing, false)
        }

        val days = mutableListOf<DayItem>()
        for (i in 0 until 14) {
            val cal = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_YEAR, i)
            days.add(DayItem(cal.time, isSelected = i == 0))
        }
        adapterPagingDay.updateList(days)
    }

    private fun setUpRecycleView() {
        val spacing = resources.getDimensionPixelSize(com.wodox.core.R.dimen.dp_2)

        binding.rvTaskDay.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = adapterPagingTask
            addSpaceDecoration(spacing, false)
        }
    }

    private fun scrollToTaskForDate(date: Date) {
        val sortedTasks = adapterPagingTask.list
        val index = sortedTasks.indexOfFirst { task ->
            val startAt = task.startAt ?: return@indexOfFirst false
            val startDate = startAt.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            val targetDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            startDate == targetDate
        }
        if (index != -1) {
            binding.rvTaskDay.smoothScrollToPosition(index)
        }
    }

    companion object {
        fun newInstance() = MyWorkFragment()
    }
}