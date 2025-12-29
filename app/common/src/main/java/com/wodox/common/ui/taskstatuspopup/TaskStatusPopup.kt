package com.wodox.common.ui.taskstatuspopup



import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.WindowManager
import android.widget.PopupWindow
import com.wodox.core.extension.AbstractView
import com.wodox.core.extension.debounceClick
import com.wodox.domain.home.model.local.TaskStatus
import com.wodox.common.R
import com.wodox.common.databinding.ItemTaskPopupBinding

class TaskStatusPopup(context: Context, attrs: AttributeSet?) : AbstractView(context, attrs) {
    interface OnItemClickListener {
        fun onSelect(status: TaskStatus)
    }

    var listener: OnItemClickListener? = null

    override fun layoutId() = R.layout.item_task_popup
    override fun viewBinding() = binding as ItemTaskPopupBinding

    override fun viewInitialized() {
        viewBinding().apply {
            siTodo.debounceClick { listener?.onSelect(TaskStatus.TODO) }
            siInProgress.debounceClick { listener?.onSelect(TaskStatus.IN_PROGRESS) }
            siComplete.debounceClick { listener?.onSelect(TaskStatus.DONE) }
        }
    }

    companion object {
        fun show(context: Context, anchor: View, callback: (TaskStatus) -> Unit) {
            val popupView = TaskStatusPopup(context, null)
            val popup = PopupWindow(
                popupView,
                anchor.width,
                WindowManager.LayoutParams.WRAP_CONTENT,
                true
            )

            popupView.listener = object : OnItemClickListener {
                override fun onSelect(status: TaskStatus) {
                    callback(status)
                    popup.dismiss()
                }
            }

            popup.showAsDropDown(anchor)
        }
    }
}
