package com.example.androidtechnote.recycler.paging3.retrofit

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.androidtechnote.databinding.ItemPagingRetrofitBinding

class PgRetrofitAdapter : PagingDataAdapter<ResponseParameter.Photo, PageKeyViewHolder>(diffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageKeyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return PageKeyViewHolder(ItemPagingRetrofitBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: PageKeyViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<ResponseParameter.Photo>() {
            override fun areItemsTheSame(oldItem: ResponseParameter.Photo, newItem: ResponseParameter.Photo): Boolean {
                return oldItem.id == newItem.id
            }
            override fun areContentsTheSame(oldItem: ResponseParameter.Photo, newItem: ResponseParameter.Photo): Boolean {
                return oldItem == newItem
            }
        }
    }
}

class PageKeyViewHolder(private val binding: ItemPagingRetrofitBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: ResponseParameter.Photo) {
        binding.item = item
    }
}

@BindingAdapter("setImg")
fun setImg(view: ImageView, imgUri : String){
    Glide.with(view.context)
        .load(imgUri)
        .into(view)
}