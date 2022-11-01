package com.example.androidtechnote.recycler.focus

import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs
import kotlin.math.min

class CenterFocusedZoomManager(context: Context, orientation: Int, reverseLayout: Boolean? = false): LinearLayoutManager(context, orientation, reverseLayout!!) {
    private val amount = 0.3f
    private val distance = 0.9f

    override fun onLayoutCompleted(state: RecyclerView.State?) {
        super.onLayoutCompleted(state)

        if (orientation == HORIZONTAL)
            horizontalScaling()
        else
            verticalScaling()
    }

    override fun scrollHorizontallyBy(dx: Int, recycler: RecyclerView.Recycler?, state: RecyclerView.State?) =  if(orientation == HORIZONTAL) {
        super.scrollHorizontallyBy(dx, recycler, state).also { horizontalScaling() }
    } else {
        0
    }

    override fun scrollVerticallyBy(dy: Int, recycler: RecyclerView.Recycler?, state: RecyclerView.State?) = if (orientation == VERTICAL) {
        super.scrollVerticallyBy(dy, recycler, state).also { verticalScaling() }
    } else {
        0
    }

    private fun horizontalScaling() {
        val midPoint = width / 2f
        val d0 = 0.0f
        val d1 = distance * midPoint
        val s0 = 1.0f
        val s1 = 1.0f - amount

        for(i in 0 until childCount) {
            val child = getChildAt(i)
            val childMidPoint = (getDecoratedRight(child!!) + getDecoratedLeft(child)) / 2f
            val d = min(d1, abs(midPoint - childMidPoint))
            val scale = s0 + (s1 - s0) * (d - d0) / (d1 - d0)
            Log.e("scale value", "$scale")
            child.isEnabled = scale > (0.9f - amount)
            child.scaleX = scale
            child.scaleY = scale
        }
    }

    private fun verticalScaling() {
        val midPoint = height / 2f
        val d0 = 0.0f
        val d1 = distance * midPoint
        val s0 = 1.0f
        val s1 = 1.0f - amount

        for(i in 0 until childCount) {
            val child = getChildAt(i)
            val childMidPoint = (getDecoratedBottom(child!!) + getDecoratedTop(child)) / 2f
            val d = min(d1, abs(midPoint - childMidPoint))
            val scale = s0 + (s1 - s0) * (d - d0) / (d1 - d0)
            Log.e("scale value", "$scale")
            child.isEnabled = scale > (0.9f - amount)
            child.scaleX = scale
            child.scaleY = scale
        }
    }
}