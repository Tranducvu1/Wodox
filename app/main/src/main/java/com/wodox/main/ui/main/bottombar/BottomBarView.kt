package com.wodox.main.ui.main.bottombar

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wodox.core.extension.AbstractView
import com.wodox.main.databinding.ItemBottomBarViewBinding
import com.wodox.main.BR
import com.wodox.main.R

class BottomBarView(context: Context, attrs: AttributeSet?) : AbstractView(context, attrs) {
    interface OnBottomBarViewListener {
        fun onClick(menu: BottomBarMenu)
    }

    var listener: OnBottomBarViewListener? = null

    var data: List<BottomBarMenu> = arrayListOf()
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


    override fun layoutId(): Int = R.layout.item_bottom_bar_view

    override fun viewBinding() = binding as ItemBottomBarViewBinding

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
                BottomBarAdapter(context, object : BottomBarAdapter.OnItemClickListener {
                    override fun onClick(menu: BottomBarMenu) {
                        listener?.onClick(menu)
                    }
                })
        }
    }
}

object BottomBarBindingAdapter {
    @BindingAdapter(value = ["items"])
    @JvmStatic
    fun <T> setItems(view: BottomBarView, items: List<BottomBarMenu>?) {
        view.data = items ?: arrayListOf()
    }
}