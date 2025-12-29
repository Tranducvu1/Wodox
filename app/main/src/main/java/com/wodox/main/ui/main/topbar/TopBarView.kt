package com.wodox.main.ui.main.topbar

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wodox.core.extension.AbstractView
import com.wodox.main.databinding.ItemTopBarViewBinding
import com.wodox.main.BR
import com.wodox.main.R

class TopBarView(context: Context, attrs: AttributeSet?) : AbstractView(context, attrs) {
    interface OnTopBarViewListener {
        fun onClick(menu: TopBarMenu)
    }

    var listener: OnTopBarViewListener? = null

    var data: List<TopBarMenu> = arrayListOf()
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


    override fun layoutId(): Int = R.layout.item_top_bar_view

    override fun viewBinding() = binding as ItemTopBarViewBinding

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
                TopBarAdapter(context, object : TopBarAdapter.OnItemClickListener {
                    override fun onClick(menu: TopBarMenu) {
                        listener?.onClick(menu)
                    }
                })
        }
    }
}

object TopBarBindingAdapter {
    @BindingAdapter(value = ["items"])
    @JvmStatic
    fun <T> setItems(view: TopBarView, items: List<TopBarMenu>?) {
        view.data = items ?: arrayListOf()
    }
}