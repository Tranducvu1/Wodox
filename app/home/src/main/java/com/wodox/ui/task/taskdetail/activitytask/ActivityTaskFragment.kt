package com.wodox.ui.task.taskdetail.activitytask

import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wodox.core.base.fragment.BaseFragment
import com.wodox.core.extension.addSpaceDecoration
import com.wodox.domain.home.model.local.Comment
import com.wodox.domain.home.model.local.Log
import com.wodox.domain.home.model.local.Task
import com.wodox.home.R
import com.wodox.home.databinding.ActivityTaskFragmentBinding
import com.wodox.model.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ActivityTaskFragment :
    BaseFragment<ActivityTaskFragmentBinding, ActivityTaskViewModel>(ActivityTaskViewModel::class) {

    override fun layoutId(): Int = R.layout.activity_task_fragment

    private val logAdapter by lazy {
        LogAdapter(
            context,
            object : LogAdapter.OnItemClickListener {
                override fun onLogClick(log: Log) {
                    Toast.makeText(
                        requireContext(),
                        "Log: ${log.title}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
    }

    private val commentAdapter by lazy {
        CommentAdapter(
            context,
            object : CommentAdapter.OnCommentActionListener {
                override fun onCommentDelete(comment: Comment) {
                    // Show confirmation dialog
                    showDeleteConfirmation(comment)
                }

                override fun onCommentEdit(comment: Comment) {
                    viewModel.dispatch(ActivityTaskUiAction.StartEditComment(comment))
                }

                override fun onCommentLike(comment: Comment) {
                    Toast.makeText(
                        requireContext(),
                        "Liked: ${comment.userName}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onCommentReply(comment: Comment) {
                    binding.bottomCommentBar.apply {
                        requestCommentFocus()
                        setCommentText("@${comment.userName}: ")
                    }
                }

                override fun onMoreOptions(comment: Comment) {
                    showCommentOptions(comment)
                }
            }
        )
    }

    override fun initialize() {
        setupUI()
        setupLogRecyclerView()
        setupCommentRecyclerView()
        setupCommentBar()
        observeEvents()
        observeComments()
        observeEditMode()
    }

    private fun setupUI() {
        binding.lifecycleOwner = this@ActivityTaskFragment
        binding.viewModel = viewModel
        viewModel.dispatch(ActivityTaskUiAction.LoadActivity)
    }

    private fun setupLogRecyclerView() {
        val spacing = resources.getDimensionPixelSize(com.wodox.core.R.dimen.dp_6)

        binding.rvTask.apply {
            layoutManager = LinearLayoutManager(
                context,
                RecyclerView.VERTICAL,
                false
            )
            adapter = logAdapter
            addSpaceDecoration(spacing, false)
        }
    }

    private fun setupCommentRecyclerView() {
        val spacing = resources.getDimensionPixelSize(com.wodox.core.R.dimen.dp_2)

        binding.rvComments.apply {
            layoutManager = LinearLayoutManager(
                context,
                RecyclerView.VERTICAL,
                false
            )
            adapter = commentAdapter
            addSpaceDecoration(spacing, false)
        }
    }

    private fun setupCommentBar() {
        binding.bottomCommentBar.apply {
            setOnSendListener { commentContent ->
                val editingCommentId = viewModel.uiState.value.editingCommentId
                if (editingCommentId != null) {
                    viewModel.dispatch(ActivityTaskUiAction.UpdateComment(editingCommentId, commentContent))
                } else {
                    viewModel.dispatch(ActivityTaskUiAction.SendComment(commentContent))
                }
            }

            setOnCancelListener {
                viewModel.dispatch(ActivityTaskUiAction.CancelEditComment)
            }
        }
    }

    private fun observeEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiEvent.collect { event ->
                when (event) {
                    is ActivityTaskUiEvent.ShowError -> {
                        Toast.makeText(
                            requireContext(),
                            event.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    ActivityTaskUiEvent.CommentSentSuccess -> {
                        Toast.makeText(
                            requireContext(),
                            "Comment sent successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.bottomCommentBar.clearComment()
                    }
                    ActivityTaskUiEvent.CommentUpdateSuccess -> {
                        Toast.makeText(
                            requireContext(),
                            "Comment updated successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.bottomCommentBar.clearComment()
                    }
                    ActivityTaskUiEvent.CommentDeleteSuccess -> {
                        Toast.makeText(
                            requireContext(),
                            "Comment deleted",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    is ActivityTaskUiEvent.StartEditMode -> {
                        binding.bottomCommentBar.apply {
                            setEditMode(true)
                            setCommentText(event.comment.content)
                            requestCommentFocus()
                        }
                    }
                    ActivityTaskUiEvent.CancelEditMode -> {
                        binding.bottomCommentBar.apply {
                            setEditMode(false)
                            clearComment()
                        }
                    }
                }
            }
        }
    }

    private fun observeComments() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                commentAdapter.submitList(state.listComments)
                commentAdapter.setEditingCommentId(state.editingCommentId)

                val hasComments = state.listComments.isNotEmpty()
                binding.tvCommentsTitle.visibility =
                    if (hasComments) android.view.View.VISIBLE else android.view.View.GONE
            }
        }
    }

    private fun observeEditMode() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                state.editingCommentId?.let { commentId ->
                    val position = state.listComments.indexOfFirst { it.id == commentId }
                    if (position != -1) {
                        binding.rvComments.smoothScrollToPosition(position)
                    }
                }
            }
        }
    }

    private fun showDeleteConfirmation(comment: Comment) {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Delete Comment")
            .setMessage("Are you sure you want to delete this comment?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.dispatch(ActivityTaskUiAction.DeleteComment(comment.id))
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showCommentOptions(comment: Comment) {
        val options = arrayOf("Edit", "Delete", "Report")
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Comment Options")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> viewModel.dispatch(ActivityTaskUiAction.StartEditComment(comment))
                    1 -> showDeleteConfirmation(comment)
                    2 -> Toast.makeText(requireContext(), "Report feature coming soon", Toast.LENGTH_SHORT).show()
                }
            }
            .show()
    }

    companion object {
        fun newInstance(task: Task?) = ActivityTaskFragment().apply {
            arguments = Bundle().apply {
                putParcelable(Constants.Intents.TASK, task)
            }
        }
    }
}