package com.example.focusablelistview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.focusablelistview.databinding.ItemDetailBinding

abstract class DetailAdapter<T>(
    diffCallback: DiffUtil.ItemCallback<T>,
    private val onImageReadyListener: OnImageReadyListener) : ListAdapter<T, DetailViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailViewHolder {
        val binding = ItemDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.root.apply {
            isFocusable = true
            isFocusableInTouchMode = true
        }
        return DetailViewHolder(binding, onImageReadyListener)
    }

    override fun onBindViewHolder(holder: DetailViewHolder, position: Int) {

    }
}