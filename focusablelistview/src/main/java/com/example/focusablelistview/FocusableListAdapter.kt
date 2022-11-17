package com.example.focusablelistview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.focusablelistview.databinding.ItemFocusableBinding

open class FocusableListAdapter<T>(
    diffCallback: DiffUtil.ItemCallback<T>,
    itemHighLight: Boolean = false,
    cornerRadius: Float = 0F
) : ListAdapter<T, FocusableViewHolder>(diffCallback) {

    var prevPosition = 0
    var itemHighLight = false
    var cornerRadius = 0F

    init {
        this.itemHighLight = itemHighLight
        this.cornerRadius = cornerRadius
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FocusableViewHolder {
        val binding = ItemFocusableBinding.inflate(LayoutInflater.from(parent.context),parent, false)
        binding.root.apply {
            isFocusable = true
            isFocusableInTouchMode = true
        }
        binding.apply {
            thumbnailImg.cornerRadius = cornerRadius
            bgHighlight.cornerRadius = cornerRadius
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

                        if (itemHighLight)
                            binding.bgHighlight.visibility = View.VISIBLE
                    }else{
                        reduceAnim()
                        if (itemHighLight)
                            binding.bgHighlight.visibility = View.INVISIBLE
                    }

                    prevPosition = adapterPosition
                }
            }
        }
    }
}

open class FocusableAdapter : RecyclerView.Adapter<FocusableViewHolder>() {
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
}