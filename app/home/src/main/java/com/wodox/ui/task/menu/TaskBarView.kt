package com.wodox.ui.task.menu

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wodox.core.extension.AbstractView
import com.wodox.home.databinding.ItemTaskBarViewBinding
import com.wodox.home.BR
import com.wodox.home.R

class TaskBarView(context: Context, attrs: AttributeSet?) : AbstractView(context, attrs) {
    interface OnTaskBarViewListener {
        fun onClick(menu: TaskBarMenu)
    }

    var listener: OnTaskBarViewListener? = null

    var data: List<TaskBarMenu> = arrayListOf()
        set(value) {
            field = value
            if (value.isEmpty()) {
                return
            }
            viewBinding().recyclerView.layoutManager = object :
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false) {
                override fun checkLayoutParams(lp: RecyclerView.LayoutParams?): Boolean {
                    lp?.width = viewBinding().recyclerView.width / value.size
                    return true
                }
            }

            data.firstOrNull()?.isSelected = true
            binding.setVariable(BR.menus, data)
            binding.executePendingBindings()
        }


    override fun layoutId(): Int = R.layout.item_task_bar_view

    override fun viewBinding() = binding as ItemTaskBarViewBinding

    override fun viewInitialized() {
        setupRecyclerView()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun selectPosition(position: Int) {
        for ((index, item) in data.withIndex()) {
            item.isSelected = position == index
        }
        viewBinding().recyclerView.adapter?.notifyDataSetChanged()
        data.firstOrNull { it.isSelected }?.let { listener?.onClick(it) }
    }

    private fun setupRecyclerView() {
        with(viewBinding()) {
            recyclerView.adapter =
                TaskBarAdapter(context, object : TaskBarAdapter.OnItemClickListener {
                    override fun onClick(menu: TaskBarMenu) {
                        listener?.onClick(menu)
                    }
                })
        }
    }
}

object TaskBarBindingAdapter {
    @BindingAdapter(value = ["items"])
    @JvmStatic
    fun <T> setItems(view: TaskBarView, items: List<TaskBarMenu>?) {
        view.data = items ?: arrayListOf()
    }
}