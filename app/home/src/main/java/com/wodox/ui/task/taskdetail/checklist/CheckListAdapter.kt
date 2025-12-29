package com.wodox.ui.task.taskdetail.checklist

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.wodox.core.ui.adapter.TMVVMAdapter
import com.wodox.core.ui.adapter.TMVVMViewHolder
import com.wodox.domain.home.model.local.CheckList
import com.wodox.home.BR
import com.wodox.home.databinding.ItemCheckListTaskLayoutBinding

class CheckListAdapter(
    private val context: Context?,
    private val listener: OnItemClickListener
) : TMVVMAdapter<CheckList>(ArrayList()) {

    interface OnItemClickListener {
        fun onClick(checkList: CheckList)
        fun onDeleteClick(checkList: CheckList)
    }

    override fun onCreateViewHolderBase(
        parent: ViewGroup?,
        viewType: Int
    ): TMVVMViewHolder {
        val binding = ItemCheckListTaskLayoutBinding.inflate(
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
        val subtask = list.getOrNull(position) ?: return
        val binding = holder?.binding as ItemCheckListTaskLayoutBinding

        binding.root.setOnClickListener {
            listener.onClick(subtask)
        }
        binding.setVariable(BR.item, subtask)
        binding.executePendingBindings()
    }

}