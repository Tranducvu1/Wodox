package com.wodox.chat.ui.channelchat

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.app.Dialog
import com.wodox.core.extension.toast
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChannelInfoDialogHandler(
    private val context: Context,
    private val channel: com.wodox.domain.chat.model.Channel,
    private val dialog: Dialog
) {

    fun onClose(view: android.view.View) {
        dialog.dismiss()
    }

    fun onCancel(view: android.view.View) {
        dialog.dismiss()
    }

    fun onCopyInfo(view: android.view.View) {
        val infoText = buildString {
            append("📌 ${channel.name}\n")
            append("${channel.description ?: "No description"}\n\n")
            append("👥 Members: ${channel.memberCount}\n")
            append("📅 Created: ${formatTimestamp(channel.createdAt)}\n")
            append("🔒 ${if (channel.isPrivate) "Private Channel" else "Public Channel"}")
        }

        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Channel Info", infoText)
        clipboard.setPrimaryClip(clip)

        context.toast("Channel info copied!")
        dialog.dismiss()
    }

    fun formatTimestamp(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}