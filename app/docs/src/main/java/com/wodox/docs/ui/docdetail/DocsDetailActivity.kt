package com.wodox.docs.ui.docdetail

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.wodox.common.extension.showMoreColor
import com.wodox.common.navigation.MainNavigator
import com.wodox.common.view.colorpicker.TextSizePopupView
import com.wodox.core.base.activity.BaseActivity
import com.wodox.core.extension.debounceClick
import com.wodox.core.extension.launchWhenStarted
import com.wodox.core.extension.show
import com.wodox.core.extension.showAllowingStateLoss
import com.wodox.core.extension.toast
import com.wodox.docs.R
import com.wodox.docs.databinding.ActivityDocsLayoutBinding
import com.wodox.docs.font.FontBottomSheet
import com.wodox.domain.docs.model.TextFormat
import com.wodox.domain.docs.model.model.DocumentPermission
import com.wodox.domain.docs.model.model.InvitedUser
import com.wodox.ui.task.userbottomsheet.ListUserBottomSheet
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.poi.xwpf.usermodel.ParagraphAlignment
import org.apache.poi.xwpf.usermodel.UnderlinePatterns
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.apache.poi.xwpf.usermodel.XWPFParagraph
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode
import java.io.File
import java.io.FileOutputStream
import java.math.BigInteger
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class DocsDetailActivity : BaseActivity<ActivityDocsLayoutBinding, DocsDetailViewModel>(
    DocsDetailViewModel::class) {
    @Inject
    lateinit var mainNavigator: MainNavigator

    override fun layoutId(): Int = R.layout.activity_docs_layout

    private lateinit var invitedUserAdapter: InvitedUserAdapter
    private var autoSaveJob: Job? = null
    private var isSharedDocument = false
    private var canEdit = true

    private val openDocumentLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.data?.let { uri ->
                    handleOpenedFile(uri)
                }
            }
        }

    private val pickVideoLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val videoPath = getLocalVideoPath(uri)
                binding.richEditor.insertVideo(videoPath)
                toast("Đã chèn video thành công")
            } catch (e: Exception) {
                e.printStackTrace()
                toast("Không thể chèn video")
            }
        }
    }

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val imagePath = getLocalImagePath(uri)
                binding.richEditor.insertImage(imagePath, "Image", 200, 200)
                toast("Đã chèn ảnh thành công")
            } catch (e: Exception) {
                e.printStackTrace()
                toast("Không thể chèn ảnh")
            }
        }
    }

    private val createDocumentLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.data?.let { uri ->
                saveToUri(uri)
            }
        }
    }

    override fun initialize() {
        setupUI()
        setupAction()
        setupInvitedUsersRecyclerView()
        observeState()
        observeEvents()

        val docTitle =
            binding.etDocTitle.text.toString().ifBlank { System.currentTimeMillis().toString() }
        viewModel.dispatch(DocsDetailUiAction.SetDocumentId(docTitle))

        if (!isSharedDocument) {
            viewModel.loadInvitedUsersFromPreferences(docTitle)
        }

        setupAutoSave()
    }

    private fun restoreTextFormat() {
        val invitedUsers = viewModel.uiState.value.invitedUsers
        viewModel.loadInvitedUsersFromPreferences(
            viewModel.uiState.value.documentId
        )
    }


    private fun setupUI() {
        binding.lifecycleOwner = this
        setupWebViewForYouTube()
        binding.richEditor.apply {
            setEditorHeight(200)
            setEditorFontSize(16)
            setPadding(10, 10, 10, 10)
            setPlaceholder("Start writing your document...")
        }

        val extras = intent.extras
        isSharedDocument = extras?.getBoolean("is_shared", false) ?: false

        if (isSharedDocument) {
            val docId = extras?.getString("doc_id", "") ?: ""
            val docTitle = extras?.getString("doc_title", "") ?: ""
            val docHtml = extras?.getString("doc_html", "") ?: ""
            val ownerName = extras?.getString("doc_owner", "") ?: ""
            val permission = extras?.getString("doc_permission", "VIEW") ?: "VIEW"

            binding.etDocTitle.setText(docTitle)
            binding.richEditor.html = docHtml
            binding.tvLastSaved.text = "Shared by $ownerName"

            viewModel.dispatch(DocsDetailUiAction.SetDocumentId(docId))
            viewModel.loadSharedDocumentById(docId)
            viewModel.loadInvitedUsersFromPreferences(docId)

            canEdit = permission == "EDIT"

            if (!canEdit) {
                binding.richEditor.isEnabled = false
                binding.etDocTitle.isEnabled = false
                binding.llEditingTools.visibility = View.GONE
                toast("Bạn chỉ có quyền xem document này")
            }
        } else {
            val docTitle = System.currentTimeMillis().toString()
            binding.etDocTitle.setText("")
            binding.richEditor.html = ""
            binding.tvLastSaved.text = "Unsaved"

            viewModel.dispatch(DocsDetailUiAction.SetDocumentId(docTitle))
            viewModel.loadInvitedUsersFromPreferences(docTitle)
        }
    }

    private fun setupInvitedUsersRecyclerView() {
        invitedUserAdapter = InvitedUserAdapter { user ->
            if (!isSharedDocument || canEdit) {
                viewModel.dispatch(DocsDetailUiAction.RemoveInvitedUser(user.userId))
                toast("Đã xóa ${user.userName}")
            } else {
                toast("Bạn không có quyền xóa người dùng")
            }
        }

        binding.rvInvitedUsers.apply {
            layoutManager = LinearLayoutManager(
                this@DocsDetailActivity, LinearLayoutManager.HORIZONTAL, false
            )
            adapter = invitedUserAdapter
        }
    }

    private fun setupWebViewForYouTube() {
        binding.richEditor.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            databaseEnabled = true
            allowFileAccess = true
            allowContentAccess = true
            mediaPlaybackRequiresUserGesture = false
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            pluginState = WebSettings.PluginState.ON
        }

        binding.richEditor.webChromeClient = WebChromeClient()
    }

    private fun setupAction() {
        binding.apply {
            ivBack.debounceClick { finish() }
            ivUndo.debounceClick { richEditor.undo() }
            ivRedo.debounceClick { richEditor.redo() }
            ivBold.debounceClick { richEditor.setBold() }
            ivItalic.debounceClick { richEditor.setItalic() }
            ivUnderline.debounceClick { richEditor.setUnderline() }
            ivStrikethrough.debounceClick { richEditor.setStrikeThrough() }
            ivAlignLeft.debounceClick { richEditor.setAlignLeft() }
            ivAlignCenter.debounceClick { richEditor.setAlignCenter() }
            ivAlignRight.debounceClick { richEditor.setAlignRight() }
            ivBulletList.debounceClick { richEditor.setBullets() }
            ivNumberList.debounceClick { richEditor.setNumbers() }

            ivTextColor.debounceClick {
                setupColorPicker { selectedColor ->
                    val color = convertHexColorString(selectedColor)
                    richEditor.setTextColor(color)
                }
            }

            ivShare.debounceClick {
                if (!isSharedDocument || canEdit) {
                    showUserSelectionBottomSheet()
                } else {
                    toast("Bạn không có quyền chia sẻ document này")
                }
            }

            ivHighlight.debounceClick {
                setupColorPicker { selectedColor ->
                    val color = convertHexColorString(selectedColor)
                    richEditor.setTextBackgroundColor(color)
                }
            }

            ivOpenFromDrive.debounceClick { openDrivePicker() }
            ivSave.debounceClick { openSaveFilePicker() }

            tvNumber.debounceClick { view ->
                TextSizePopupView.Companion.show(view) { selectedSize ->
                    richEditor.setFontSize(selectedSize)
                    tvNumber.text = selectedSize.toString()
                    this@DocsDetailActivity.viewModel.dispatch(
                        DocsDetailUiAction.UpdateTextSize(
                            selectedSize
                        )
                    )
                }
            }

            ivImage.debounceClick { pickImageLauncher.launch("image/*") }
            ivFont.debounceClick { handleChangeFont() }
            ivVideo.debounceClick { showVideoUrlDialog() }
        }
    }

    private fun showUserSelectionBottomSheet() {
        val bottomSheet = ListUserBottomSheet.Companion.newInstance()
        bottomSheet.listener = object : ListUserBottomSheet.OnItemClickListener {
            override fun onClick(id: UUID) {
                viewModel.dispatch(DocsDetailUiAction.AssignUser(id))
                showPermissionDialog(id)
            }
        }
        bottomSheet.showAllowingStateLoss(supportFragmentManager, "ListUserBottomSheet")
    }

    private fun showPermissionDialog(userId: UUID) {
        val permissions = arrayOf("Chỉ xem", "Có thể chỉnh sửa")
        var selectedPermission = 0

        AlertDialog.Builder(this).setTitle("Chọn quyền cho người dùng")
            .setSingleChoiceItems(permissions, selectedPermission) { _, which ->
                selectedPermission = which
            }.setPositiveButton("Thêm") { _, _ ->
                addUserToDocument(userId, selectedPermission)
            }.setNegativeButton("Hủy", null).show()
    }

    private fun addUserToDocument(userId: UUID, permissionIndex: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val user = viewModel.uiState.value.userInvite

                if (user != null) {
                    val permission = if (permissionIndex == 0) {
                        DocumentPermission.VIEW
                    } else {
                        DocumentPermission.EDIT
                    }

                    val invitedUser = InvitedUser(
                        userId = user.id,
                        userName = user.name,
                        userEmail = user.email,
                        permission = permission
                    )

                    withContext(Dispatchers.Main) {
                        viewModel.dispatch(DocsDetailUiAction.AddInvitedUser(invitedUser))
                    }

                    delay(500)

                    val docId = viewModel.uiState.value.documentId
                    val docTitle = withContext(Dispatchers.Main) {
                        binding.etDocTitle.text.toString()
                            .ifBlank { System.currentTimeMillis().toString() }
                    }
                    val htmlContent = withContext(Dispatchers.Main) {
                        binding.richEditor.html ?: ""
                    }

                    android.util.Log.d("DocsDetailActivity", "Sharing document with new user")
                    viewModel.shareDocumentWithUsers(
                        documentId = docId,
                        documentTitle = docTitle,
                        htmlContent = htmlContent
                    )

                    withContext(Dispatchers.Main) {
                        toast("Đã thêm ${user.name} với quyền ${if (permissionIndex == 0) "Chỉ xem" else "Có thể chỉnh sửa"}")
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("DocsDetailActivity", "Error adding user", e)
                withContext(Dispatchers.Main) {
                    toast("Lỗi: ${e.message}")
                }
            }
        }
    }

    private fun setupAutoSave() {
        binding.richEditor.setOnTextChangeListener { _ ->
            autoSaveJob?.cancel()
            autoSaveJob = lifecycleScope.launch {
                delay(3000)

                val docId = viewModel.uiState.value.documentId
                val docTitle = binding.etDocTitle.text.toString()
                    .ifBlank { System.currentTimeMillis().toString() }
                val htmlContent = binding.richEditor.html ?: ""

                viewModel.shareDocumentWithUsers(
                    documentId = docId,
                    documentTitle = docTitle,
                    htmlContent = htmlContent
                )

                val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                binding.tvLastSaved.text = "Saved at $currentTime"
            }
        }
    }

    private fun observeState() {
        launchWhenStarted {
            viewModel.uiState.collect { state ->
                binding.tvNumber.text = state.textSize.toString()

                invitedUserAdapter.updateUsers(state.invitedUsers)

                binding.llInvitedUsers.visibility =
                    if (state.invitedUsers.isNotEmpty()) View.VISIBLE
                    else View.GONE
            }
        }
    }

    private fun observeEvents() {
        launchWhenStarted {
            viewModel.uiEvent.collect { event ->
                when (event) {
                    is DocsDetailUiEvent.DocumentSharedSuccessfully -> {
                        toast("Đã chia sẻ với ${event.userCount} người")
                    }

                    is DocsDetailUiEvent.SharingFailed -> {
                        toast("Lỗi chia sẻ: ${event.message}")
                    }

                    is DocsDetailUiEvent.DocumentLoadedFromServer -> {
                        val doc = event.document

                        binding.etDocTitle.setText(doc.documentTitle)
                        binding.richEditor.html = doc.htmlContent
                        binding.tvLastSaved.text = "Shared by ${doc.ownerUserName}"

                        invitedUserAdapter.updateUsers(doc.invitedUsers)
                        binding.llInvitedUsers.show(doc.invitedUsers.isNotEmpty())
                    }

                    else -> {}
                }
            }
        }
    }

    private fun showVideoUrlDialog() {
        val editText = EditText(this)
        editText.hint = "https://www.youtube.com/watch?v=..."

        AlertDialog.Builder(this).setTitle("Nhập đường dẫn video YouTube")
            .setView(editText).setPositiveButton("Chèn") { _, _ ->
                val url = editText.text.toString().trim()
                if (url.isNotEmpty() && isValidYoutubeUrl(url)) {
                    insertYoutubeVideoHtml(url)
                    toast("Đã chèn video YouTube")
                } else {
                    toast("URL YouTube không hợp lệ")
                }
            }.setNegativeButton("Hủy", null).show()
    }

    private fun isValidYoutubeUrl(url: String): Boolean {
        return url.contains("youtube.com/watch?v=") || url.contains("youtu.be/") || url.contains("youtube.com/embed/")
    }

    private fun insertYoutubeVideoHtml(url: String) {
        val videoId = extractVideoId(url)
        val embedUrl = "https://www.youtube.com/embed/$videoId?enablejsapi=1&playsinline=1"
        binding.richEditor.insertYoutubeVideo(embedUrl, 560, 315)
    }

    private fun extractVideoId(url: String): String {
        return when {
            url.contains("watch?v=") -> url.substringAfter("watch?v=").substringBefore("&")
            url.contains("youtu.be/") -> url.substringAfter("youtu.be/").substringBefore("?")
            url.contains("embed/") -> url.substringAfter("embed/").substringBefore("?")
            else -> ""
        }
    }

    private fun convertHexColorString(color: Int): Int {
        return 0xFFFFFF and color
    }

    private fun handleChangeFont() {
        val bottomSheet = FontBottomSheet.Companion.newInstance(viewModel.textFormat)
        bottomSheet.listener = object : FontBottomSheet.OnItemClickListener {
            override fun onClick(font: TextFormat) {
                viewModel.textFormat.fontName = font.fontName
                viewModel.textFormat.componentToChange = TextFormat.ChangeComponent.FONT
                applyFormat()
            }
        }
        bottomSheet.showAllowingStateLoss(supportFragmentManager, "FontBottomSheet")
    }

    private fun applyFormat() {
        val fontName = viewModel.textFormat.fontName
        val displayFontName = fontName.split("_").joinToString(" ") {
            it.replaceFirstChar { ch ->
                if (ch.isLowerCase()) ch.titlecase() else ch.toString()
            }
        }
        binding.richEditor.setFontName(displayFontName)
    }

    private fun setupColorPicker(onColorSelected: (Int) -> Unit) {
        showMoreColor(
            defaultColor = ContextCompat.getColor(
                this, com.wodox.core.R.color.black
            ),
            success = { selectedColor -> onColorSelected(selectedColor) },
            cancel = { onColorSelected(Color.BLACK) }
        )
    }

    private fun openDrivePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
        }
        openDocumentLauncher.launch(intent)
    }

    private fun handleOpenedFile(uri: Uri) {
        try {
            val type = contentResolver.getType(uri)
            val inputStream = contentResolver.openInputStream(uri)
            if (type == "application/vnd.openxmlformats-officedocument.wordprocessingml.document") {
                val doc = XWPFDocument(inputStream)
                val htmlBuilder = StringBuilder()

                for (paragraph in doc.paragraphs) {
                    val alignment = when (paragraph.alignment) {
                        ParagraphAlignment.CENTER -> "center"
                        ParagraphAlignment.RIGHT -> "right"
                        else -> "left"
                    }

                    val headingLevel = when (paragraph.style) {
                        "Heading1", "Heading 1" -> "h1"
                        "Heading2", "Heading 2" -> "h2"
                        "Heading3", "Heading 3" -> "h3"
                        else -> "p"
                    }

                    htmlBuilder.append("<$headingLevel style=\"text-align:$alignment;\">")
                    for (run in paragraph.runs) {
                        var text = run.text() ?: ""
                        if (text.isBlank()) continue

                        text = text.replace("<", "&lt;").replace(">", "&gt;")

                        val style = StringBuilder()

                        run.fontSize.takeIf { it > 0 }?.let {
                            style.append("font-size:${it}px;")
                        }
                        run.color?.let { color ->
                            style.append("color:#${color};")
                        }

                        run.textHighlightColor?.let { highlight ->
                            val colorMap = mapOf(
                                "yellow" to "#ffff00",
                                "cyan" to "#00ffff",
                                "green" to "#00ff00",
                                "magenta" to "#ff00ff"
                            )
                            val highlightName = highlight.toString().lowercase(Locale.getDefault())
                            colorMap[highlightName]?.let {
                                style.append("background-color:$it;")
                            }
                        }
                        if (run.isBold) text = "<b>$text</b>"
                        if (run.isItalic) text = "<i>$text</i>"
                        if (run.underline != UnderlinePatterns.NONE) text = "<u>$text</u>"

                        if (style.isNotEmpty()) {
                            text = "<span style=\"$style\">$text</span>"
                        }
                        htmlBuilder.append(text)
                    }
                    htmlBuilder.append("</$headingLevel>")
                }
                doc.close()
                binding.richEditor.html = htmlBuilder.toString()
            } else {
                val content = inputStream?.bufferedReader().use { it?.readText() } ?: ""
                binding.richEditor.html = content
            }
            binding.etDocTitle.setText(uri.lastPathSegment ?: "Untitled Document")
        } catch (e: Exception) {
            e.printStackTrace()
            toast("Cannot open file")
        }
    }

    private fun getLocalVideoPath(uri: Uri): String {
        val inputStream = contentResolver.openInputStream(uri)
        val file = File(cacheDir, "video_${System.currentTimeMillis()}.mp4")
        inputStream?.use { input ->
            FileOutputStream(file).use { output -> input.copyTo(output) }
        }
        return "file://${file.absolutePath}"
    }

    private fun openSaveFilePicker() {
        saveToFirebase()
        val title = binding.etDocTitle.text.toString().ifBlank { "Untitled Document" }

        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            putExtra(Intent.EXTRA_TITLE, "$title.docx")
        }
        createDocumentLauncher.launch(intent)
    }

    private fun saveToFirebase() {
        lifecycleScope.launch(Dispatchers.Main) {
            val docId = viewModel.uiState.value.documentId
            val docTitle = binding.etDocTitle.text.toString()
                .ifBlank { System.currentTimeMillis().toString() }
            val htmlContent = binding.richEditor.html ?: ""

            android.util.Log.d("DocsDetailActivity", "Saving to Firebase...")
            android.util.Log.d("DocsDetailActivity", "  - docId: $docId")
            android.util.Log.d("DocsDetailActivity", "  - docTitle: $docTitle")

            viewModel.shareDocumentWithUsers(
                documentId = docId,
                documentTitle = docTitle,
                htmlContent = htmlContent
            )

            toast("Saved to cloud successfully")
        }
    }

    private fun saveToUri(uri: Uri) {
        try {
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val html = withContext(Dispatchers.Main) {
                        binding.richEditor.html ?: ""
                    }

                    if (html.isBlank()) {
                        withContext(Dispatchers.Main) {
                            toast("Không có nội dung để lưu")
                        }
                        return@launch
                    }

                    val document = XWPFDocument()
                    parseHtmlToDocx(html, document)

                    contentResolver.openOutputStream(uri)?.use { outputStream ->
                        document.write(outputStream)
                        document.close()
                    }

                    withContext(Dispatchers.Main) {
                        toast("Đã lưu file thành công")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    withContext(Dispatchers.Main) {
                        toast("Lỗi khi lưu file: ${e.message}")
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            toast("Không thể lấy nội dung từ editor")
        }
    }

    private fun parseHtmlToDocx(html: String, document: XWPFDocument) {
        val doc = Jsoup.parse(html)

        doc.body().children().forEach { element ->
            when (element.tagName().lowercase()) {
                "h1", "h2", "h3", "h4", "h5", "h6", "p" -> {
                    val paragraph = document.createParagraph()
                    val style = element.attr("style")
                    when {
                        style.contains("text-align:center") || style.contains("text-align: center") -> paragraph.alignment =
                            ParagraphAlignment.CENTER

                        style.contains("text-align:right") || style.contains("text-align: right") -> paragraph.alignment =
                            ParagraphAlignment.RIGHT

                        style.contains("text-align:justify") || style.contains("text-align: justify") -> paragraph.alignment =
                            ParagraphAlignment.BOTH

                        else -> paragraph.alignment =
                            ParagraphAlignment.LEFT
                    }
                    processElement(element, paragraph)
                }

                "ul", "ol" -> {
                    element.children().forEach { li ->
                        val paragraph = document.createParagraph()
                        paragraph.setNumID(BigInteger.ONE)
                        processElement(li, paragraph)
                    }
                }
            }
        }
    }

    private fun processElement(element: Element, paragraph: XWPFParagraph) {
        element.childNodes().forEach { node ->
            when (node) {
                is TextNode -> {
                    val text = node.text()
                    if (text.isNotBlank()) {
                        val run = paragraph.createRun()
                        run.setText(text)
                    }
                }

                is Element -> {
                    processElementNode(node, paragraph)
                }
            }
        }
    }

    private fun processElementNode(node: Element, paragraph: XWPFParagraph) {
        val run = paragraph.createRun()
        var text = node.text()

        if (text.isBlank()) return

        var isBold = false
        var isItalic = false
        var isUnderline = false
        var isStrikethrough = false

        var currentNode: Element? = node
        while (currentNode != null) {
            when (currentNode.tagName().lowercase()) {
                "b", "strong" -> isBold = true
                "i", "em" -> isItalic = true
                "u" -> isUnderline = true
                "s", "strike", "del" -> isStrikethrough = true
            }
            currentNode = currentNode.parent() as? Element
        }
        run.isBold = isBold
        run.isItalic = isItalic
        if (isUnderline) run.underline = UnderlinePatterns.SINGLE
        if (isStrikethrough) run.isStrikeThrough = true

        val style = node.attr("style")
        if (style.isNotEmpty()) {
            "font-size:\\s*(\\d+)px".toRegex().find(style)?.groupValues?.get(1)?.toIntOrNull()
                ?.let {
                    run.fontSize = it
                }

            "color:\\s*#([0-9a-fA-F]{6})".toRegex().find(style)?.groupValues?.get(1)?.let {
                run.color = it.uppercase()
            }

            "background-color:\\s*#([0-9a-fA-F]{6})".toRegex().find(style)?.groupValues?.get(1)
                ?.let { color ->
                    when (color.lowercase()) {
                        "ffff00" -> run.setTextHighlightColor("yellow")
                        "00ff00" -> run.setTextHighlightColor("green")
                        "00ffff" -> run.setTextHighlightColor("cyan")
                        "ff00ff" -> run.setTextHighlightColor("magenta")
                    }
                }

            "font-family:\\s*['\"]?([^;'\"]+)['\"]?".toRegex().find(style)?.groupValues?.get(1)
                ?.let { fontFamily ->
                    run.fontFamily = fontFamily.trim()
                }
        }
        run.setText(text)
    }

    private fun getLocalImagePath(uri: Uri): String {
        val inputStream = contentResolver.openInputStream(uri)
        val file = File(cacheDir, "image_${System.currentTimeMillis()}.jpg")
        inputStream?.use { input ->
            FileOutputStream(file).use { output -> input.copyTo(output) }
        }
        return "file://${file.absolutePath}"
    }
}