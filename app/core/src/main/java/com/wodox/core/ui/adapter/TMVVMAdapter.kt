package com.wodox.core.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class TMVVMAdapter<T>(var list: ArrayList<T>) : RecyclerView.Adapter<TMVVMViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TMVVMViewHolder {
        return onCreateViewHolderBase(parent, viewType)
    }

    override fun onBindViewHolder(holder: TMVVMViewHolder, position: Int) {
        onBindViewHolderBase(holder, position)
    }

    override fun getItemCount(): Int = list.size

    abstract fun onCreateViewHolderBase(parent: ViewGroup?, viewType: Int): TMVVMViewHolder

    abstract fun onBindViewHolderBase(holder: TMVVMViewHolder?, position: Int)

    open fun submitList(newList: List<T>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }

    fun addItems(items: List<T>) {
        val startPosition = list.size
        list.addAll(items)
        notifyItemRangeInserted(startPosition, items.size)
    }

    fun addItem(item: T) {
        list.add(item)
        notifyItemInserted(list.size - 1)
    }


    fun removeItem(position: Int) {
        if (position in list.indices) {
            list.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    open fun clear() {
        val size = list.size
        list.clear()
        notifyItemRangeRemoved(0, size)
    }

    fun getItem(position: Int): T? = list.getOrNull(position)

    fun getList(): List<T> = list.toList()
}

class TMVVMViewHolder(val binding: ViewBinding) : RecyclerView.ViewHolder(binding.root)