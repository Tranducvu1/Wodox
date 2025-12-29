package com.wodox.ui.task.taskdetail.createtask

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wodox.core.extension.debounceClick
import com.wodox.core.extension.show
import com.wodox.common.ui.dialog.FileData
import com.wodox.common.ui.dialog.MediaPickerDialogFragment
import com.wodox.common.ui.menuview.MenuOption
import com.wodox.common.ui.menuview.MenuOptionListener
import com.wodox.common.ui.menuview.MenuView
import com.wodox.common.util.KeyboardUtil
import com.wodox.config.model.Config
import com.wodox.core.base.fragment.BaseBottomSheetDialogFragment
import com.wodox.core.extension.addSpaceDecoration
import com.wodox.core.extension.launchWhenStarted
import com.wodox.core.extension.screenHeight
import com.wodox.core.extension.showAllowingStateLoss
import com.wodox.core.extension.toast
import com.wodox.domain.home.model.local.Attachment
import com.wodox.domain.home.model.local.AttachmentType
import com.wodox.domain.home.model.local.Difficulty
import com.wodox.domain.home.model.local.Priority
import com.wodox.domain.home.model.local.Task
import com.wodox.home.R
import com.wodox.home.databinding.FragmentCreateTaskBottomSheetBinding
import com.wodox.ui.task.taskdetail.AttachmentsAdapter
import com.wodox.ui.task.taskdetail.TaskDetailUiAction
import com.wodox.ui.task.taskdetail.attachment.AttachmentDialogFragment
import com.wodox.ui.task.taskdetail.dialogDayPicker.TaskDatePickerDialogFragment
import com.wodox.ui.task.userbottomsheet.ListUserBottomSheet
import com.wodox.util.TaskMenuUtil
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

@AndroidEntryPoint
class CreateTaskBottomSheet :
    BaseBottomSheetDialogFragment<FragmentCreateTaskBottomSheetBinding, CreateTaskViewModel>(
        CreateTaskViewModel::class
    ) {

    private var selectedStartDate: Long? = null
    private var selectedEndDate: Long? = null
    private var selectedPriority: Int = 3
    private var selectedDifficulty: Int = 3


    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                handleSelectedFile(uri, AttachmentType.IMAGE)
            }
        }
    }

    private val videoPickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                handleSelectedFile(uri, AttachmentType.VIDEO)
            }
        }
    }

    private val filePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                handleSelectedFile(uri, AttachmentType.FILE)
            }
        }
    }

    private val audioPickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                handleSelectedFile(uri, AttachmentType.AUDIO)
            }
        }
    }

    private val adapterAttachments by lazy {
        AttachmentsAdapter(
            context,
            object : AttachmentsAdapter.OnItemClickListener {
                override fun onAttachmentClick(attachment: Attachment) {
                    //openAttachment(attachment)
                }

                override fun onDeleteClick(attachment: Attachment) {
                    deleteAttachment(attachment)
                }
            }
        )
    }

    override fun layoutId() = R.layout.fragment_create_task_bottom_sheet

    override fun initialize() {
        setupAction()
        setupUI()
        observe()
    }

    private fun setupUI() {
        setupRecyclerView()
        KeyboardUtil.listenerKeyboardVisibleForAndroid15AndAbove(
            requireActivity(),
            binding.container
        )
        showTask()
    }


    private fun setupAction() {
        binding.ivClose.debounceClick {
            dismissAllowingStateLoss()
        }
        binding.tvCreate.debounceClick {
            saveTask()
        }
        binding.llSupport.debounceClick {
            viewModel.dispatch(CreateTaskUiAction.AnalyzeUserSkill)
        }
        binding.llAssignees.debounceClick {
            val bottomSheet = ListUserBottomSheet.newInstance()
            bottomSheet.listener = object : ListUserBottomSheet.OnItemClickListener {
                override fun onClick(id: UUID) {
                    this@CreateTaskBottomSheet.viewModel.dispatch(CreateTaskUiAction.AssignUser(id))
                }
            }
            bottomSheet.showAllowingStateLoss(childFragmentManager)
        }
        binding.llDate.debounceClick {
            TaskDatePickerDialogFragment().apply {
                onDateSelected = { start, end ->
                    selectedStartDate = start
                    selectedEndDate = end
                    binding.tvDate.text = formatDate(start, end)
                }
            }.show(childFragmentManager, "taskDatePicker")
        }
        binding.llPriority.debounceClick {
            val menus = TaskMenuUtil.getItemMenusPriority(requireContext())
            MenuView.show(requireContext(), binding.tvPriority, menus, object : MenuOptionListener {
                override fun onClick(menu: MenuOption) {
                    binding.llItemMenuPriority.show()
                    binding.tvPrioritySelected.text = getString(menu.nameResId)
                    binding.imgPriorityIcon.setImageResource(menu.iconResId)
                    menu.tintColor?.let {
                        binding.imgPriorityIcon.imageTintList = ColorStateList.valueOf(it)
                    } ?: run {
                        binding.imgPriorityIcon.imageTintList = null
                    }
                    selectedPriority = when (menu.type.name) {
                        "LOW" -> 1
                        "NORMAL" -> 3
                        "HIGH" -> 5
                        else -> 3
                    }
                }
            })
        }

        binding.llDifficulty.debounceClick {
            val menus = TaskMenuUtil.getItemMenusDifficulty(requireContext())
            MenuView.show(
                requireContext(),
                binding.tvDifficulty,
                menus,
                object : MenuOptionListener {
                    override fun onClick(menu: MenuOption) {
                        binding.llItemMenuDifficulty.show()
                        binding.tvDifficultySelected.text = getString(menu.nameResId)
                        binding.imgDifficultyIcon.setImageResource(menu.iconResId)
                        menu.tintColor?.let {
                            binding.imgDifficultyIcon.imageTintList = ColorStateList.valueOf(it)
                        } ?: run {
                            binding.imgDifficultyIcon.imageTintList = null
                        }

                        selectedDifficulty = when (menu.type.name) {
                            "VERY_EASY" -> 1
                            "EASY" -> 2
                            "NORMAL" -> 3
                            "HARD" -> 5
                            "VERY_HARD" -> 7
                            "EXPERT" -> 10
                            else -> 3
                        }
                        viewModel.dispatch(CreateTaskUiAction.UpdateDifficulty(selectedDifficulty,menu.type.name))
                    }
                })
        }

        binding.llAttachments.debounceClick {
            val dialog = AttachmentDialogFragment.newInstance(
                this@CreateTaskBottomSheet.viewModel.currentTask.value?.id
            )
            dialog.onAttachmentSelected = { type ->
                when (type) {
                    AttachmentType.IMAGE -> openImagePicker()
                    AttachmentType.VIDEO -> openVideoPicker()
                    AttachmentType.FILE -> openFilePicker()
                    AttachmentType.AUDIO -> openAudioPicker()
                }
            }
            dialog.showAllowingStateLoss(childFragmentManager, "")
        }
    }

    private fun formatDate(startAt: Long?, dueAt: Long?): String {
        if (startAt == null || dueAt == null) return ""
        val startAt = Calendar.getInstance().apply {
            timeInMillis = startAt
        }
        val dueAt = Calendar.getInstance().apply {
            timeInMillis = dueAt
        }
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)

        val startYear = startAt.get(Calendar.YEAR)
        val dueYear = dueAt.get(Calendar.YEAR)

        val startDateTimeFormat = if (startYear == currentYear) {
            SimpleDateFormat("MMM dd", Locale.getDefault())
        } else {
            SimpleDateFormat("MMM dd,YYYY", Locale.getDefault())
        }

        val dueDateTimeFormat = if (dueYear == currentYear) {
            SimpleDateFormat("MMM dd", Locale.getDefault())
        } else {
            SimpleDateFormat("MMM dd,YYYY", Locale.getDefault())
        }

        val timeFormat = SimpleDateFormat("h:ma", Locale.getDefault())

        val startDateStr = startDateTimeFormat.format(startAt.time)
        val dueDateStr = dueDateTimeFormat.format(startAt.time)
        val dueTimeStr = timeFormat.format(startAt.time)

        return "$startDateStr - $dueDateStr $dueTimeStr"
    }

    private fun showTask() {
        val task = viewModel.uiState.value.tasks
        val isEditing = task != null
        binding.tvTitle.show(isEditing)
        binding.etTitle.show(!isEditing)
        binding.tvDescription.show(isEditing)
        binding.etDescription.show(!isEditing)
        binding.tvDate.text =
            if (viewModel.uiState.value.tasks?.startAt == null || viewModel.uiState.value.tasks?.dueAt == null) {
                getString(com.wodox.resources.R.string.add_dates)
            } else {
                formatDate(
                    viewModel.uiState.value.tasks?.startAt?.time,
                    viewModel.uiState.value.tasks?.dueAt?.time
                )
            }
    }

    private fun saveTask() {
        val title = binding.etTitle.text?.toString()?.trim() ?: return
        val description = binding.etDescription.text?.toString()?.trim() ?: ""
        if (title.isEmpty()) {
            requireContext().toast("Please enter a task title")
            return
        }

        val startDate = selectedStartDate?.let {
            Date(it)
        }

        val dueDate = selectedEndDate?.let { Date(it) }

        val priority = Priority.fromValue(selectedPriority)
        val difficulty = Difficulty.fromValue(selectedDifficulty)

        val task = Task(
            id = UUID.randomUUID(),
            title = title,
            description = description,
            priority = priority,
            difficulty = difficulty,
            startAt = startDate,
            dueAt = dueDate,
            ownerId = UUID.randomUUID()
        )

        viewModel.dispatch(CreateTaskUiAction.SaveTask(task))
    }


    private fun setupRecyclerView() {
        val spacing = resources.getDimensionPixelSize(com.wodox.core.R.dimen.dp_8)

        binding.rvAttachments.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = adapterAttachments
            addSpaceDecoration(spacing, true)
        }
    }

    private fun openImagePicker() {
        if (!isAdded) return
        val pickerDialog = MediaPickerDialogFragment.newInstance(
            shouldResizeImage = true, folder = Config.Folder.AI_COMPANION
        )
        pickerDialog.listener =
            object : MediaPickerDialogFragment.OnMediaPickerDialogFragmentListener {
                override fun onPick(files: List<FileData>) {
                    files.firstOrNull()?.let { fileData ->
                        val uri = Uri.fromFile(fileData.file)
                        handleSelectedFile(uri, AttachmentType.IMAGE)
                    }
                }

                override fun onDismiss() {
                }
            }
        pickerDialog.showAllowingStateLoss(childFragmentManager)
    }


    private fun openAudioPicker() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "audio/*"
        }
        audioPickerLauncher.launch(intent)
    }


    private fun observe() {
        launchWhenStarted {
            viewModel.uiEvent.collect { event ->
                when (event) {
                    is CreateTaskUiEvent.SaveSuccess -> {
                        requireContext().toast("Task saved successfully")
                        dismissAllowingStateLoss()
                    }

                    is CreateTaskUiEvent.DeleteSuccess -> {
                        requireContext().toast("Attachment deleted")
                    }

                    is CreateTaskUiEvent.Error -> {
                        requireContext().toast(event.message)
                    }

                    CreateTaskUiEvent.AssignSuccess -> {
                        requireContext().toast("Assign Successfully")
                    }

                    is CreateTaskUiEvent.AnalysisComplete -> {
                        val result = event.result
                        Log.d("CreateTaskBottomSheet", "═══════════════════════════════════════")
                        Log.d("CreateTaskBottomSheet", "🎯 Analysis Complete Event Received")
                        Log.d("CreateTaskBottomSheet", "═══════════════════════════════════════")
                        Log.d("CreateTaskBottomSheet", "📊 Total Tasks: ${result.totalTasks}")
                        Log.d("CreateTaskBottomSheet", "✅ Completed: ${result.completedTasks}")
                        Log.d("CreateTaskBottomSheet", "⏰ On-Time: ${result.onTimeTasks}")
                        Log.d("CreateTaskBottomSheet", "⏱️ Late: ${result.lateTasks}")
                        Log.d(
                            "CreateTaskBottomSheet",
                            "⭐ Skill Score: ${String.format("%.2f", result.skillScore)}/10"
                        )
                        Log.d(
                            "CreateTaskBottomSheet",
                            "🏆 Skill Level: ${result.suggestedLevel.displayName}"
                        )
                        Log.d("CreateTaskBottomSheet", "💡 Insights Count: ${result.insights.size}")
                        result.insights.forEachIndexed { index, insight ->
                            Log.d("CreateTaskBottomSheet", "   ${index + 1}. $insight")
                        }
                        Log.d("CreateTaskBottomSheet", "═══════════════════════════════════════")

                        requireContext().toast("Analysis completed! Skill Level: ${result.suggestedLevel.displayName}")
                    }

                    is CreateTaskUiEvent.SupportersLoaded -> {
                        val supporters = event.supporters
                        Log.d("CreateTaskBottomSheet", "═══════════════════════════════════════")
                        Log.d("CreateTaskBottomSheet", "👥 Supporters Loaded Event Received")
                        Log.d("CreateTaskBottomSheet", "═══════════════════════════════════════")
                        Log.d("CreateTaskBottomSheet", "📋 Total Supporters: ${supporters.size}")
                        supporters.forEachIndexed { index, user ->
                            Log.d("CreateTaskBottomSheet", "   ${index + 1}. Name: ${user.name}")
                            Log.d("CreateTaskBottomSheet", "      • Email: ${user.email}")
                            Log.d(
                                "CreateTaskBottomSheet",
                                "      • Skill Level: ${user.skillLevel.displayName}"
                            )
                            Log.d("CreateTaskBottomSheet", "      • User ID: ${user.id}")
                        }
                        Log.d("CreateTaskBottomSheet", "═══════════════════════════════════════")

                        // Optional: Show toast to user
                        if (supporters.isNotEmpty()) {
                            requireContext().toast("Found ${supporters.size} suitable supporters")
                        } else {
                            requireContext().toast("No supporters found for this task")
                        }
                    }
                }
            }
        }
    }


    private fun handleSelectedFile(uri: Uri, type: AttachmentType) {
        val resolver = context?.contentResolver ?: return
        val (name) = when (uri.scheme) {
            "content" -> {
                var fileName = "Unknown"
                resolver.query(uri, null, null, null, null)?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (cursor.moveToFirst() && nameIndex != -1) {
                        fileName = cursor.getString(nameIndex)
                    }
                }
                val inputStream = resolver.openInputStream(uri)
                val tempFile = File(context?.cacheDir, fileName)
                inputStream?.use { input ->
                    tempFile.outputStream().use { output -> input.copyTo(output) }
                }

                fileName to tempFile
            }

            "file" -> {
                val path = uri.path ?: return
                val fileName = path.substringAfterLast('/')
                val fileObj = File(path)
                fileName to fileObj
            }

            else -> "Unknown" to null
        }
        val taskID = viewModel.currentTask.value?.id
        val attachment = Attachment(
            id = UUID.randomUUID(),
            name = name,
            uri = uri.path,
            type = type,
        )
        val currentList = adapterAttachments.list
        currentList.add(attachment)
        adapterAttachments.list = currentList
    }

    private fun openVideoPicker() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "video/*"
        }
        videoPickerLauncher.launch(intent)
    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "*/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        filePickerLauncher.launch(intent)
    }


    private fun deleteAttachment(attachment: Attachment) {
        val currentList = adapterAttachments.list
        currentList.remove(attachment)
        adapterAttachments.list = currentList
        adapterAttachments.notifyDataSetChanged()
        viewModel.dispatch(CreateTaskUiAction.DeleteAttachment(attachment))
    }

    companion object {
        @JvmStatic
        fun newInstance() = CreateTaskBottomSheet()
    }
}