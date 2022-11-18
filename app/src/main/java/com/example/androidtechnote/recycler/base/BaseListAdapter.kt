package com.example.androidtechnote.recycler.base

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.viewbinding.ViewBinding

/**
 * https://blog.devgenius.io/android-generic-recyclerview-adapter-67eb8f826cad
 * */

class BaseListAdapter<T>(diffCallback: DiffUtil.ItemCallback<T>)
    : ListAdapter<T, BaseViewHolder<T>>(diffCallback){

    var expressionOnCreateViewHolder:((ViewGroup)-> ViewBinding)? = null
    var expressionViewHolderBinding: ((T,ViewBinding) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<T> {
        return expressionOnCreateViewHolder?.let {
            it(parent)
        }?.let {
            BaseViewHolder(it, expressionViewHolderBinding!!)
        }!!
    }

    override fun onBindViewHolder(holder: BaseViewHolder<T>, position: Int) {
        holder.bind(currentList[position])
    }
}