package com.wodox.docs.ui.docs

import android.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.wodox.common.navigation.DocNavigator
import com.wodox.core.base.fragment.BaseFragment
import com.wodox.core.extension.addSpaceDecoration
import com.wodox.core.extension.debounceClick
import com.wodox.core.extension.launchWhenStarted
import com.wodox.core.extension.show
import com.wodox.core.extension.toast
import com.wodox.docs.R
import com.wodox.docs.databinding.FragmentDocsBinding
import com.wodox.domain.docs.model.model.SharedDocument
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DocsFragment : BaseFragment<FragmentDocsBinding, DocsViewModel>(
    DocsViewModel::class
) {

    @Inject
    lateinit var docNavigator: DocNavigator

    private val documentAdapter by lazy {
        DocumentAdapter(
            context,
            object : DocumentAdapter.OnItemClickListener {
                override fun onItemClick(document: SharedDocument) {
                    val currentUserId = viewModel.uiState.value.currentUser?.id?.toString()
                    val isShared = currentUserId != null && document.ownerUserId != currentUserId

                    val permission = if (isShared) {
                        document.invitedUsers
                            .find { it.userId.toString() == currentUserId }
                            ?.permission?.name ?: "VIEW"
                    } else {
                        "EDIT"
                    }

                    docNavigator.openDocsDetail(
                        requireContext(),
                        documentId = document.documentId,
                    )
                }

                override fun onDeleteClick(document: SharedDocument) {
                    showDeleteConfirmation(document)
                }
            }
        )
    }

    override fun layoutId(): Int = R.layout.fragment_docs

    override fun initialize() {
        setupUI()
        setupRecyclerView()
        setupAction()
        observeState()
        observeEvents()
    }

    private fun setupUI() {
        binding.lifecycleOwner = viewLifecycleOwner
    }

    private fun setupRecyclerView() {
        val spacing = resources.getDimensionPixelSize(com.wodox.core.R.dimen.dp_4)

        binding.rvDocuments.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = documentAdapter
            addSpaceDecoration(spacing = spacing)
        }
    }

    private fun setupAction() {
        binding.fabAddDoc.debounceClick {
            docNavigator.openDocs(requireContext())
        }
    }

    private fun observeState() {
        launchWhenStarted {
            viewModel.uiState.collect { state ->
                binding.progressBar.show(state.isLoading)
                documentAdapter.setCurrentUserId(state.currentUser?.id?.toString())
                documentAdapter.submitList(state.documents)

                binding.llEmptyState.show(state.documents.isEmpty() && !state.isLoading)

                state.error?.let {
                    requireContext().toast(it)
                }
            }
        }
    }

    private fun observeEvents() {
        launchWhenStarted {
            viewModel.uiEvent.collect { event ->
                when (event) {
                    is DocsUiEvent.ShowError -> {
                        requireContext().toast(event.message)
                    }

                    is DocsUiEvent.DocumentDeleted -> {
                        requireContext().toast("Document deleted successfully")
                    }

                    is DocsUiEvent.NavigateToCreateDoc -> {
                        docNavigator.openDocs(requireContext())
                    }
                }
            }
        }
    }


    private fun showDeleteConfirmation(document: SharedDocument) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Document")
            .setMessage("Are you sure you want to delete \"${document.documentTitle}\"?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.dispatch(DocsUiAction.DeleteDocument(document.documentId))
            }
            .setNegativeButton("Cancel", null)
            .show()
    }


    override fun onResume() {
        super.onResume()
        viewModel.dispatch(DocsUiAction.LoadDocuments)
    }

    companion object {
        fun newInstance() = DocsFragment()
    }
}