package com.example.focusablelistview

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.focusablelistview.databinding.LayoutFocusableListviewBinding

class FocusableListView : ConstraintLayout {
    var focusBinding : LayoutFocusableListviewBinding = LayoutFocusableListviewBinding.inflate(LayoutInflater.from(context), this, true)
    var focusPosition = 0

    var itemHighLight = false
    var itemCornerDp = 0F

    val recyclerView : RecyclerView
        get() = focusBinding.recycler

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs){
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.FocusableListView)
        setTypedArray(typedArray, context)
    }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.FocusableListView, defStyleAttr, 0)
        setTypedArray(typedArray, context)
    }

    private fun setTypedArray(typedArray: TypedArray, context: Context){

        /**
         * 아이템 Space 데코레이션
         * */
        val itemDp = typedArray.getDimension(R.styleable.FocusableListView_itemDecoration, 0F)
        recyclerView.apply {
            addItemDecoration(FocusSpaceDeco(itemDp))
            addOnItemTouchListener(object : RecyclerView.SimpleOnItemTouchListener(){
                override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                    return true
                }
            })
        }

        /**
         * 리스트 방향 Horizontal = 0, Vertical = 1
         * */
        val orientation = typedArray.getInteger(R.styleable.FocusableListView_orientation, 0)


        /**
         * 중앙 포커스 여부
         * */
        val centerFocus = typedArray.getBoolean(R.styleable.FocusableListView_centerScrollPosition, false)
        if (centerFocus)
            recyclerView.layoutManager = FocusCenterScrollManager(context, orientation, false)
        else
            recyclerView.layoutManager = LinearLayoutManager(context, orientation, false)


        /**
         * 아이템 하이라이트, 라운드 크기
         * */
        itemHighLight = typedArray.getBoolean(R.styleable.FocusableListView_itemHighLight, false)
        itemCornerDp = typedArray.getDimension(R.styleable.FocusableListView_itemImageCornerRadius, 0F)

        typedArray.recycle()
    }

    fun itemFocus(){
        when(recyclerView.layoutManager){
            is FocusCenterScrollManager -> {
                (recyclerView.layoutManager as FocusCenterScrollManager).apply {
                    smoothScrollToPosition(recyclerView, null, focusPosition)
                    findViewByPosition(focusPosition)?.requestFocus(View.FOCUS_DOWN)
                }
            }
            is LinearLayoutManager -> {
                (recyclerView.layoutManager as LinearLayoutManager).findViewByPosition(focusPosition)?.requestFocus(View.FOCUS_DOWN)
            }
        }

    }

    fun setBgGlide(url : String){
        Glide.with(focusBinding.bgImg)
            .load(url)
            .transition(DrawableTransitionOptions.withCrossFade(500))
            .error(R.drawable.ic_launcher_background)
            .override(1400, 800)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .skipMemoryCache(true)
            .into(focusBinding.bgImg)
    }

    fun prevItemFocus() : Boolean{
        return if (focusPosition > 0){
            focusPosition -= 1
            itemFocus()
            true
        }else
            false
    }

    fun nextItemFocus() : Boolean{
        return if (focusPosition < (recyclerView.adapter?.itemCount?.minus(1) ?: 0)){
            focusPosition += 1
            itemFocus()
            true
        } else
            false
    }

    fun selectItem(){
        recyclerView.findViewHolderForAdapterPosition(focusPosition)
            ?.itemView
            ?.performClick()
    }
}