package com.wodox.mywork.ui


import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.wodox.core.ui.adapter.TMVVMAdapter
import com.wodox.core.ui.adapter.TMVVMViewHolder
import com.wodox.mywork.databinding.ItemDayLayoutBinding
import com.wodox.mywork.model.DayItem
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DayAdapter(
    private val context: Context?,
    private val listener: OnItemClickListener
) : TMVVMAdapter<DayItem>(ArrayList()) {

    private var selectedPosition = RecyclerView.NO_POSITION

    interface OnItemClickListener {
        fun onDayClicked(date: java.util.Date)
    }

    override fun onCreateViewHolderBase(
        parent: ViewGroup?,
        viewType: Int
    ): TMVVMViewHolder {
        val binding = ItemDayLayoutBinding.inflate(
            LayoutInflater.from(parent?.context),
            parent,
            false
        )
        return TMVVMViewHolder(binding)
    }

    override fun onBindViewHolderBase(holder: TMVVMViewHolder?, position: Int) {
        val item = list.getOrNull(position) ?: return
        val binding = holder?.binding as ItemDayLayoutBinding

        val date = item.date
        val calendar = Calendar.getInstance().apply { time = date }
        val dayName = SimpleDateFormat("EEE", Locale.getDefault()).format(date)
        val dayNumber = calendar.get(Calendar.DAY_OF_MONTH).toString()

        val colorRes = if (position == selectedPosition) {
            ContextCompat.getColor(context!!, com.wodox.resources.R.color.color63E6C8)
        } else {
            ContextCompat.getColor(context!!, android.R.color.white)
        }

        binding.clcontainer.setBackgroundColor(colorRes)

        binding.tvDayName.text = dayName
        binding.tvDayNumber.text = dayNumber

        binding.root.setOnClickListener {
            val oldPos = selectedPosition
            selectedPosition = position
            notifyItemChanged(oldPos)
            notifyItemChanged(selectedPosition)

            listener.onDayClicked(date)
        }
    }

    fun updateList(newList: List<DayItem>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }

}

