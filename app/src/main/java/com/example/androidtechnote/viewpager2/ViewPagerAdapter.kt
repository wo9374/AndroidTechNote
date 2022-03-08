package com.example.androidtechnote.viewpager2

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.androidtechnote.R
import com.example.androidtechnote.databinding.ItemViewpagerBinding


class ViewPagerAdapter(private val listData: ArrayList<DataPage>) : RecyclerView.Adapter<ViewHolderPage>() {
    lateinit var binding: ItemViewpagerBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderPage {
        binding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_viewpager, parent, false)
        return ViewHolderPage(binding)
    }

    override fun onBindViewHolder(holder: ViewHolderPage, position: Int) {
        val viewHolder: ViewHolderPage = holder
        val idx = position % listData.size
        viewHolder.onBind(listData[idx])
    }

    override fun getItemCount(): Int = Int.MAX_VALUE

}

class ViewHolderPage(val binding: ItemViewpagerBinding) : RecyclerView.ViewHolder(binding.root) {
    fun onBind(data: DataPage) {
        binding.tvTitle.text = data.title
        binding.rlLayout.setBackgroundResource(data.color)
    }
}