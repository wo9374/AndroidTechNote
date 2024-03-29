package com.example.focusablelistview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.focusablelistview.databinding.ItemFocusableBinding

abstract class ListViewAdapter<T>(diffCallback: DiffUtil.ItemCallback<T>) : ListAdapter<T, ListViewHolder>(diffCallback) {

    abstract var highLight : Boolean
    abstract var radius : Float
    abstract var itemClick: (T, View, Int) -> Unit

    private var prevPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemFocusableBinding.inflate(LayoutInflater.from(parent.context),parent, false)
        binding.root.apply {
            isFocusable = true
            isFocusableInTouchMode = true
        }
        binding.apply {
            thumbnailImg.cornerRadius = radius
            bgHighlight.cornerRadius = radius
        }
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.apply {
            if (adapterPosition != RecyclerView.NO_POSITION){

                setBgHighLight(highLight)

                binding.root.onFocusChangeListener = View.OnFocusChangeListener { itemView, hasFocus ->
                    if (hasFocus){
                        if (prevPosition < adapterPosition)
                            nextItemAnim()
                        else if (prevPosition > adapterPosition)
                            prevItemAnim()
                        else
                            expansionAnim()

                        if (highLight) appearHighLight()
                    }else{
                        reduceAnim()
                        if (highLight) disappearHighLight()
                    }

                    prevPosition = adapterPosition
                }

                binding.root.setOnClickListener {
                    itemClick(getItem(adapterPosition), it, adapterPosition)
                }
            }
        }
    }
}

/*
abstract class FocusableAdapter : RecyclerView.Adapter<FocusableViewHolder>() {
    var prevPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FocusableViewHolder {
        val binding = ItemFocusableBinding.inflate(LayoutInflater.from(parent.context),parent, false)
        binding.root.apply {
            isFocusable = true
            isFocusableInTouchMode = true
        }
        return FocusableViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FocusableViewHolder, position: Int) {
        holder.apply {
            if (adapterPosition != RecyclerView.NO_POSITION){
                binding.root.onFocusChangeListener = View.OnFocusChangeListener { itemView, hasFocus ->
                    if (hasFocus){
                        if (prevPosition < adapterPosition)
                            nextItemAnim()
                        else if (prevPosition > adapterPosition)
                            prevItemAnim()
                        else
                            expansionAnim()
                    }else{
                        reduceAnim()
                    }

                    prevPosition = adapterPosition
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return 0
    }
}*/
