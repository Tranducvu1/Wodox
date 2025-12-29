package com.wodox.ui.task.taskdetail.activitytask

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.wodox.core.ui.adapter.TMVVMAdapter
import com.wodox.core.ui.adapter.TMVVMViewHolder
import com.wodox.domain.home.model.local.Log
import com.wodox.domain.home.model.local.LogType
import com.wodox.home.BR
import com.wodox.home.databinding.ItemLogLayoutBinding


class LogAdapter(
    private val context: Context?,
    private val listener: OnItemClickListener? = null
) : TMVVMAdapter<Log>(ArrayList()) {

    interface OnItemClickListener {
        fun onLogClick(log: Log)
    }


    override fun onCreateViewHolderBase(
        parent: ViewGroup?,
        viewType: Int
    ): TMVVMViewHolder {
        val binding = ItemLogLayoutBinding.inflate(
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
        val log = list.getOrNull(position) ?: return
        val binding = holder?.binding as ItemLogLayoutBinding

        val logType = determineLogType(log)

        setupLogIcon(binding, logType)

        binding.root.setOnClickListener {
            listener?.onLogClick(log)
        }

        binding.setVariable(BR.item, log)
        binding.executePendingBindings()
    }

    private fun determineLogType(log: Log): LogType {
        val title = log.title?.lowercase() ?: return LogType.UNKNOWN
        val ctx = context ?: return LogType.UNKNOWN

        val home = ctx.getString(com.wodox.resources.R.string.home).lowercase()
        val created = ctx.getString(com.wodox.resources.R.string.create).lowercase()
        val dated = ctx.getString(com.wodox.resources.R.string.date).lowercase()
        val assigned = ctx.getString(com.wodox.resources.R.string.assigned_to_me).lowercase()
        val priority = ctx.getString(com.wodox.resources.R.string.priority).lowercase()
        val comment = ctx.getString(com.wodox.resources.R.string.comments).lowercase()

        return when {
            home in title -> LogType.STATUS_CHANGE
            created in title -> LogType.CREATED
            dated in title -> LogType.DATE_CHANGED
            assigned in title -> LogType.ASSIGNED
            priority in title -> LogType.PRIORITY_CHANGED
            comment in title -> LogType.COMMENT
            else -> LogType.UNKNOWN
        }
    }

    private fun setupLogIcon(binding: ItemLogLayoutBinding, type: LogType) {
        val iconRes = when (type) {
            LogType.STATUS_CHANGE -> com.wodox.resources.R.drawable.ic_status_change
            LogType.CREATED -> com.wodox.resources.R.drawable.ic_created
            LogType.DATE_CHANGED -> com.wodox.resources.R.drawable.ic_date_changed
            LogType.ASSIGNED -> com.wodox.resources.R.drawable.ic_person
            LogType.PRIORITY_CHANGED -> com.wodox.home.R.drawable.ic_flag
            LogType.COMMENT -> com.wodox.resources.R.drawable.ic_comment
            else -> com.wodox.resources.R.drawable.ic_help
        }

        binding.ivIcon.setImageResource(iconRes)
    }

}