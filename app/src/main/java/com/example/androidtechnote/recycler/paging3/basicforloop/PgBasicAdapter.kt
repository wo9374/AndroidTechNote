package com.example.androidtechnote.recycler.paging3.basicforloop

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.androidtechnote.databinding.ItemSampleBinding


class PgBasicAdapter : PagingDataAdapter<String, PagingViewHolder>(diffCallback) {

    lateinit var mClickListener: ItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagingViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        return PagingViewHolder(
            ItemSampleBinding.inflate(layoutInflater, parent, false),
            mClickListener
        )
    }

    override fun onBindViewHolder(
        holder: PagingViewHolder,
        position: Int
    ) { // PagingViewHolder, NetworkViewHolder
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }
        }
    }

    interface ItemClickListener {
        fun onClick(v: View, position: Int)
    }

    fun setOnItemClick(listener: ItemClickListener) {
        mClickListener = listener
    }
}

class PagingViewHolder(private val binding: ItemSampleBinding, private val listener: PgBasicAdapter.ItemClickListener) : RecyclerView.ViewHolder(binding.root) {

    fun bind(value: String) {
        val pos = bindingAdapterPosition
        if (pos != RecyclerView.NO_POSITION) {
            binding.textView.text = value

            binding.root.setOnClickListener {
                listener.onClick(it, pos)
            }
        }

        binding.root.apply {
            isFocusable = true
            isFocusableInTouchMode = true
            onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    ViewCompat
                        .animate(v)
                        .scaleX(1.12f)
                        .scaleY(1.12f)
                        .setDuration(300)
                        .translationZ(1f).start()
                } else {
                    ViewCompat
                        .animate(v)
                        .scaleX(1.0f)
                        .scaleY(1.0f)
                        .setDuration(100)
                        .translationZ(0f)
                        .start()
                }
            }
        }

    }
}
