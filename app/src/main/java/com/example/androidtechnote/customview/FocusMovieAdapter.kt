package com.example.androidtechnote.customview

import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.androidtechnote.R
import com.example.androidtechnote.customview.MoviesRepository.Companion.TMDB_POPULAR_MOVIE_IMG_W500
import com.example.androidtechnote.databinding.ItemFocusMovieBinding

class FocusMovieAdapter() : ListAdapter<PopularMovie, FocusMovieAdapter.FocusMovieHolder>(FocusDiffUtil()) {

    private var prevPosition = 0 //좌측 이동인지 우측 이동인지 판단용

    lateinit var mListener : OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FocusMovieHolder {
        val focusMovieBinding = ItemFocusMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        focusMovieBinding.root.apply {
            isFocusable = true
            isFocusableInTouchMode = true
        }
        return FocusMovieHolder(focusMovieBinding)
    }

    override fun onBindViewHolder(holder: FocusMovieHolder, position: Int) {
        holder.bind(currentList[position])
    }

    interface OnItemClickListener{
        fun onItemClick(v: View, position: Int, data: PopularMovie)
    }

    fun setOnItemClick(listener: OnItemClickListener){
        mListener = listener
    }

    inner class FocusMovieHolder(private val binding: ItemFocusMovieBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: PopularMovie) {
            binding.apply {

                //무비 포스터 set
                Glide
                    .with(thumbnailImg)
                    .load(TMDB_POPULAR_MOVIE_IMG_W500 + data.poster_path)
                    .thumbnail(0.1f)
                    .error(R.drawable.ic_baseline_android_24)
                    .into(thumbnailImg)

                //무비 타이틀 set
                title.text = data.title

                //포커스 리스너 set
                root.onFocusChangeListener = View.OnFocusChangeListener { itemView, hasFocus ->
                    if (hasFocus) {

                        val anim = ViewCompat
                            .animate(itemView)
                            .scaleX(1.14f)
                            .scaleY(1.14f)
                            .setDuration(150)
                            .translationZ(1f)

                        if(prevPosition < bindingAdapterPosition){ //우측 이동
                            anim.apply {
                                translationX(14f)
                                withEndAction {
                                    translationX(0f).setDuration(350).setStartDelay(100).start()
                                }
                            }.start()
                        } else if (prevPosition > bindingAdapterPosition){ //좌측 이동
                            anim.apply {
                                translationX(-14f)
                                withEndAction {
                                    translationX(0f).setDuration(350).setStartDelay(100).start()
                                }
                            }.start()
                        } else { // 0 Initial
                            anim.start()
                        }

                        //타이틀 alpha 지정
                        title.animate().alpha(1.0f)

                    } else {
                        itemView.animate().scaleX(1.0f).scaleY(1.0f).setDuration(350).setStartDelay(300).translationZ(0f).start()

                        //타이틀 alpha 및 시간 지정
                        title.animate().alpha(0.0f).duration = 300
                    }
                    prevPosition = bindingAdapterPosition
                }

                root.setOnClickListener {
                    mListener.onItemClick(it, bindingAdapterPosition, data)
                }
            }
        }
    }
}



class FocusDiffUtil : DiffUtil.ItemCallback<PopularMovie>() {
    override fun areItemsTheSame(oldItem: PopularMovie, newItem: PopularMovie): Boolean {
        return (oldItem.id == newItem.id) //and (oldItem.title == newItem.title)
    }

    override fun areContentsTheSame(oldItem: PopularMovie, newItem: PopularMovie): Boolean {
        return (oldItem.title == newItem.title) or (oldItem.backdrop_path == newItem.backdrop_path) or (oldItem.poster_path == newItem.poster_path)
    }
}

class SimpleSpaceDeco(private val leftSpace: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {

        val position = parent.getChildAdapterPosition(view) //각 아이템뷰의 순서 (index)
        val totalItemCount = state.itemCount                //총 아이템 수
        val scrollPosition = state.targetScrollPosition     //스크롤 됬을때 아이템 position

        //첫번째 아이템이 아닐때
        if (position != 0)
            outRect.left = leftSpace
    }
}