package com.wodox.docs.font

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.wodox.core.extension.debounceClick
import com.wodox.core.ui.adapter.TMVVMAdapter
import com.wodox.core.ui.adapter.TMVVMViewHolder
import com.wodox.docs.BR
import com.wodox.docs.databinding.ItemFontLayoutBinding
import com.wodox.domain.docs.model.TextFormat

class FontAdapter(val context: Context, private var listener: OnClickListener) :
    TMVVMAdapter<TextFormat>(ArrayList()) {
    interface OnClickListener {
        fun onClick(font: TextFormat)
    }

    override fun onCreateViewHolderBase(parent: ViewGroup?, viewType: Int): TMVVMViewHolder {
        val binding =
            ItemFontLayoutBinding.inflate(
                LayoutInflater.from(parent?.context),
                parent,
                false
            )
        return TMVVMViewHolder(binding)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolderBase(holder: TMVVMViewHolder?, position: Int) {
        val font = list[position]
        val binding = holder?.binding as ItemFontLayoutBinding

        binding.apply {
            tvName.typeface = font.typeface(context)
            llContainer.debounceClick {
                listener.onClick(font)
            }
        }
        binding.setVariable(BR.font, font)
        binding.executePendingBindings()
    }
}