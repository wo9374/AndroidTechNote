package com.example.androidtechnote.recycler.custom_focus

import com.example.androidtechnote.recycler.custom_focus.data.FocusItem
import com.example.androidtechnote.recycler.custom_focus.data.MoviesRepository
import com.example.focusablelistview.DetailAdapter
import com.example.focusablelistview.DetailViewHolder

class MyDetailAdapter() : DetailAdapter<FocusItem>(DiffUtil()){

    override fun onBindViewHolder(holder: DetailViewHolder, position: Int) {
        holder.setTitle(getItem(position).title)
        holder.setGlide(MoviesRepository.TMDB_POPULAR_MOVIE_IMG_ORIGINAL + getItem(position).backdrop_path)
        super.onBindViewHolder(holder, position)
    }
}
