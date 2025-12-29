package com.wodox.ui.task.taskdetail.attachment

import android.os.Bundle
import android.view.ViewGroup
import com.wodox.core.base.fragment.BaseDialogFragment
import com.wodox.core.extension.debounceClick
import com.wodox.core.extension.dialogWidth
import com.wodox.core.extension.getDialogWidth
import com.wodox.home.R
import com.wodox.home.databinding.FragmentAttachmentLayoutBinding
import com.wodox.domain.home.model.local.AttachmentType
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID
import com.wodox.model.Constants

@AndroidEntryPoint
class AttachmentDialogFragment :
    BaseDialogFragment<FragmentAttachmentLayoutBinding, AttachmentViewModel>(AttachmentViewModel::class) {
    private var selectedType: AttachmentType? = null
    var onAttachmentSelected: ((AttachmentType) -> Unit)? = null

    override fun layoutId(): Int = R.layout.fragment_attachment_layout

    override fun initialize() {
        setSize(requireContext().getDialogWidth(), ViewGroup.LayoutParams.WRAP_CONTENT)
        setupAction()
        setupUI()
    }


    private fun setupUI() {

    }

    private fun setupAction() {
        binding.apply {
            llImage.debounceClick {
                selectAttachmentType(AttachmentType.IMAGE)
            }
            llVideo.debounceClick {
                selectAttachmentType(AttachmentType.VIDEO)
            }
            llFile.debounceClick {
                selectAttachmentType(AttachmentType.FILE)
            }
            llAudio.debounceClick {
                selectAttachmentType(AttachmentType.AUDIO)
            }

            btnDone.debounceClick {
                selectedType?.let { type ->
                    onAttachmentSelected?.invoke(type)
                    dismiss()
                }
            }
            ivClose.debounceClick {
                dismissAllowingStateLoss()
            }
        }
    }

    private fun selectAttachmentType(type: AttachmentType) {
        selectedType = type
        highlightSelection(type)
        onAttachmentSelected?.invoke(type)
        dismiss()
    }

    private fun highlightSelection(type: AttachmentType) {
        binding.apply {
            llImage.alpha = if (type == AttachmentType.IMAGE) 1f else 0.5f
            llVideo.alpha = if (type == AttachmentType.VIDEO) 1f else 0.5f
            llFile.alpha = if (type == AttachmentType.FILE) 1f else 0.5f
            llAudio.alpha = if (type == AttachmentType.AUDIO) 1f else 0.5f
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(category: UUID?) = AttachmentDialogFragment().apply {
            arguments = Bundle().apply {
                putSerializable(Constants.Intents.TASK_ID, category)
            }
        }
    }
}