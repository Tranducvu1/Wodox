package com.wodox.ui.task.taskdetail


import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.wodox.core.extension.show
import com.wodox.core.ui.adapter.TMVVMAdapter
import com.wodox.core.ui.adapter.TMVVMViewHolder
import com.wodox.domain.home.model.local.SubTask
import com.wodox.home.BR
import com.wodox.home.databinding.ItemSubTaskLayoutBinding
import java.text.SimpleDateFormat
import java.util.Locale

class SubTaskAdapter(
    private val context: Context?,
    private val listener: OnItemClickListener
) : TMVVMAdapter<SubTask>(ArrayList()) {

    interface OnItemClickListener {
        fun onClick(subTask: SubTask)
        fun onDeleteClick(subTask: SubTask)

        fun onClickShow(subTask: SubTask)
    }

    override fun onCreateViewHolderBase(
        parent: ViewGroup?,
        viewType: Int
    ): TMVVMViewHolder {
        val binding = ItemSubTaskLayoutBinding.inflate(
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
        val binding = holder?.binding as ItemSubTaskLayoutBinding

        binding.root.setOnClickListener {
            listener.onClick(subtask)
        }

        binding.ivDelete.setOnClickListener {
            listener.onDeleteClick(subtask)
        }
        binding.llDate.show(subtask.startAt != null || subtask.dueAt != null)
        binding.setVariable(BR.item, subtask)
        binding.executePendingBindings()
    }

    companion object {
        @JvmStatic
        @BindingAdapter("formatDateRange")
        fun TextView.setFormatDateRange(item: SubTask?) {
            item ?: return

            val startDate = item.startAt
            val endDate = item.dueAt

            if (startDate != null && endDate != null) {
                val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                text = "${formatter.format(startDate)} - ${formatter.format(endDate)}"
            } else {
                text = ""
            }
        }
    }
}