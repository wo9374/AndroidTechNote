package com.example.androidtechnote.customview.custom

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Rect
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.androidtechnote.R
import com.example.androidtechnote.databinding.LayoutFocusRecyclerConstraintBinding

class FocusableListView : ConstraintLayout {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs){
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.FocusableListView)
        setTypedArray(typedArray)
    }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.FocusableListView, defStyleAttr, 0)
        setTypedArray(typedArray)
    }

    var focusBinding : LayoutFocusRecyclerConstraintBinding = LayoutFocusRecyclerConstraintBinding.inflate(LayoutInflater.from(context), this, true)
    var focusPosition = 0

    private fun setTypedArray(typedArray: TypedArray) {
        val itemDp = typedArray.getDimension(R.styleable.FocusableListView_itemDecoration, 0F)
        focusBinding.recycler.addItemDecoration(FocusSpaceDeco(itemDp))

        typedArray.recycle()
    }
    /**
     * Programmatically
     */
    fun setBgRes(bgResId: Int){
        focusBinding.bgImg.setImageResource(bgResId)
    }

    fun setBgGlide(url : String){
        Glide.with(focusBinding.bgImg)
            .load(url)
            .transition(DrawableTransitionOptions.withCrossFade(600))
            .error(R.drawable.ic_launcher_background)
            .override(1400, 800)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .skipMemoryCache(true)
            .into(focusBinding.bgImg)
        /*.listener(object : RequestListener<Drawable>{
            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                return false
            }
            override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                if (resource is BitmapDrawable) {
                    val bitmap = resource.bitmap
                    Log.d("Glide", String.format("bitmap %,d btyes, size: %d x %d",
                        bitmap.byteCount,		// 리사이징된 이미지 바이트
                        bitmap.width,			// 이미지 넓이
                        bitmap.height			// 이미지 높이
                    )
                    )
                }
                return false
            }
        })*/
        //.onlyRetrieveFromCache(true) //캐시에 저장된 이미지가 있을때만 불러오기
    }

    fun setHasFixedSize(boolean: Boolean = false){
        focusBinding.recycler.setHasFixedSize(boolean)
    }

    fun itemFocus(){
        (focusBinding.recycler.layoutManager as LinearLayoutManager).findViewByPosition(focusPosition)
            ?.requestFocus(View.FOCUS_DOWN)
    }

    fun prevItemFocus(){
        if (focusPosition > 0){
            focusPosition -= 1
            itemFocus()
        }
    }

    fun nextItemFocus(){
        if (focusPosition < (focusBinding.recycler.adapter?.itemCount?.minus(1) ?: 0)){
            focusPosition += 1
            itemFocus()
        }
    }

    fun selectItem(){
        focusBinding.recycler.findViewHolderForAdapterPosition(focusPosition)
            ?.itemView
            ?.performClick()
    }
}

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