package com.wodox.core.extension


import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView


class SpacesItemDecoration(
    private val spacing: Int,
    private val includeEdge: Boolean = false
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
            outRect.left = spacing
            outRect.right = spacing
            outRect.top = spacing
            outRect.bottom = spacing
        } else {
            if (position == 0) {
                outRect.left = spacing
            }
            if (position == itemCount - 1) {
                outRect.right = spacing
            }
            outRect.right = spacing / 2
            outRect.left = spacing / 2
        }
    }
}