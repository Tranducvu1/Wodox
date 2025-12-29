package com.wodox.docs.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.wodox.core.extension.debounceClick
import com.wodox.core.extension.launchWhenStarted
import com.wodox.core.extension.toast
import com.wodox.core.extension.showAllowingStateLoss
import com.wodox.common.extension.showMoreColor
import com.wodox.common.navigation.MainNavigator
import com.wodox.common.view.colorpicker.TextSizePopupView
import com.wodox.core.base.fragment.BaseFragment
import com.wodox.docs.R
import com.wodox.docs.databinding.DocsFragmentLayoutBinding
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
import org.apache.poi.xwpf.usermodel.UnderlinePatterns
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.apache.poi.xwpf.usermodel.XWPFParagraph
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class DocsFragment : BaseFragment<DocsFragmentLayoutBinding, DocViewModel>(
    DocViewModel::class
) {
    @Inject
    lateinit var mainNavigator: MainNavigator

    override fun layoutId(): Int = R.layout.docs_fragment_layout

    private lateinit var invitedUserAdapter: InvitedUserAdapter
    private var autoSaveJob: Job? = null
    private var isSharedDocument = false
    private var canEdit = true

    private val openDocumentLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
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
                requireContext().toast("Đã chèn video thành công")
            } catch (e: Exception) {
                e.printStackTrace()
                requireContext().toast("Không thể chèn video")
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
                requireContext().toast("Đã chèn ảnh thành công")
            } catch (e: Exception) {
                e.printStackTrace()
                requireContext().toast("Không thể chèn ảnh")
            }
        }
    }

    private val createDocumentLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
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

        val docTitle = binding.etDocTitle.text.toString()
            .ifBlank { System.currentTimeMillis().toString() }
        viewModel.dispatch(DocUiAction.SetDocumentId(docTitle))

        if (!isSharedDocument) {
            viewModel.loadInvitedUsersFromPreferences(docTitle)
        }

        setupAutoSave()
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

        val args = arguments
        isSharedDocument = args?.getBoolean("is_shared", false) ?: false

        if (isSharedDocument) {
            val docId = args?.getString("doc_id", "") ?: ""
            val docTitle = args?.getString("doc_title", "") ?: ""
            val docHtml = args?.getString("doc_html", "") ?: ""
            val ownerName = args?.getString("doc_owner", "") ?: ""
            val permission = args?.getString("doc_permission", "VIEW") ?: "VIEW"

            binding.etDocTitle.setText(docTitle)
            binding.richEditor.html = docHtml
            binding.tvLastSaved.text = "Shared by $ownerName"

            viewModel.dispatch(DocUiAction.SetDocumentId(docId))

            viewModel.loadSharedDocumentById(docId)

            canEdit = permission == "EDIT"

            if (!canEdit) {
                binding.richEditor.isEnabled = false
                binding.etDocTitle.isEnabled = false

                binding.llEditingTools.visibility = android.view.View.GONE

                requireContext().toast("Bạn chỉ có quyền xem document này")
            }
        }
    }

    private fun setupInvitedUsersRecyclerView() {
        invitedUserAdapter = InvitedUserAdapter { user ->
            if (!isSharedDocument || canEdit) {
                viewModel.dispatch(DocUiAction.RemoveInvitedUser(user.userId))
                requireContext().toast("Đã xóa ${user.userName}")
            } else {
                requireContext().toast("Bạn không có quyền xóa người dùng")
            }
        }

        binding.rvInvitedUsers.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
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
                    requireContext().toast("Bạn không có quyền chia sẻ document này")
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
                TextSizePopupView.show(view) { selectedSize ->
                    richEditor.setFontSize(selectedSize)
                    tvNumber.text = selectedSize.toString()
                    this@DocsFragment.viewModel.dispatch(DocUiAction.UpdateTextSize(selectedSize))
                }
            }

            ivImage.debounceClick { pickImageLauncher.launch("image/*") }
            ivFont.debounceClick { handleChangeFont() }
            ivVideo.debounceClick { showVideoUrlDialog() }
        }
    }

    private fun showUserSelectionBottomSheet() {
        val bottomSheet = ListUserBottomSheet.newInstance()
        bottomSheet.listener = object : ListUserBottomSheet.OnItemClickListener {
            override fun onClick(id: UUID) {
                viewModel.dispatch(DocUiAction.AssignUser(id))
                showPermissionDialog(id)
            }
        }
        bottomSheet.showAllowingStateLoss(childFragmentManager, "ListUserBottomSheet")
    }

    private fun showPermissionDialog(userId: UUID) {
        val permissions = arrayOf("Chỉ xem", "Có thể chỉnh sửa")
        var selectedPermission = 0

        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Chọn quyền cho người dùng")
            .setSingleChoiceItems(permissions, selectedPermission) { _, which ->
                selectedPermission = which
            }
            .setPositiveButton("Thêm") { _, _ ->
                addUserToDocument(userId, selectedPermission)
            }
            .setNegativeButton("Hủy", null)
            .show()
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
                        userName = user.name ?: "Unknown",
                        userEmail = user.email ?: "",
                        permission = permission
                    )

                    withContext(Dispatchers.Main) {
                        viewModel.dispatch(DocUiAction.AddInvitedUser(invitedUser))

                        val docId = viewModel.uiState.value.documentId
                        val docTitle = binding.etDocTitle.text.toString()
                            .ifBlank { System.currentTimeMillis().toString() }
                        val htmlContent = binding.richEditor.html ?: ""

                        viewModel.shareDocumentWithUsers(
                            documentId = docId,
                            documentTitle = docTitle,
                            htmlContent = htmlContent
                        )
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    requireContext().toast("Lỗi: ${e.message}")
                }
            }
        }
    }

    // ✅ Auto-save cho shared documents
    private fun setupAutoSave() {
        if (!isSharedDocument || !canEdit) return

        binding.richEditor.setOnTextChangeListener { _ ->
            autoSaveJob?.cancel()
            autoSaveJob = lifecycleScope.launch {
                delay(3000)

                val docId = viewModel.uiState.value.documentId
                val docTitle = binding.etDocTitle.text.toString()
                val htmlContent = binding.richEditor.html ?: ""

                viewModel.updateSharedDocument(docId, docTitle, htmlContent)

                val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                binding.tvLastSaved.text = "Đã lưu lúc $currentTime"
            }
        }
    }

    private fun observeState() {
        launchWhenStarted {
            viewModel.uiState.collect { state ->
                binding.tvNumber.text = state.textSize.toString()

                invitedUserAdapter.updateUsers(state.invitedUsers)

                binding.llInvitedUsers.visibility =
                    if (state.invitedUsers.isNotEmpty())
                        android.view.View.VISIBLE
                    else
                        android.view.View.GONE
            }
        }
    }

    // ✅ Observe events từ ViewModel
    private fun observeEvents() {
        launchWhenStarted {
            viewModel.uiEvent.collect { event ->
                when (event) {
                    is DocUiEvent.DocumentSharedSuccessfully -> {
                        requireContext().toast("Đã chia sẻ với ${event.userCount} người")
                    }
                    is DocUiEvent.SharingFailed -> {
                        requireContext().toast("Lỗi chia sẻ: ${event.message}")
                    }
                    is DocUiEvent.DocumentLoadedFromServer -> {
                        // Load invited users từ document
                        event.document.invitedUsers.forEach { invitedUser ->
                            viewModel.dispatch(DocUiAction.AddInvitedUser(invitedUser))
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    private fun showVideoUrlDialog() {
        val editText = android.widget.EditText(requireContext())
        editText.hint = "https://www.youtube.com/watch?v=..."

        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Nhập đường dẫn video YouTube")
            .setView(editText)
            .setPositiveButton("Chèn") { _, _ ->
                val url = editText.text.toString().trim()
                if (url.isNotEmpty() && isValidYoutubeUrl(url)) {
                    insertYoutubeVideoHtml(url)
                    requireContext().toast("Đã chèn video YouTube")
                } else {
                    requireContext().toast("URL YouTube không hợp lệ")
                }
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun isValidYoutubeUrl(url: String): Boolean {
        return url.contains("youtube.com/watch?v=") ||
                url.contains("youtu.be/") ||
                url.contains("youtube.com/embed/")
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
        val bottomSheet = FontBottomSheet.newInstance(viewModel.textFormat)
        bottomSheet.listener = object : FontBottomSheet.OnItemClickListener {
            override fun onClick(font: TextFormat) {
                viewModel.textFormat.fontName = font.fontName
                viewModel.textFormat.componentToChange = TextFormat.ChangeComponent.FONT
                applyFormat()
            }
        }
        bottomSheet.showAllowingStateLoss(childFragmentManager, "FontBottomSheet")
    }

    private fun applyFormat() {
        val fontName = viewModel.textFormat.fontName
        val displayFontName = fontName.split("_")
            .joinToString(" ") {
                it.replaceFirstChar { ch ->
                    if (ch.isLowerCase()) ch.titlecase() else ch.toString()
                }
            }
        binding.richEditor.setFontName(displayFontName)
    }

    private fun setupColorPicker(onColorSelected: (Int) -> Unit) {
        requireActivity().showMoreColor(
            defaultColor = ContextCompat.getColor(
                requireActivity(), com.wodox.core.R.color.black
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
            val type = requireContext().contentResolver.getType(uri)
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            if (type == "application/vnd.openxmlformats-officedocument.wordprocessingml.document") {
                val doc = XWPFDocument(inputStream)
                val htmlBuilder = StringBuilder()

                for (paragraph in doc.paragraphs) {
                    val alignment = when (paragraph.alignment) {
                        org.apache.poi.xwpf.usermodel.ParagraphAlignment.CENTER -> "center"
                        org.apache.poi.xwpf.usermodel.ParagraphAlignment.RIGHT -> "right"
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
                                "yellow" to "#ffff00", "cyan" to "#00ffff",
                                "green" to "#00ff00", "magenta" to "#ff00ff"
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
            requireContext().toast("Cannot open file")
        }
    }

    private fun getLocalVideoPath(uri: Uri): String {
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val file = File(requireContext().cacheDir, "video_${System.currentTimeMillis()}.mp4")
        inputStream?.use { input ->
            FileOutputStream(file).use { output -> input.copyTo(output) }
        }
        return "file://${file.absolutePath}"
    }

    private fun openSaveFilePicker() {
        val title = binding.etDocTitle.text.toString().ifBlank { "Untitled Document" }

        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            putExtra(Intent.EXTRA_TITLE, "$title.docx")
        }
        createDocumentLauncher.launch(intent)
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
                            requireContext().toast("Không có nội dung để lưu")
                        }
                        return@launch
                    }

                    val document = XWPFDocument()
                    parseHtmlToDocx(html, document)

                    requireContext().contentResolver.openOutputStream(uri)?.use { outputStream ->
                        document.write(outputStream)
                        document.close()
                    }

                    withContext(Dispatchers.Main) {
                        requireContext().toast("Đã lưu file thành công")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    withContext(Dispatchers.Main) {
                        requireContext().toast("Lỗi khi lưu file: ${e.message}")
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            requireContext().toast("Không thể lấy nội dung từ editor")
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
                        style.contains("text-align:center") || style.contains("text-align: center") ->
                            paragraph.alignment = org.apache.poi.xwpf.usermodel.ParagraphAlignment.CENTER
                        style.contains("text-align:right") || style.contains("text-align: right") ->
                            paragraph.alignment = org.apache.poi.xwpf.usermodel.ParagraphAlignment.RIGHT
                        style.contains("text-align:justify") || style.contains("text-align: justify") ->
                            paragraph.alignment = org.apache.poi.xwpf.usermodel.ParagraphAlignment.BOTH
                        else ->
                            paragraph.alignment = org.apache.poi.xwpf.usermodel.ParagraphAlignment.LEFT
                    }
                    processElement(element, paragraph)
                }
                "ul", "ol" -> {
                    element.children().forEach { li ->
                        val paragraph = document.createParagraph()
                        paragraph.setNumID(java.math.BigInteger.ONE)
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
            "font-size:\\s*(\\d+)px".toRegex().find(style)?.groupValues?.get(1)?.toIntOrNull()?.let {
                run.fontSize = it
            }

            "color:\\s*#([0-9a-fA-F]{6})".toRegex().find(style)?.groupValues?.get(1)?.let {
                run.color = it.uppercase()
            }

            "background-color:\\s*#([0-9a-fA-F]{6})".toRegex().find(style)?.groupValues?.get(1)?.let { color ->
                when (color.lowercase()) {
                    "ffff00" -> run.setTextHighlightColor("yellow")
                    "00ff00" -> run.setTextHighlightColor("green")
                    "00ffff" -> run.setTextHighlightColor("cyan")
                    "ff00ff" -> run.setTextHighlightColor("magenta")
                }
            }

            "font-family:\\s*['\"]?([^;'\"]+)['\"]?".toRegex().find(style)?.groupValues?.get(1)?.let { fontFamily ->
                run.fontFamily = fontFamily.trim()
            }
        }
        run.setText(text)
    }

    private fun getLocalImagePath(uri: Uri): String {
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val file = File(requireContext().cacheDir, "image_${System.currentTimeMillis()}.jpg")
        inputStream?.use { input ->
            FileOutputStream(file).use { output -> input.copyTo(output) }
        }
        return "file://${file.absolutePath}"
    }

    companion object {
        fun newInstance() = DocsFragment()
    }
}