package com.wodox.ui.task.aibottomsheet

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.wodox.core.ui.adapter.TMVVMAdapter
import com.wodox.core.ui.adapter.TMVVMViewHolder
import com.wodox.home.BR
import com.wodox.home.databinding.ItemAiResponseMessageUserLayoutBinding
import com.wodox.home.databinding.ItemMessageAiLayoutBinding
import com.wodox.model.Message

class MessageAdapter(
    private val context: Context?,
    private val onTypingComplete: (() -> Unit)? = null,
    private val onDeleteMessage: ((position: Int) -> Unit)? = null
) : TMVVMAdapter<Message>(ArrayList()) {

    companion object {
        private const val VIEW_TYPE_USER = 1
        private const val VIEW_TYPE_AI = 2
        private const val TYPING_DELAY = 20L
    }

    private val handler = Handler(Looper.getMainLooper())
    private var currentTypingRunnable: Runnable? = null

    override fun getItemViewType(position: Int): Int {
        return if (list.getOrNull(position)?.isUser == true) VIEW_TYPE_USER else VIEW_TYPE_AI
    }

    override fun onCreateViewHolderBase(
        parent: ViewGroup?,
        viewType: Int
    ): TMVVMViewHolder {
        return when (viewType) {
            VIEW_TYPE_USER -> {
                val binding = ItemAiResponseMessageUserLayoutBinding.inflate(
                    LayoutInflater.from(parent?.context),
                    parent,
                    false
                )
                TMVVMViewHolder(binding)
            }

            else -> {
                val binding = ItemMessageAiLayoutBinding.inflate(
                    LayoutInflater.from(parent?.context),
                    parent,
                    false
                )
                TMVVMViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolderBase(
        holder: TMVVMViewHolder?,
        position: Int
    ) {
        val message = list.getOrNull(position) ?: return
        val binding = holder?.binding as? ViewDataBinding ?: return

        binding.setVariable(BR.item, message)
        binding.executePendingBindings()
    }

    fun addMessage(message: Message) {
        list.add(message)
        notifyItemInserted(list.size - 1)
    }

    fun addAIMessageWithTyping(fullText: String) {
        stopTypingAnimation()

        val message = Message(text = "", isUser = false)
        list.add(message)
        val position = list.size - 1
        notifyItemInserted(position)

        startTypingAnimation(fullText, position)
    }

    // ✅ Batch update messages (for loading history)
    fun updateMessages(messages: List<Message>) {
        stopTypingAnimation()
        list.clear()
        list.addAll(messages.sortedBy { it.timestamp })
        notifyDataSetChanged()
    }

    private fun startTypingAnimation(fullText: String, position: Int) {
        if (fullText.isEmpty()) {
            onTypingComplete?.invoke()
            return
        }

        var currentIndex = 0

        currentTypingRunnable = object : Runnable {
            override fun run() {
                if (currentIndex < fullText.length && position < list.size) {
                    currentIndex++
                    val displayText = fullText.substring(0, currentIndex)

                    list.getOrNull(position)?.text = displayText
                    notifyItemChanged(position)

                    handler.postDelayed(this, TYPING_DELAY)
                } else {
                    currentTypingRunnable = null
                    onTypingComplete?.invoke()
                }
            }
        }

        handler.post(currentTypingRunnable!!)
    }

    private fun stopTypingAnimation() {
        currentTypingRunnable?.let {
            handler.removeCallbacks(it)
            currentTypingRunnable = null
        }
    }

    override fun clear() {
        stopTypingAnimation()
        list.clear()
        notifyDataSetChanged()
    }

    fun deleteMessage(position: Int) {
        if (position in 0 until list.size) {
            list.removeAt(position)
            notifyItemRemoved(position)
            onDeleteMessage?.invoke(position)
        }
    }

    fun cleanup() {
        stopTypingAnimation()
        handler.removeCallbacksAndMessages(null)
    }
}