package com.wodox.common.navigation
import androidx.recyclerview.widget.RecyclerView
import com.wodox.core.data.model.Selectable
import com.wodox.core.ui.adapter.TMVVMAdapter


fun <T: Selectable> TMVVMAdapter<T>.reloadChangedItems(predicate: (T) -> Boolean) {
    val previousIndex = list.indexOfFirst { it.isSelected }
    val currentIndex = list.indexOfFirst { predicate(it) }

    if (previousIndex != currentIndex) {
        for (item in list) {
            item.isSelected = predicate(item)
        }
        notifyItemChanged(previousIndex)
        notifyItemChanged(currentIndex)
    }
}

fun <T: Selectable> RecyclerView.reloadChangedItems(predicate: (T) -> Boolean) {
    (this.adapter as? TMVVMAdapter<T>)?.apply {
        reloadChangedItems(predicate)
    }
}