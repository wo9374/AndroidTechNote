package com.example.androidtechnote.coordinator.bottomsheet

import android.content.Context
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.androidtechnote.databinding.LayoutItemBottomSheetBinding

data class TodoData(val title : String)

class BottomSheetTodoAdapter(val context: Context, var list: List<TodoData>) : RecyclerView.Adapter<BottomSheetTodoAdapter.TodoHolder>() {

    lateinit var mListener: OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoHolder {
        return TodoHolder(
            LayoutItemBottomSheetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: TodoHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface OnItemClickListener{
        fun onItemClick(v: View, pos: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener){
        mListener = listener
    }

    inner class TodoHolder(val binding: LayoutItemBottomSheetBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                val pos = bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION){
                    mListener.onItemClick(it, pos)
                }
            }
        }

        fun bind(position: Int) {
            binding.apply {
                val todoData = list[position]

                if (position == 0) {
                    itemAssistanceText.visibility = View.VISIBLE
                }
                itemText.text = todoData.title
            }
        }
    }

    class TodoItemDecoration(private val leftSpace: Int) :
        RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {

            val position = parent.getChildAdapterPosition(view) //??? ??????????????? ?????? (index)
            val totalItemCount = state.itemCount                //??? ????????? ???
            val scrollPosition = state.targetScrollPosition     //????????? ????????? ????????? position

            //????????? ???????????? ?????????
            if (position != 0)
                outRect.left = leftSpace
        }
    }
}
