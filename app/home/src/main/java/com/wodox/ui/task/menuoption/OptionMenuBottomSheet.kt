package com.wodox.ui.task.menuoption

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.dynamiclinks.androidParameters
import com.google.firebase.dynamiclinks.dynamicLinks
import com.google.firebase.dynamiclinks.shortLinkAsync
import com.wodox.common.navigation.HomeNavigator
import com.wodox.domain.home.model.local.ItemMenu
import com.wodox.domain.home.model.local.MenuOption
import com.wodox.domain.home.model.local.Task
import com.wodox.home.R
import com.wodox.home.databinding.FragmentOptionMenuBottomSheetBinding
import com.wodox.model.Constants
import com.wodox.notification.ReminderScheduler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import androidx.core.net.toUri
import com.google.firebase.dynamiclinks.socialMetaTagParameters
import com.wodox.core.extension.debounceClick
import com.wodox.core.base.fragment.BaseBottomSheetDialogFragment
import com.wodox.core.extension.addSpaceDecoration
import com.wodox.core.extension.launchWhenStarted
import com.wodox.core.extension.screenHeight
import com.wodox.core.extension.toast

@AndroidEntryPoint
class OptionMenuBottomSheet :
    BaseBottomSheetDialogFragment<FragmentOptionMenuBottomSheetBinding, OptionMenuUiViewModel>(
        OptionMenuUiViewModel::class
    ) {
    @Inject
    lateinit var homeNavigator: HomeNavigator

    interface TaskDeleteListener {
        fun onTaskDeleted()
    }

    var taskDeleteListener: TaskDeleteListener? = null

    private val subTaskAdapter by lazy {
        MenuOptionAdapter(
            context,
            object : MenuOptionAdapter.OnItemClickListener {
                override fun onClick(menuOption: MenuOption) {
                    when (menuOption.type) {
                        ItemMenu.REMIND -> {
                            viewModel.dispatch(OptionMenuUiAction.RemindTask)
                        }

                        ItemMenu.DUPLICATE -> {
                            viewModel.dispatch(OptionMenuUiAction.DuplicateTask)
                        }

                        ItemMenu.SHARE -> {
                            viewModel.task?.let { task -> shareTask(task) }
                        }

                        ItemMenu.DELETE -> {
                            viewModel.dispatch(OptionMenuUiAction.DeleteTask)
                        }

                        else -> {

                        }
                    }
                }
            }
        )
    }

    override fun initialize() {
        setupUi()
        setupAction()
        observer()
    }

    override fun layoutId(): Int = R.layout.fragment_option_menu_bottom_sheet

    private fun setupUi() {
        binding.root.layoutParams = binding.root.layoutParams.apply {
            height = (requireActivity().screenHeight * 1)
        }
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        setupRecycleView()
    }

    private fun setupAction() {
        binding.apply {
            ivClose.debounceClick {
                dismissAllowingStateLoss()
            }
        }
    }

    private fun setupRecycleView() {
        val spacing = resources.getDimensionPixelSize(com.wodox.core.R.dimen.dp_4)

        binding.rvItem.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = subTaskAdapter
            addSpaceDecoration(spacing, true)
        }
    }

    private fun observer() {
        launchWhenStarted {
            viewModel.uiEvent.collect { event ->
                when (event) {
                    is OptionMenuUiEvent.DeleteSuccess -> {
                        handleDeleteSuccess()
                    }

                    is OptionMenuUiEvent.DuplicateSuccess -> {
                        context?.toast("Duplicate Task Successfully")
                    }

                    is OptionMenuUiEvent.RemindSuccess -> {
                        val task = viewModel.task ?: return@collect

                        event.startTime?.let { time ->
                            ReminderScheduler.scheduleReminder(
                                requireContext(),
                                task.id.toString(),
                                "Task starts: ${task.title}",
                                time
                            )
                        }

                        event.dueTime?.let { time ->
                            ReminderScheduler.scheduleReminder(
                                requireContext(),
                                task.id.toString(),
                                "Task due: ${task.title}",
                                time
                            )
                        }

                        context?.toast("Reminder scheduled!")
                    }
                }
            }
        }
    }

    private fun handleDeleteSuccess() {
        context?.toast("Delete Task Successfully")
        taskDeleteListener?.onTaskDeleted()
        binding.root.postDelayed({
            dismissAllowingStateLoss()
        }, 200)
    }

    private fun shareTask(task: Task) {
        val context = requireContext()

        lifecycleScope.launch {
            try {
                context.toast("Creating share link...")

                val inviteToken = UUID.randomUUID().toString()
                val link = withContext(Dispatchers.IO) {
                    createInviteLink(task.id, inviteToken)
                }

                val shareText = buildShareText(task, link)

                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, shareText)
                    putExtra(Intent.EXTRA_SUBJECT, "Task Invitation: ${task.title}")
                }
                startActivity(Intent.createChooser(intent, "Share Task"))
            } catch (ex: Exception) {
                withContext(Dispatchers.Main) {
                    context.toast("Failed to share task: ${ex.localizedMessage}")
                }
            }
        }
    }

    private fun buildShareText(task: Task, link: String): String {
        return buildString {
            appendLine("📋 Task: ${task.title}")
            appendLine()

            if (!task.description.isNullOrBlank()) {
                appendLine("📝 Description:")
                appendLine(task.description)
                appendLine()
            }

            appendLine("🔗 Join Task:")
            appendLine(link)
            appendLine()
            appendLine("-- Sent from Wodox App --")
        }
    }

    private suspend fun createInviteLink(taskId: UUID, token: String): String {
        val deepLink = "https://wodox.app/task?taskId=$taskId&token=$token"

        val shortLinkResult = Firebase.dynamicLinks.shortLinkAsync {
            link = deepLink.toUri()
            domainUriPrefix = "https://wodox.page.link"

            androidParameters {
                minimumVersion = 1
            }
            socialMetaTagParameters {
                title = "Task Invitation"
                description = "You've been invited to collaborate"
            }
        }.await()

        return shortLinkResult.shortLink?.toString()
            ?: throw Exception("Failed to create short link")
    }

    companion object {
        fun newInstance(task: Task?) = OptionMenuBottomSheet().apply {
            arguments = Bundle().apply {
                putSerializable(Constants.Intents.TASK, task)
            }
        }
    }
}