package com.example.focusablelistview

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class FocusSpaceDeco(private val leftSpace: Float) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {

        val position = parent.getChildAdapterPosition(view) //각 아이템뷰의 순서 (index)
        val totalItemCount = state.itemCount                //총 아이템 수
        val scrollPosition = state.targetScrollPosition     //스크롤 됬을때 아이템 position

        //첫번째 아이템이 아닐때
        if (position != 0)
            outRect.left = leftSpace.toInt()
    }
}