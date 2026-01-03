package com.wodox.ui.task.taskdetail

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wodox.common.navigation.HomeNavigator
import com.wodox.common.ui.dialog.FileData
import com.wodox.common.ui.dialog.MediaPickerDialogFragment
import com.wodox.common.ui.menuview.MenuOption
import com.wodox.common.ui.menuview.MenuOptionListener
import com.wodox.common.ui.menuview.MenuView
import com.wodox.domain.home.model.local.Attachment
import com.wodox.domain.home.model.local.Task
import com.wodox.domain.home.model.local.TaskStatus
import com.wodox.home.R
import com.wodox.home.databinding.FragmentTaskDetailLayoutBinding
import com.wodox.domain.home.model.local.AttachmentType
import com.wodox.model.Constants
import com.wodox.ui.task.taskdetail.attachment.AttachmentDialogFragment
import com.wodox.ui.task.taskdetail.dialogDayPicker.TaskDatePickerDialogFragment
import com.wodox.util.TaskMenuUtil
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID
import javax.inject.Inject
import com.wodox.config.model.Config
import java.io.File
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import org.greenrobot.eventbus.EventBus
import com.wodox.common.util.HtmlConverterUtil
import com.wodox.domain.home.model.local.SubTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import com.wodox.common.model.SearchEvent
import com.wodox.common.ui.taskstatuspopup.TaskStatusPopup
import com.wodox.core.base.fragment.BaseFragment
import com.wodox.core.extension.addSpaceDecoration
import com.wodox.core.extension.debounceClick
import com.wodox.core.extension.gone
import com.wodox.core.extension.launchWhenStarted
import com.wodox.core.extension.show
import com.wodox.core.extension.showAllowingStateLoss
import com.wodox.core.extension.toArrayList
import com.wodox.core.extension.toast
import com.wodox.core.util.hideKeyboard
import com.wodox.domain.user.model.User
import com.wodox.ui.notification.manager.NotificationPermissionHelper
import com.wodox.ui.task.taskdetail.createtask.CreateTaskUiAction
import com.wodox.ui.task.taskdetail.createtask.CreateTaskUiEvent
import com.wodox.ui.task.userbottomsheet.ListUserBottomSheet

@AndroidEntryPoint
class TaskDetailFragment : BaseFragment<FragmentTaskDetailLayoutBinding, TaskDetailViewModel>(
    TaskDetailViewModel::class
) {

    @Inject
    lateinit var homeNavigator: HomeNavigator

    private var currentSearchQuery: String = ""
    private var selectedDifficulty: Int = 3

    private val task: Task? by lazy {
        arguments?.getParcelable(Constants.Intents.TASK)
    }

    private val adapterAttachments by lazy {
        AttachmentsAdapter(
            context,
            object : AttachmentsAdapter.OnItemClickListener {
                override fun onAttachmentClick(attachment: Attachment) {
                    openAttachment(attachment)
                }

                override fun onDeleteClick(attachment: Attachment) {
                    deleteAttachment(attachment)
                }
            }
        )
    }

    private val subTaskAdapter by lazy {
        SubTaskAdapter(
            context,
            object : SubTaskAdapter.OnItemClickListener {
                override fun onClick(subTask: SubTask) {
                    homeNavigator.openSubTask(childFragmentManager, viewModel.task, subTask)
                }

                override fun onDeleteClick(subTask: SubTask) {
                    viewModel.dispatch(TaskDetailUiAction.DeleteSubTask(subTask))
                }

                override fun onClickShow(subTask: SubTask) {
                    homeNavigator.openSubTask(childFragmentManager, null, subTask)
                }
            }
        )
    }

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

    override fun layoutId(): Int = R.layout.fragment_task_detail_layout

    override fun initialize() {
        setupUI()
        setupAction()
        setupRecyclerViewAttachments()
        setupRecyclerSubTask()
        observer()
    }

    override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onStop() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
            EventBus.getDefault().removeStickyEvent(SearchEvent::class.java)
        }
        super.onStop()
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onSearchEvent(event: SearchEvent) {
        android.util.Log.d("TaskDetailFragment", "Received SearchEvent: ${event.query}")
        currentSearchQuery = event.query
        filterSubTasks(event.query)
    }

    private fun filterSubTasks(query: String) {
        val allSubTasks = viewModel.uiState.value.subTasks

        val filteredList = if (query.isEmpty()) {
            allSubTasks
        } else {
            allSubTasks.filter { subTask ->
                subTask.title.contains(query, ignoreCase = true)
            }
        }
        subTaskAdapter.list = filteredList.toArrayList()
        subTaskAdapter.notifyDataSetChanged()
    }

    private fun setupUI() {
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.tvTitle.text = task?.title.orEmpty()
        viewModel.currentTask.value = task
        setupInitialPriority()
        setupInitialDifficulty()
        setupInitialSupport()
        setupConvertDescription()
        viewModel.dispatch(TaskDetailUiAction.LoadAttachment)
        binding.tvDate.text = if (task?.startAt == null || task?.dueAt == null) {
            getString(com.wodox.resources.R.string.add_dates)
        } else {
            formatDate(task?.startAt?.time, task?.dueAt?.time)
        }
        binding.tvDescription.text = if (task?.description == null) {
            getString(com.wodox.resources.R.string.add_description)
        } else {
            viewModel.currentTask.value?.description
        }
        NotificationPermissionHelper.requestNotificationPermission(requireActivity())
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == NotificationPermissionHelper.NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                android.util.Log.d("TaskDetailFragment", "✅ Notification permission granted")
            } else {
                android.util.Log.d("TaskDetailFragment", "❌ Notification permission denied")
            }
        }
    }


    private fun setupInitialPriority() {
        val priority = viewModel.currentTask.value?.priority?.value ?: 0

        if (priority > 0) {
            val menus = TaskMenuUtil.getItemMenusPriority(requireContext())

            val selectedMenu = menus.find {
                when (it.type.name) {
                    "LOW" -> priority == 1
                    "NORMAL" -> priority == 2
                    "HIGH" -> priority == 3
                    else -> false
                }
            }

            if (selectedMenu != null) {
                binding.llItemMenuPriority.show()
                binding.tvPrioritySelected.text = getString(selectedMenu.nameResId)
                binding.imgPriorityIcon.setImageResource(selectedMenu.iconResId)
                selectedMenu.tintColor?.let {
                    binding.imgPriorityIcon.imageTintList = ColorStateList.valueOf(it)
                } ?: run {
                    binding.imgPriorityIcon.imageTintList = null
                }
            } else {
                binding.llItemMenuPriority.gone()
            }
        } else {
            binding.llItemMenuPriority.gone()
        }
    }

    private fun setupInitialDifficulty() {
        val difficulty = viewModel.currentTask.value?.difficulty
        if (difficulty != null && difficulty.name != "NORMAL") {
            val menus = TaskMenuUtil.getItemMenusDifficulty(requireContext())

            val selectedMenu = menus.find { it.type.name == difficulty.name }

            if (selectedMenu != null) {
                binding.llItemMenuDifficulty.show()

                binding.tvDifficultySelected.text = getString(selectedMenu.nameResId)
                binding.imgDifficultyIcon.setImageResource(selectedMenu.iconResId)

                selectedMenu.tintColor?.let {
                    binding.imgDifficultyIcon.imageTintList = ColorStateList.valueOf(it)
                } ?: run {
                    binding.imgDifficultyIcon.imageTintList = null
                }

                selectedDifficulty = when (difficulty.name) {
                    "VERY_EASY" -> 1
                    "EASY" -> 2
                    "NORMAL" -> 3
                    "HARD" -> 5
                    "VERY_HARD" -> 7
                    "EXPERT" -> 10
                    else -> 3
                }
            }
        } else {
            binding.llItemMenuDifficulty.gone()
        }
        binding.tvDifficulty.text = getDifficultyText(difficulty)
    }

    private fun setupInitialSupport() {
        val support = viewModel.currentTask.value?.support
        binding.tvSupport.text = getSupportLevelText(support)
    }

    private fun getDifficultyText(difficulty: Any?): String {
        return when (difficulty?.toString()) {
            "VERY_EASY" -> "Very Easy"
            "EASY" -> "Easy"
            "NORMAL" -> "Normal"
            "HARD" -> "Hard"
            "VERY_HARD" -> "Very Hard"
            "EXPERT" -> "Expert"
            else -> "None"
        }
    }

    private fun getSupportLevelText(support: Any?): String {
        return when (support?.toString()) {
            "NONE" -> "None"
            "LOW" -> "Low"
            "MEDIUM" -> "Medium"
            "HIGH" -> "High"
            else -> "None"
        }
    }

    private fun setupRecyclerSubTask() {
        val spacing = resources.getDimensionPixelSize(com.wodox.core.R.dimen.dp_8)

        binding.rvAttachments.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = adapterAttachments
            addSpaceDecoration(spacing, true)
        }
    }

    private fun setupRecyclerViewAttachments() {
        val spacing = resources.getDimensionPixelSize(com.wodox.core.R.dimen.dp_8)

        binding.rvSubTask.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = subTaskAdapter
            addSpaceDecoration(spacing, true)
        }
    }

    private fun setupAction() {
        binding.apply {
            llInProgress.debounceClick { it ->
                TaskStatusPopup.show(requireContext(), it) { newStatus ->
                    task?.let {
                        this@TaskDetailFragment.viewModel.dispatch(
                            TaskDetailUiAction.UpdateTaskState(newStatus)
                        )
                    }
                }
            }
            llDate.debounceClick {
                TaskDatePickerDialogFragment().apply {
                    onDateSelected = { start, end ->
                        this@TaskDetailFragment.viewModel.dispatch(
                            TaskDetailUiAction.UpdateDay(
                                start,
                                end
                            )
                        )
                        binding.tvDate.text = formatDate(start, end)
                    }
                }.show(childFragmentManager, "taskDatePicker")
            }
            llDescription.debounceClick {
                this@TaskDetailFragment.viewModel.currentTask.value?.let { task ->
                    homeNavigator.openDescription(requireContext(), task)
                }
            }
            llPriority.debounceClick {
                val menus = TaskMenuUtil.getItemMenusPriority(
                    context = requireContext(),
                )
                MenuView.show(requireContext(), tvPriority, menus, object : MenuOptionListener {
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
                        this@TaskDetailFragment.viewModel.dispatch(
                            TaskDetailUiAction.UpdatePriority(
                                priorityValue
                            )
                        )
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
                            this@TaskDetailFragment.viewModel.dispatch(
                                TaskDetailUiAction.UpdateDifficulty(
                                    selectedDifficulty,
                                    menu.type.name
                                )
                            )
                        }
                    })
            }

            binding.llSupport.debounceClick {
                this@TaskDetailFragment.viewModel.dispatch(TaskDetailUiAction.AnalyzeUserSkill)
            }

            llSubtask.debounceClick {
                this@TaskDetailFragment.viewModel.currentTask.value?.let { it ->
                    homeNavigator.openSubTask(childFragmentManager, it, null)
                }
            }
            llAttachments.debounceClick {
                val dialog = AttachmentDialogFragment.newInstance(
                    this@TaskDetailFragment.viewModel.currentTask.value?.id
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
            imgPriorityClose.debounceClick {
                this@TaskDetailFragment.viewModel.dispatch(TaskDetailUiAction.UpdatePriority(0))
                binding.llItemMenuPriority.gone()
            }
            tvTitle.debounceClick {
                binding.tvTitle.gone()
                binding.layoutEditTitle.show()
                binding.etTitle.setText(binding.tvTitle.text)
                binding.etTitle.requestFocus()
            }
            ivSaveTitle.debounceClick {
                val title = etTitle.text.toString().trim()
                this@TaskDetailFragment.viewModel.dispatch(TaskDetailUiAction.UpdateTitle(title))
            }
            llCheckList.debounceClick {
                homeNavigator.openCheckList(requireContext(), task)
            }
            llAssign.debounceClick {
                val bottomSheet = ListUserBottomSheet.newInstance()
                bottomSheet.listener = object : ListUserBottomSheet.OnItemClickListener {
                    override fun onClick(id: UUID) {
                        this@TaskDetailFragment.viewModel.dispatch(TaskDetailUiAction.AssignUser(id))
                    }
                }
                bottomSheet.showAllowingStateLoss(childFragmentManager)
            }
        }
    }

    private fun observer() {
        launchWhenStarted {
            viewModel.uiEvent.collect { event ->
                when (event) {
                    TaskDetailUiEvent.DeleteSuccess -> requireContext().toast("Delete Successfully")
                    TaskDetailUiEvent.UpdateSuccess -> handleUpdateTitleSuccess()
                    TaskDetailUiEvent.AssignSuccess -> handelUpdateSuccessFully()
                    is TaskDetailUiEvent.AnalysisComplete -> {
                        requireContext().toast("Analysis complete! Support level updated")
                        setupInitialSupport()
                    }

                    is TaskDetailUiEvent.Error -> requireContext().toast(event.message)
                }
            }
        }

        launchWhenStarted {
            viewModel.uiState.collect {
                filterSubTasks(currentSearchQuery)
                updateAssignedUserUI(it.user)
            }
        }

        viewModel.currentTask.observe(viewLifecycleOwner) { task ->
            task?.let {
                setupInitialSupport()
            }
        }
    }

    private fun updateAssignedUserUI(user: User?) {
        user?.let {
            binding.tvSupport.text = it.name
            binding.tvSupport.show()
        } ?: run {
            binding.tvSupport.gone()
        }
    }

    fun handleAskAI() {
        homeNavigator.openAIBottomSheet(childFragmentManager, viewModel.task)
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

    private fun openAudioPicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "audio/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        audioPickerLauncher.launch(Intent.createChooser(intent, "Select Audio"))
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
        viewModel.dispatch(TaskDetailUiAction.UpdateAttachment(uri, type, taskID))
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

    private fun openAttachment(attachment: Attachment) {
        val uri = attachment.uri?.toUri() ?: return
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, getMimeType(attachment.type))
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        try {
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun deleteAttachment(attachment: Attachment) {
        val currentList = adapterAttachments.list
        currentList.remove(attachment)
        adapterAttachments.list = currentList
        adapterAttachments.notifyDataSetChanged()
        viewModel.dispatch(TaskDetailUiAction.DeleteAttachment(attachment))
    }

    private fun getMimeType(type: AttachmentType?): String {
        return when (type) {
            AttachmentType.IMAGE -> "image/*"
            AttachmentType.VIDEO -> "video/*"
            AttachmentType.AUDIO -> "audio/*"
            AttachmentType.FILE -> "*/*"
            else -> "*/*"
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

    private fun handleUpdateTitleSuccess() {
        binding.apply {
            binding.tvTitle.text =
                this@TaskDetailFragment.viewModel.currentTask.value?.title.orEmpty()
            tvTitle.show()
            layoutEditTitle.gone()
            tvTitle.hideKeyboard()
        }
    }

    private fun setupConvertDescription() {
        val html = viewModel.task?.description.orEmpty()
        if (html.isNotEmpty()) {
            lifecycleScope.launch(Dispatchers.IO) {
                val spanned = HtmlConverterUtil.convertToSpanned(html)
                launch(Dispatchers.Main) {
                    binding.tvDescription.setText(spanned, TextView.BufferType.SPANNABLE)
                }
            }
        }
    }

    private fun handelUpdateSuccessFully() {
        requireContext().toast("User assigned successfully")
        viewModel.dispatch(TaskDetailUiAction.AssignSuccessfully)
    }

    companion object {
        fun newInstance(task: Task?) = TaskDetailFragment().apply {
            arguments = Bundle().apply {
                putParcelable(Constants.Intents.TASK, task)
            }
        }

        @JvmStatic
        @BindingAdapter("taskStatusText")
        fun TextView.setTaskStatusText(status: TaskStatus?) {
            text = when (status) {
                TaskStatus.TODO -> "To do"
                TaskStatus.IN_PROGRESS -> "In Progress"
                TaskStatus.DONE -> "Completed"
                TaskStatus.BLOCKED -> "Blocked"
                else -> ""
            }
        }
    }
}