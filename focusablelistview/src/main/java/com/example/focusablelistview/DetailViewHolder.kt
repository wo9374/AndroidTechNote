package com.example.focusablelistview

import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.focusablelistview.databinding.ItemDetailBinding

class DetailViewHolder(val binding: ItemDetailBinding, val onImageReadyListener: OnImageReadyListener) : RecyclerView.ViewHolder(binding.root){

    fun setTitle(titleText:String){
        binding.title.text = titleText
    }

    fun setGlide(glideUrl: String){
        Glide
            .with(binding.image)
            .load(glideUrl)
            .override(1400,800)
            .skipMemoryCache(true)
            .error(R.drawable.ic_launcher_foreground)
            .listener(object : RequestListener<Drawable>{
                override fun onLoadFailed(
                    e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean
                ): Boolean {
                    onImageReadyListener.onImageReady(adapterPosition)
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean
                ): Boolean {
                    onImageReadyListener.onImageReady(adapterPosition)
                    return false
                }
            })
            .into(binding.image)

        binding.executePendingBindings()
    }
}