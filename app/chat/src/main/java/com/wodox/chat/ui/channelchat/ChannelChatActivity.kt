package com.wodox.chat.ui.channelchat

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import android.text.Editable
import android.text.TextWatcher
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.permissionx.guolindev.PermissionX
import com.wodox.chat.R
import com.wodox.chat.databinding.ActivityChannelChatBinding
import com.wodox.chat.databinding.BottomSheetAttachmentBinding
import com.wodox.chat.databinding.BottomSheetChannelMenuBinding
import com.wodox.chat.databinding.BottomSheetEmojiPickerBinding
import com.wodox.chat.databinding.BottomSheetMembersListBinding
import com.wodox.chat.databinding.CreateNewChannelBinding
import com.wodox.chat.model.Constant
import com.wodox.core.base.activity.BaseActivity
import com.wodox.core.extension.addSpaceDecoration
import com.wodox.core.extension.debounceClick
import com.wodox.core.extension.launchWhenStarted
import com.wodox.core.extension.show
import com.wodox.core.extension.toast
import com.wodox.domain.chat.model.ChannelMember
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlin.compareTo

@AndroidEntryPoint
class ChannelChatActivity :
    BaseActivity<ActivityChannelChatBinding, ChannelChatViewModel>(ChannelChatViewModel::class) {

    override fun layoutId(): Int = R.layout.activity_channel_chat

    private lateinit var messageAdapter: ChannelMessageAdapter
    private val currentUserId = UUID.randomUUID()

    private var membersBottomSheet: BottomSheetDialog? = null

    private var emojiBottomSheet: BottomSheetDialog? = null
    private var attachmentBottomSheet: BottomSheetDialog? = null
    private var menuBottomSheet: BottomSheetDialog? = null

    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.data?.let { uri ->
                handleSelectedImage(uri)
            }
        }
    }

    private val filePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.data?.let { uri ->
                handleSelectedFile(uri)
            }
        }
    }

    override fun initialize() {
        setupChannelId()
        setupAdapter()
        setupRecyclerView()
        setupActions()
        observeState()
        observeEvents()
        setupUi()
    }

    private fun setupUi() {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this@ChannelChatActivity
    }

    private fun setupChannelId() {
        val channelIdString = intent.getStringExtra(Constant.Intents.CHANNEL_ID)
        if (channelIdString != null) {
            try {
                val channelId = UUID.fromString(channelIdString)
                viewModel.setChannelId(channelId)
            } catch (e: Exception) {
                toast("Invalid channel ID")
                finish()
            }
        } else {
            toast("Channel ID not found")
            finish()
        }
    }

    private fun setupAdapter() {
        messageAdapter = ChannelMessageAdapter(currentUserId)
    }

    private fun setupRecyclerView() {
        val spacing = resources.getDimensionPixelSize(com.wodox.core.R.dimen.dp_6)

        binding.rvMessages.apply {
            layoutManager = LinearLayoutManager(this@ChannelChatActivity)
            adapter = messageAdapter
            addSpaceDecoration(spacing, false)
        }
    }

    private fun setupActions() {
        binding.apply {
            ivBack.debounceClick {
                finish()
            }

            ivMenu.debounceClick {
                showChannelMenu()
            }

            btnAttachment.debounceClick {
                showAttachmentOptions()
            }

            btnEmoji.debounceClick {
                showEmojiPicker()
            }

            etMessage.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val hasText = !s.isNullOrBlank()
                    btnSend.isEnabled = hasText
                    btnSend.alpha = if (hasText) 1.0f else 0.5f

                    val showActions = s.isNullOrEmpty()
                    btnAttachment.show(showActions)
                    btnEmoji.show(showActions)
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            btnSend.debounceClick {
                val text = etMessage.text.toString().trim()
                if (text.isNotEmpty()) {
                    etMessage.text?.clear()
                    this@ChannelChatActivity.viewModel.dispatch(ChannelChatUiAction.SendMessage(text))
                }
            }

            etMessage.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    btnSend.performClick()
                    true
                } else {
                    false
                }
            }
        }
    }

    private fun observeState() {
        launchWhenStarted {
            viewModel.uiState.collect { state ->
                state.channel?.let { channel ->
                    binding.tvChannelName.text = channel.name
                    binding.tvMemberCount.text = "${channel.memberCount} members"
                }

                if (state.messages.isNotEmpty()) {
                    val oldSize = messageAdapter.itemCount
                    messageAdapter.submitList(state.messages)
                    if (state.messages.size > oldSize) {
                        binding.rvMessages.post {
                            if (messageAdapter.itemCount > 0) {
                                binding.rvMessages.smoothScrollToPosition(messageAdapter.itemCount - 1)
                            }
                        }
                    }
                }
                binding.progressBar.show(state.isLoading)
                if (!state.error.isNullOrEmpty()) {
                    toast(state.error)
                }
            }
        }
    }

    private fun observeEvents() {
        launchWhenStarted {
            viewModel.uiEvent.collect { event ->
                when (event) {
                    is ChannelChatUiEvent.MessageSent -> {
                        // Message sent successfully
                    }

                    is ChannelChatUiEvent.Error -> {
                        toast(event.message)
                    }
                }
            }
        }
    }

    private fun showChannelMenu() {
        if (menuBottomSheet == null) {
            menuBottomSheet = BottomSheetDialog(this).apply {
                val bottomSheetBinding = BottomSheetChannelMenuBinding.inflate(layoutInflater)
                setContentView(bottomSheetBinding.root)

                bottomSheetBinding.btnChannelInfo.setOnClickListener {
                    dismiss()
                    showChannelInfo()
                }

                bottomSheetBinding.btnViewMembers.setOnClickListener {
                    dismiss()
                    showChannelInfoBottomSheet()
                }

                bottomSheetBinding.btnSearch.setOnClickListener {
                    dismiss()
                    showSearchDialog()
                }

                bottomSheetBinding.btnViewMedia.setOnClickListener {
                    dismiss()
                    showMediaGallery()
                }

                bottomSheetBinding.btnMuteNotifications.setOnClickListener {
                    dismiss()
                    toggleMuteNotifications()
                }

                bottomSheetBinding.btnLeaveChannel.setOnClickListener {
                    dismiss()
                    showLeaveChannelConfirmation()
                }

                bottomSheetBinding.btnCancel.setOnClickListener {
                    dismiss()
                }
            }
        }
        menuBottomSheet?.show()
    }

    private fun showChannelInfo() {
        val channel = viewModel.uiState.value.channel ?: return
        val dialog = MaterialAlertDialogBuilder(this)
            .setCancelable(true)
            .create()

        val binding: CreateNewChannelBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.create_new_channel,
            null,
            false
        )

        val handler = ChannelInfoDialogHandler(this, channel, dialog)
        binding.apply {
            this.channel = channel
            this.handler = handler
            lifecycleOwner = this@ChannelChatActivity
        }

        dialog.setView(binding.root)

        dialog.window?.apply {
            setBackgroundDrawableResource(android.R.color.transparent)
            attributes?.let {
                it.width = (resources.displayMetrics.widthPixels * 0.85).toInt()
                it.height = WindowManager.LayoutParams.WRAP_CONTENT
            }
        }

        dialog.show()
    }

    private fun showChannelInfoBottomSheet() {
        val channel = viewModel.uiState.value.channel ?: return

        val bottomSheet = BottomSheetDialog(
            this,
            com.wodox.core.R.style.BottomSheetDialogTheme
        )

        val binding: CreateNewChannelBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.create_new_channel,
            null,
            false
        )

        val handler = ChannelInfoDialogHandler(this, channel, bottomSheet)

        binding.apply {
            this.channel = channel
            this.handler = handler
            lifecycleOwner = this@ChannelChatActivity
        }

        bottomSheet.setContentView(binding.root)
        bottomSheet.show()
    }


    private fun showSearchDialog() {
        val builder = AlertDialog.Builder(this)
        val input = EditText(this).apply {
            hint = "Search messages..."
            setPadding(50, 30, 50, 30)
        }

        builder.setTitle("Search Messages")
            .setView(input)
            .setPositiveButton("Search") { dialog, _ ->
                val query = input.text.toString().trim()
                if (query.isNotEmpty()) {
                    toast("Searching for: $query")
                    // TODO: Implement search
                } else {
                    toast("Please enter search text")
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }

    private fun showMediaGallery() {
        val mediaMessages = viewModel.uiState.value.messages.filter {
            it.text.startsWith("[Image]") || it.text.startsWith("[File]")
        }

        if (mediaMessages.isEmpty()) {
            toast("No media or files in this channel")
            return
        }

        val mediaList = mediaMessages.map { message ->
            when {
                message.text.startsWith("[Image]") -> "📷 Image - ${formatTimestamp(message.timestamp)}"
                message.text.startsWith("[File]") -> message.text.replace("[File]", "📎")
                else -> "Unknown"
            }
        }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Media & Files (${mediaMessages.size})")
            .setItems(mediaList) { _, which ->
                toast("Selected: ${mediaList[which]}")
            }
            .setNegativeButton("Close") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun toggleMuteNotifications() {
        AlertDialog.Builder(this)
            .setTitle("Mute Notifications")
            .setMessage("Choose mute duration:")
            .setItems(arrayOf("1 hour", "8 hours", "1 week", "Forever")) { dialog, which ->
                val duration = when (which) {
                    0 -> "1 hour"
                    1 -> "8 hours"
                    2 -> "1 week"
                    else -> "forever"
                }
                toast("Notifications muted for $duration")
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }

    private fun showLeaveChannelConfirmation() {
        val channelName = viewModel.uiState.value.channel?.name ?: "this channel"

        AlertDialog.Builder(this)
            .setTitle("Leave Channel")
            .setMessage("Are you sure you want to leave $channelName?\nYou can rejoin later if it's public.")
            .setPositiveButton("Leave") { dialog, _ ->
                toast("Left $channelName")
                dialog.dismiss()
                finish()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }

    private fun formatTimestamp(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    // ============= EMOJI PICKER =============
    private fun showEmojiPicker() {
        if (emojiBottomSheet == null) {
            emojiBottomSheet = BottomSheetDialog(this).apply {
                val bottomSheetBinding = BottomSheetEmojiPickerBinding.inflate(layoutInflater)
                setContentView(bottomSheetBinding.root)

                bottomSheetBinding.emojiPickerView.setOnEmojiPickedListener { emoji ->
                    insertEmoji(emoji.emoji)
                }

                bottomSheetBinding.btnClose.setOnClickListener {
                    dismiss()
                }
            }
        }
        emojiBottomSheet?.show()
    }

    private fun insertEmoji(emoji: String) {
        val currentText = binding.etMessage.text.toString()
        val cursorPosition = binding.etMessage.selectionStart
        val newText = StringBuilder(currentText).insert(cursorPosition, emoji).toString()
        binding.etMessage.setText(newText)
        binding.etMessage.setSelection(cursorPosition + emoji.length)
    }

    // ============= ATTACHMENT OPTIONS =============
    private fun showAttachmentOptions() {
        if (attachmentBottomSheet == null) {
            attachmentBottomSheet = BottomSheetDialog(this).apply {
                val bottomSheetBinding = BottomSheetAttachmentBinding.inflate(layoutInflater)
                setContentView(bottomSheetBinding.root)

                bottomSheetBinding.btnCamera.setOnClickListener {
                    dismiss()
                    openCamera()
                }

                bottomSheetBinding.btnGallery.setOnClickListener {
                    dismiss()
                    openGallery()
                }

                bottomSheetBinding.btnDocument.setOnClickListener {
                    dismiss()
                    openDocumentPicker()
                }

                bottomSheetBinding.btnCancel.setOnClickListener {
                    dismiss()
                }
            }
        }
        attachmentBottomSheet?.show()
    }

    private fun openCamera() {
        PermissionX.init(this)
            .permissions(Manifest.permission.CAMERA)
            .request { allGranted, _, _ ->
                if (allGranted) {
                    ImagePicker.with(this)
                        .cameraOnly()
                        .compress(1024)
                        .maxResultSize(1080, 1080)
                        .createIntent { intent ->
                            imagePickerLauncher.launch(intent)
                        }
                } else {
                    toast("Camera permission required")
                }
            }
    }

    private fun openGallery() {
        PermissionX.init(this)
            .permissions(
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    Manifest.permission.READ_MEDIA_IMAGES
                } else {
                    Manifest.permission.READ_EXTERNAL_STORAGE
                }
            )
            .request { allGranted, _, _ ->
                if (allGranted) {
                    ImagePicker.with(this)
                        .galleryOnly()
                        .compress(1024)
                        .maxResultSize(1080, 1080)
                        .createIntent { intent ->
                            imagePickerLauncher.launch(intent)
                        }
                } else {
                    toast("Storage permission required")
                }
            }
    }

    private fun openDocumentPicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*"
        intent.putExtra(
            Intent.EXTRA_MIME_TYPES, arrayOf(
                "application/pdf",
                "application/msword",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "application/vnd.ms-excel",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            )
        )
        filePickerLauncher.launch(intent)
    }


    private fun handleSelectedImage(uri: Uri) {
        val message = "[Image] $uri"
        viewModel.dispatch(ChannelChatUiAction.SendMessage(message))
        toast("Sending image...")
    }

    private fun handleSelectedFile(uri: Uri) {
        val fileName = getFileName(uri) ?: "document"
        val message = "[File] $fileName"
        viewModel.dispatch(ChannelChatUiAction.SendMessage(message))
        toast("Sending file...")
    }

    private fun getFileName(uri: Uri): String? {
        var fileName: String? = null
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex >= 0 && cursor.moveToFirst()) {
                fileName = cursor.getString(nameIndex)
            }
        }
        return fileName ?: uri.lastPathSegment
    }

    override fun onDestroy() {
        emojiBottomSheet?.dismiss()
        attachmentBottomSheet?.dismiss()
        menuBottomSheet?.dismiss()
        membersBottomSheet?.dismiss()
        super.onDestroy()
    }
}