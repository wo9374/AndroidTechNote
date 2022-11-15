package com.example.androidtechnote.customview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.androidtechnote.R
import com.example.androidtechnote.databinding.ItemDetailMovieBinding

class DetailMovieAdapter : ListAdapter<PopularMovie, DetailMovieAdapter.DetailMovieHolder>(FocusDiffUtil()){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailMovieHolder {
        val binding = ItemDetailMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DetailMovieHolder(binding)
    }

    override fun onBindViewHolder(holder: DetailMovieHolder, position: Int) {
        holder.bind(currentList[position])
    }

    inner class DetailMovieHolder(val binding : ItemDetailMovieBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(data: PopularMovie){
            Glide
                .with(binding.bgMovie)
                .load(MoviesRepository.TMDB_POPULAR_MOVIE_IMG_ORIGINAL + data.backdrop_path)
                .error(R.drawable.ic_baseline_android_24)
                .override(1400,800)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.bgMovie)
        }
    }
}