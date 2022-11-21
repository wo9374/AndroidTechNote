package com.example.androidtechnote.recycler.custom_focus

import android.view.ViewGroup
import com.example.customlibrary.FocusItem
import com.example.customlibrary.MoviesRepository
import com.example.focusablelistview.DetailAdapter
import com.example.focusablelistview.DetailViewHolder
import com.example.focusablelistview.OnImageReadyListener

class MyDetailAdapter(onImageReadyListener: OnImageReadyListener) : DetailAdapter<FocusItem>(DiffUtil(), onImageReadyListener){

    override fun onBindViewHolder(holder: DetailViewHolder, position: Int) {
        holder.setTitle(getItem(position).title)
        holder.setGlide(MoviesRepository.TMDB_POPULAR_MOVIE_IMG_ORIGINAL + getItem(position).backdrop_path)
        super.onBindViewHolder(holder, position)
    }
}
