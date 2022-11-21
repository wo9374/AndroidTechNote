package com.example.androidtechnote.recycler.custom_focus

import android.view.View
import com.example.customlibrary.FocusItem
import com.example.customlibrary.MoviesRepository
import com.example.focusablelistview.ListViewAdapter
import com.example.focusablelistview.ListViewHolder

class CustomFocusListAdapter(
    override var highLight: Boolean,
    override var radius: Float,
    override var itemClick: (FocusItem, View) -> Unit
) : ListViewAdapter<FocusItem>(DiffUtil()) {

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.setTitle(getItem(position).title)
        holder.setGlide(MoviesRepository.TMDB_POPULAR_MOVIE_IMG_W500 + getItem(position).poster_path)
        super.onBindViewHolder(holder, position)
    }
}

class DiffUtil: androidx.recyclerview.widget.DiffUtil.ItemCallback<FocusItem>(){
    override fun areItemsTheSame(oldItem: FocusItem, newItem: FocusItem): Boolean {
        return (oldItem.id == newItem.id)
    }

    override fun areContentsTheSame(oldItem: FocusItem, newItem: FocusItem): Boolean {
        return (oldItem.title == newItem.title)
    }
}

/*class FocusAdapter(data: List<FocusItem>) : FocusableAdapter() {
    var dataList = data

    override fun onBindViewHolder(holder: FocusableViewHolder, position: Int) {
        holder.setTitle(dataList[position].title)
        holder.setGlide(TMDB_POPULAR_MOVIE_IMG_W500 + dataList[position].poster_path)
        super.onBindViewHolder(holder, position)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}*/