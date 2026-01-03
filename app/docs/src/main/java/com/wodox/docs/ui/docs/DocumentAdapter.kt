package com.wodox.docs.ui.docs

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wodox.core.extension.debounceClick
import com.wodox.core.ui.adapter.TMVVMAdapter
import com.wodox.core.ui.adapter.TMVVMViewHolder
import com.wodox.docs.databinding.ItemDocumentBinding
import com.wodox.domain.docs.model.model.SharedDocument
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class DocumentAdapter(
    private val context: Context?,
    private val listener: OnItemClickListener
) : TMVVMAdapter<SharedDocument>(ArrayList()) {

    private var currentUserId: String? = null

    interface OnItemClickListener {
        fun onItemClick(document: SharedDocument)
        fun onDeleteClick(document: SharedDocument)
    }

    fun setCurrentUserId(userId: String?) {
        currentUserId = userId
        notifyDataSetChanged()
    }

    override fun onCreateViewHolderBase(
        parent: ViewGroup?,
        viewType: Int
    ): TMVVMViewHolder {
        val binding = ItemDocumentBinding.inflate(
            LayoutInflater.from(parent?.context),
            parent,
            false
        )
        return TMVVMViewHolder(binding)
    }

    override fun onBindViewHolderBase(
        holder: TMVVMViewHolder?,
        position: Int
    ) {
        val document = list.getOrNull(position) ?: return
        val binding = holder?.binding as? ItemDocumentBinding ?: return

        binding.apply {
            tvDocTitle.text = document.documentTitle

            val isOwnedByCurrentUser = currentUserId != null &&
                    document.ownerUserId == currentUserId

            if (isOwnedByCurrentUser) {
                tvOwnerInfo.visibility = View.VISIBLE

                if (document.invitedUsers.isEmpty()) {
                    tvOwnerInfo.text = "You • Not shared"
                    android.util.Log.d(
                        "DocumentAdapter",
                        "Setting owner text (no shares): ${tvOwnerInfo.text}"
                    )
                } else {
                    val peopleCount = document.invitedUsers.size
                    tvOwnerInfo.text =
                        "You • $peopleCount ${if (peopleCount == 1) "person" else "people"} shared"
                    android.util.Log.d(
                        "DocumentAdapter",
                        "Setting owner text (with shares): ${tvOwnerInfo.text}"
                    )
                }
            } else {
                tvOwnerInfo.visibility = View.VISIBLE
                tvOwnerInfo.text = "Shared by ${document.ownerUserName}"

                val userPermission = document.invitedUsers
                    .find { it.userId.toString() == currentUserId }
                    ?.permission?.name

                if (userPermission != null) {
                    tvOwnerInfo.text =
                        "Shared by ${document.ownerUserName} • ${getPermissionText(userPermission)}"
                }
            }

            tvLastModified.text = formatLastModified(document.lastModified)

            llDocument.debounceClick {
                listener.onItemClick(document)
            }

            ivDelete.visibility = if (isOwnedByCurrentUser) View.VISIBLE else View.GONE
            ivDelete.debounceClick {
                listener.onDeleteClick(document)
            }
        }
    }

    private fun getPermissionText(permission: String): String {
        return when (permission) {
            "EDIT" -> "Can edit"
            "VIEW" -> "View only"
            else -> ""
        }
    }

    private fun formatLastModified(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp

        return when {
            diff < TimeUnit.MINUTES.toMillis(1) -> "Just now"
            diff < TimeUnit.HOURS.toMillis(1) -> {
                val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
                "$minutes ${if (minutes == 1L) "minute" else "minutes"} ago"
            }

            diff < TimeUnit.DAYS.toMillis(1) -> {
                val hours = TimeUnit.MILLISECONDS.toHours(diff)
                "$hours ${if (hours == 1L) "hour" else "hours"} ago"
            }

            diff < TimeUnit.DAYS.toMillis(7) -> {
                val days = TimeUnit.MILLISECONDS.toDays(diff)
                "$days ${if (days == 1L) "day" else "days"} ago"
            }

            else -> {
                val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                sdf.format(Date(timestamp))
            }
        }
    }
}
