package com.example.androidtechnote.room.view

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.androidtechnote.R
import com.example.androidtechnote.databinding.ItemTestBinding
import com.example.androidtechnote.room.model.Test

class TestAdapter(val todoItemClick: (Test) -> Unit, val todoItemLongClick: (Test) -> Unit): RecyclerView.Adapter<TestAdapter.ViewHolder>()  {

    private var testList:List<Test> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<ItemTestBinding>(LayoutInflater.from(parent.context), R.layout.item_test, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(testList[position])
    }

    override fun getItemCount(): Int {
        return testList.size
    }

    inner class ViewHolder(val binding: ItemTestBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(test: Test) {
            binding.test = test

            binding.root.setOnClickListener {
                todoItemClick(test)
            }
            binding.root.setOnLongClickListener {
                todoItemLongClick(test)
                true
            }
        }
    }

    fun setTestItemList(test:List<Test>){
        this.testList = test
        notifyDataSetChanged()
    }
}


object BindingAdapter {
    @BindingAdapter("imageUrl")
    @JvmStatic
    fun loadImage(imageView: ImageView, url: String){
        Glide.with(imageView.context).load(url).error(R.drawable.ic_launcher_background).into(imageView)
    }

    @BindingAdapter("listData")
    @JvmStatic
    fun bindData(recyclerView: RecyclerView, test: List<Test>?){
        val adapter = recyclerView.adapter as TestAdapter
        if (test != null) {
            adapter.setTestItemList(test)
        }
    }
}