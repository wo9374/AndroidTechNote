package com.example.androidtechnote.recycler.paging3.contentresolver

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.androidtechnote.databinding.ItemPgcrBinding

class PgCrAdapter : PagingDataAdapter<PhotoItem, PgCrAdapter.PageKeyViewHolder>(diffCallback) {

    private var mListener: OnItemLongClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageKeyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return PageKeyViewHolder(ItemPgcrBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: PageKeyViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }

    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<PhotoItem>() {
            override fun areItemsTheSame(oldItem: PhotoItem, newItem: PhotoItem): Boolean {
                return oldItem.index == newItem.index
            }
            override fun areContentsTheSame(oldItem: PhotoItem, newItem: PhotoItem): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class PageKeyViewHolder(private val binding: ItemPgcrBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PhotoItem) {
            binding.item = item
            binding.root.setOnLongClickListener {
                PgCrActivity.checkBoxOnOff = !PgCrActivity.checkBoxOnOff

                for (i in snapshot().items.indices){
                    snapshot().items[i].apply {
                        onOffCheck = PgCrActivity.checkBoxOnOff     //멀티 셀렉트 onOff
                        checkBoolean = false                        //체크 모두 해제
                    }
                    notifyItemChanged(i)
                }
                snapshot().items[bindingAdapterPosition].checkBoolean = true  //처음 롱클릭 한 아이템 하나만 체크
                notifyItemChanged(absoluteAdapterPosition)
                true
            }

            binding.root.setOnClickListener {
                if (PgCrActivity.checkBoxOnOff){
                    val pos = bindingAdapterPosition
                    if (pos != RecyclerView.NO_POSITION){
                        snapshot().items[pos].checkBoolean = !snapshot().items[pos].checkBoolean
                        notifyItemChanged(pos)
                    }
                }
            }
        }
    }

    interface OnItemLongClickListener{
        fun onItemClick(v: View, pos: Int)
    }

    fun setOnItemLongClickListener(listener: OnItemLongClickListener?) {
        this.mListener = listener
    }
}

@BindingAdapter("setImg")
fun setImg(view: ImageView, imgUri: String){
    Glide.with(view.context).load(imgUri).into(view)
}

@BindingAdapter("onOffMulti")
fun onOffMulti(view: ImageView, boolean: Boolean){
    if (boolean) view.visibility = View.VISIBLE
    else view.visibility = View.GONE
}

@BindingAdapter("setCheck")
fun setCheck(view: ImageView, boolean: Boolean){
    view.isSelected = boolean
}