package com.wodox.core.extension

import android.graphics.Rect
import androidx.recyclerview.widget.RecyclerView
import android.view.View


fun RecyclerView.addSpaceDecoration(spacing: Int, includeEdge: Boolean = true) {
    this.addItemDecoration(
        SpaceItemDecoration(spacing, includeEdge)
    )
}

class SpaceItemDecoration(
    private val spacing: Int,
    private val includeEdge: Boolean = true
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val itemCount = state.itemCount

        if (includeEdge) {
            if (position == 0) {
                outRect.top = spacing
            }
            if (position == itemCount - 1) {
                outRect.bottom = spacing
            }
            outRect.left = spacing
            outRect.right = spacing
            outRect.bottom = spacing
        } else {
            outRect.left = spacing / 2
            outRect.right = spacing / 2
            outRect.bottom = spacing
        }
    }
}