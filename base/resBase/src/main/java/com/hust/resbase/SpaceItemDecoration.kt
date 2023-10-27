package com.hust.resbase

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

open class SpaceItemDecoration(
    protected val leftRight: Int,
    protected val topBottom: Int
) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val layoutManager: LinearLayoutManager = parent.layoutManager as LinearLayoutManager
        if (layoutManager.orientation == LinearLayoutManager.VERTICAL) {
            //最后一项需要 bottom
            if (parent.getChildAdapterPosition(view) == layoutManager.itemCount - 1) {
                outRect.bottom = topBottom
            }
            if(parent.getChildAdapterPosition(view) == 4 || parent.getChildAdapterPosition(view) == 7) {
                outRect.top = topBottom + 10
            } else {
                outRect.top = topBottom
            }
            outRect.left = leftRight
            outRect.right = leftRight
        } else {
            //最后一项需要right
            if (parent.getChildAdapterPosition(view) == layoutManager.itemCount - 1) {
                outRect.right = leftRight
            }
            outRect.top = topBottom
            outRect.left = leftRight
            outRect.bottom = topBottom
        }
    }
}