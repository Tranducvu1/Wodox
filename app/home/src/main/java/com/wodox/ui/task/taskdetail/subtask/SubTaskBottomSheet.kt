package com.wodox.ui.task.taskdetail.subtask

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wodox.common.ui.dialog.FileData
import com.wodox.common.ui.dialog.MediaPickerDialogFragment
import com.wodox.common.ui.menuview.MenuOption
import com.wodox.common.ui.menuview.MenuOptionListener
import com.wodox.common.ui.menuview.MenuView
import com.wodox.common.util.KeyboardUtil
import com.wodox.config.model.Config
import com.wodox.core.base.fragment.BaseBottomSheetDialogFragment
import com.wodox.core.extension.addSpaceDecoration
import com.wodox.core.extension.debounceClick
import com.wodox.core.extension.launchWhenStarted
import com.wodox.core.extension.screenHeight
import com.wodox.core.extension.show
import com.wodox.core.extension.showAllowingStateLoss
import com.wodox.core.extension.toast
import com.wodox.domain.home.model.local.Attachment
import com.wodox.domain.home.model.local.AttachmentType
import com.wodox.domain.home.model.local.SubTask
import com.wodox.domain.home.model.local.Task
import com.wodox.home.R
import com.wodox.home.databinding.FragmentSubTaskBottomSheetLayoutBinding
import com.wodox.model.Constants
import com.wodox.ui.task.taskdetail.AttachmentsAdapter
import com.wodox.ui.task.taskdetail.attachment.AttachmentDialogFragment
import com.wodox.ui.task.taskdetail.dialogDayPicker.TaskDatePickerDialogFragment
import com.wodox.util.TaskMenuUtil
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

@AndroidEntryPoint
class SubTaskBottomSheet :
    BaseBottomSheetDialogFragment<FragmentSubTaskBottomSheetLayoutBinding, SubTaskViewModel>(
        SubTaskViewModel::class) {

    private var task: Task? = null
    private var taskId: UUID? = null
    private var selectedStartDate: Long? = null
    private var selectedEndDate: Long? = null
    private var selectedPriority: Int = 0

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

    override fun layoutId() = R.layout.fragment_sub_task_bottom_sheet_layout

    override fun initialize() {
        setupAction()
        setupUI()
        observe()
    }

    private fun setupUI() {
        setupRecyclerView()
        task = arguments?.getSerializable(Constants.Intents.TASK) as? Task
        taskId = task?.id
        binding.root.layoutParams = binding.root.layoutParams.apply {
            height = (requireActivity().screenHeight * 0.95).toInt()
        }
        KeyboardUtil.listenerKeyboardVisibleForAndroid15AndAbove(
            requireActivity(),
            binding.container
        )
        showSubTask()
    }


    private fun setupAction() {
        binding.ivClose.debounceClick {
            dismissAllowingStateLoss()
        }
        binding.tvCreate.debounceClick {
            saveSubtask()
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
            val menus = TaskMenuUtil.getItemMenusPriority(
                context = requireContext(),
            )
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

                    val priorityValue = when (menu.type.name) {
                        "LOW" -> 1
                        "NORMAL" -> 2
                        "HIGH" -> 3
                        else -> 0
                    }

                    selectedPriority = priorityValue
                }
            })
        }
        binding.llAttachments.debounceClick {
            val dialog = AttachmentDialogFragment.newInstance(
                this@SubTaskBottomSheet.viewModel.currentTask.value?.id
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

    private fun showSubTask() {
        val subTask = viewModel.uiState.value.subTasks
        val isEditing = subTask != null
        binding.tvTitle.show(isEditing)
        binding.etTitle.show(!isEditing)
        binding.tvDescription.show(isEditing)
        binding.etDescription.show(!isEditing)
        binding.tvDate.text =
            if (viewModel.uiState.value.subTasks?.startAt == null || viewModel.uiState.value.subTasks?.dueAt == null) {
                getString(com.wodox.resources.R.string.add_dates)
            } else {
                formatDate(
                    viewModel.uiState.value.subTasks?.startAt?.time,
                    viewModel.uiState.value.subTasks?.dueAt?.time
                )
            }
    }

    private fun saveSubtask() {
        val title = binding.etTitle.text?.toString()?.trim() ?: return
        val description = binding.etDescription.text?.toString()?.trim() ?: ""
        val currentTaskId = taskId
        if (currentTaskId == null) {
            requireContext().toast("Task ID not found")
            return
        }

        if (title.isEmpty()) {
            requireContext().toast("Please enter a task for the task")
            return
        }

        val startDate = selectedStartDate?.let {
            Date(it)
        }

        val dueDate = selectedEndDate?.let { Date(it) }

        val subTask = SubTask(
            taskId = currentTaskId,
            title = title,
            description = description,
            priority = selectedPriority,
            startAt = startDate,
            dueAt = dueDate
        )

        viewModel.dispatch(SubTaskUiAction.SaveSubTask(subTask))
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
                    is SubTaskUiEvent.SaveSuccess -> {
                        requireContext().toast("Subtask saved successfully")
                        dismissAllowingStateLoss()
                    }

                    is SubTaskUiEvent.DeleteSuccess -> {
                        requireContext().toast("Attachment deleted")
                    }

                    is SubTaskUiEvent.Error -> {
                        requireContext().toast(event.message)
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
        viewModel.dispatch(SubTaskUiAction.DeleteAttachment(attachment))
    }

    companion object {
        @JvmStatic
        fun newInstance(task: Task?, subTask: SubTask?) = SubTaskBottomSheet().apply {
            arguments = Bundle().apply {
                putSerializable(Constants.Intents.TASK, task)
                putSerializable(Constants.Intents.SUB_TASK, subTask)
            }
        }
    }
}