package com.wodox.mywork.ui

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wodox.core.base.fragment.BaseFragment
import com.wodox.core.extension.addSpaceDecoration
import com.wodox.core.extension.launchWhenStarted
import com.wodox.domain.home.model.local.Comment
import com.wodox.mywork.R
import com.wodox.mywork.databinding.MyWorkFragmentLayoutBinding
import com.wodox.mywork.model.DayItem
import dagger.hilt.android.AndroidEntryPoint
import java.time.ZoneId
import java.util.Calendar
import java.util.Date

@AndroidEntryPoint
class MyWorkFragment : BaseFragment<MyWorkFragmentLayoutBinding, MyWorkViewModel>(
    MyWorkViewModel::class
) {

    private val adapterPagingTask by lazy {
        TaskAdapter(context, object : TaskAdapter.OnItemClickListener {})
    }

    private val adapterPagingDay by lazy {
        DayAdapter(context, object : DayAdapter.OnItemClickListener {
            override fun onDayClicked(date: Date) {
                scrollToTaskForDate(date)
            }
        })
    }

    override fun initialize() {
        setUpRecycleView()
        setUpRecycleViewDay()
        setupUI()
    }

    private fun setupUI() {
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        observe()
    }

    private fun observe() {
        launchWhenStarted {
            viewModel.allTasks.collect { list ->
                adapterPagingTask.updateList(list)
            }
        }

        launchWhenStarted {
            viewModel.latestComment.collect { comment ->
                if (comment != null) {
                    showCommentNotification(comment)
                } else {
                    hideCommentNotification()
                }
            }
        }
    }

    private fun showCommentNotification(comment: Comment) {
        binding.tvCommentNotification.apply {
            visibility = View.VISIBLE
            text = "${comment.userName}: ${comment.content}\n${comment.createdAt}"
        }
    }

    private fun hideCommentNotification() {
        binding.tvCommentNotification?.visibility = View.GONE
    }

    override fun layoutId(): Int = R.layout.my_work_fragment_layout

    private fun setUpRecycleViewDay() {
        val spacing = resources.getDimensionPixelSize(com.wodox.core.R.dimen.dp_6)

        binding.rvDay.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            adapter = adapterPagingDay
            addSpaceDecoration(spacing, false)
        }

        val days = mutableListOf<DayItem>()
        for (i in 0 until 14) {
            val cal = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_YEAR, i)
            days.add(DayItem(cal.time, isSelected = i == 0))
        }
        adapterPagingDay.updateList(days)
    }

    private fun setUpRecycleView() {
        val spacing = resources.getDimensionPixelSize(com.wodox.core.R.dimen.dp_2)

        binding.rvTaskDay.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = adapterPagingTask
            addSpaceDecoration(spacing, false)
        }
    }

    private fun scrollToTaskForDate(date: Date) {
        val sortedTasks = adapterPagingTask.list
        val index = sortedTasks.indexOfFirst { task ->
            val startAt = task.startAt ?: return@indexOfFirst false
            val startDate = startAt.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            val targetDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            startDate == targetDate
        }
        if (index != -1) {
            binding.rvTaskDay.smoothScrollToPosition(index)
        }
    }

    companion object {
        fun newInstance() = MyWorkFragment()
    }
}