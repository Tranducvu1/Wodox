package com.wodox.chat.ui.message

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.permissionx.guolindev.PermissionX
import com.wodox.chat.R
import com.wodox.chat.databinding.ActivityMessageBinding
import com.wodox.chat.databinding.BottomSheetAttachmentBinding
import com.wodox.chat.databinding.BottomSheetEmojiPickerBinding
import com.wodox.chat.databinding.BottomSheetMessageMenuBinding
import com.wodox.core.base.activity.BaseActivity
import com.wodox.core.extension.addSpaceDecoration
import com.wodox.core.extension.debounceClick
import com.wodox.core.extension.launchWhenStarted
import com.wodox.core.extension.show
import com.wodox.core.extension.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MessageActivity :
    BaseActivity<ActivityMessageBinding, MessageViewModel>(MessageViewModel::class) {

    override fun layoutId(): Int = R.layout.activity_message

    private lateinit var adapterMessage: UserMessageAdapter
    private var emojiBottomSheet: BottomSheetDialog? = null
    private var attachmentBottomSheet: BottomSheetDialog? = null
    private var menuBottomSheet: BottomSheetDialog? = null

    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                handleSelectedImage(uri)
            }
        }
    }

    private val filePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                handleSelectedFile(uri)
            }
        }
    }

    override fun initialize() {
        setupAdapter()
        setupRecycleView()
        setupAction()
        observer()
        observeState()
    }

    private fun setupUi(friendName: String?) {
        val firstLetter = friendName?.firstOrNull()?.toString()?.uppercase() ?: "F"
        binding.tvAvatarLetter.text = firstLetter
        binding.tvFriendName.text = friendName ?: "Friend"
    }

    private fun setupAction() {
        binding.apply {
            ivBack.debounceClick {
                finish()
            }

            ivMenu.debounceClick {
                showMessageMenu()
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
                    this@MessageActivity.viewModel.dispatch(MessageUiAction.SendMessage(text))
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

    private fun setupRecycleView() {
        binding.lifecycleOwner = this
        val spacing = resources.getDimensionPixelSize(com.wodox.core.R.dimen.dp_6)

        binding.rvMessages.apply {
            setHasFixedSize(true)
            itemAnimator = null
            layoutManager = LinearLayoutManager(
                context,
                RecyclerView.VERTICAL,
                false
            )
            adapter = adapterMessage
            addSpaceDecoration(spacing, false)
        }
    }

    private fun observer() {
        launchWhenStarted {
            viewModel.uiEvent.collect { event ->
                when (event) {
                    is MessageUiEvent.AllMessagesCleared -> {
                        adapterMessage.clearAll()
                        toast("Chat history cleared")
                    }

                    is MessageUiEvent.Error -> {
                        toast(event.message)
                    }

                    is MessageUiEvent.MessageDeleted -> {
                        toast("Message deleted")
                    }

                    is MessageUiEvent.MessageDetailLoaded -> {}
                    is MessageUiEvent.MessageSelected -> {}
                    is MessageUiEvent.MessageUpdated -> {
                        toast("Message updated")
                    }
                }
            }
        }
    }

    private fun observeState() {
        launchWhenStarted {
            viewModel.uiState.collect { state ->
                if (state.friend != null) {
                    setupUi(state.friend.name)
                }

                if (state.messages.isNotEmpty()) {
                    val sortedMessages = state.messages.sortedBy { it.timestamp }
                    val oldSize = adapterMessage.itemCount

                    adapterMessage.submitList(sortedMessages)

                    if (sortedMessages.size > oldSize) {
                        binding.rvMessages.post {
                            if (adapterMessage.itemCount > 0) {
                                binding.rvMessages.smoothScrollToPosition(adapterMessage.itemCount - 1)
                            }
                        }
                    }
                }

                if (!state.error.isNullOrEmpty()) {
                    toast(state.error)
                }

                binding.progressBar.show(state.isLoading)
            }
        }
    }

    private fun setupAdapter() {
        adapterMessage = UserMessageAdapter()
    }

    private fun showMessageMenu() {
        if (menuBottomSheet == null) {
            menuBottomSheet = BottomSheetDialog(this).apply {
                val bottomSheetBinding = BottomSheetMessageMenuBinding.inflate(layoutInflater)
                setContentView(bottomSheetBinding.root)
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

                bottomSheetBinding.btnClearChat.setOnClickListener {
                    dismiss()
                    showClearChatConfirmation()
                }

                bottomSheetBinding.btnBlockUser.setOnClickListener {
                    dismiss()
                    showBlockUserConfirmation()
                }

                bottomSheetBinding.btnCancel.setOnClickListener {
                    dismiss()
                }
            }
        }
        menuBottomSheet?.show()
    }

    private fun showSearchDialog() {
        val builder = AlertDialog.Builder(this)
        val input = android.widget.EditText(this).apply {
            hint = "Search messages..."
            setPadding(50, 30, 50, 30)
        }

        builder.setTitle("Search Messages")
            .setView(input)
            .setPositiveButton("Search") { dialog, _ ->
                val query = input.text.toString().trim()
                if (query.isNotEmpty()) {
                    viewModel.dispatch(MessageUiAction.SearchMessages(query))
                    toast("Searching for: $query")
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
            toast("No media or files in this conversation")
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
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Mute Notifications")
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

    private fun showClearChatConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Clear Chat History")
            .setMessage("Are you sure you want to clear all messages?\nThis action cannot be undone.")
            .setPositiveButton("Clear") { dialog, _ ->
                viewModel.dispatch(MessageUiAction.ClearMessages)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }

    private fun showBlockUserConfirmation() {
        val friendName = viewModel.uiState.value.friend?.name ?: "this user"

        AlertDialog.Builder(this)
            .setTitle("Block User")
            .setMessage("Are you sure you want to block $friendName?\nYou won't receive messages from them anymore.")
            .setPositiveButton("Block") { dialog, _ ->
                toast("$friendName has been blocked")
                dialog.dismiss()
                finish()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }

    private fun formatTimestamp(timestamp: Long): String {
        val sdf = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
        return sdf.format(java.util.Date(timestamp))
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
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
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
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
            putExtra(
                Intent.EXTRA_MIME_TYPES, arrayOf(
                    "application/pdf",
                    "application/msword",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                    "application/vnd.ms-excel",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                )
            )
        }
        filePickerLauncher.launch(intent)
    }

    private fun handleSelectedImage(uri: Uri) {
        val message = "[Image] $uri"
        android.util.Log.d("MessageActivity", "=== SENDING IMAGE ===")
        android.util.Log.d("MessageActivity", "Message: $message")
        android.util.Log.d("MessageActivity", "URI: $uri")

        viewModel.dispatch(MessageUiAction.SendMessage(message))
        toast("Sending image...")
    }

    private fun handleSelectedFile(uri: Uri) {
        val fileName = getFileName(uri) ?: "document"
        val message = "[File] $fileName"
        viewModel.dispatch(MessageUiAction.SendMessage(message))
        toast("Sending file...")
    }

    private fun getFileName(uri: Uri): String? {
        var fileName: String? = null
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
            if (nameIndex >= 0 && cursor.moveToFirst()) {
                fileName = cursor.getString(nameIndex)
            }
        }
        return fileName ?: uri.lastPathSegment
    }

    override fun onDestroy() {
        adapterMessage.cleanup()
        emojiBottomSheet?.dismiss()
        attachmentBottomSheet?.dismiss()
        menuBottomSheet?.dismiss()
        super.onDestroy()
    }
}