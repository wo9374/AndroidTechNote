package com.example.androidtechnote.coordinator.bottomsheet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.animation.AnimationUtils
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidtechnote.R
import com.example.androidtechnote.databinding.ActivityBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior

class BottomSheetActivity : AppCompatActivity() {
    lateinit var bottomBehavior : BottomSheetBehavior<View>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding : ActivityBottomSheetBinding = DataBindingUtil.setContentView(this, R.layout.activity_bottom_sheet)
        setContentView(binding.root)

        val list = mutableListOf<TodoData>()
        for(i in 0..9){
            list.add(TodoData("Todo_$i"))
        }

        binding.bottomInclude.apply {
            bottomBehavior = BottomSheetBehavior.from(root)
            bottomBehavior.apply {
                //setBottomSheetCallback  <- Deprecated
                addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        when(newState){
                            BottomSheetBehavior.STATE_COLLAPSED -> { //접힘
                                binding.helloText.text = "COLLAPSED"
                                iconTitle.text = "OPEN"
                            }
                            BottomSheetBehavior.STATE_EXPANDED -> {  //펼쳐짐
                                binding.helloText.text = "EXPANDED"
                                iconTitle.text = "CLOSE"
                            }
                            BottomSheetBehavior.STATE_HIDDEN -> {    //숨겨짐
                                binding.helloText.text = "HIDDEN"
                            }
                            BottomSheetBehavior.STATE_HALF_EXPANDED -> { //절반 펼쳐짐
                                binding.helloText.text = "HALF_EXPANDED"
                            }
                            BottomSheetBehavior.STATE_DRAGGING -> {  //드래그하는 중
                                binding.helloText.text = "DRAGGING"
                            }
                            BottomSheetBehavior.STATE_SETTLING -> {  //(움직이다가) 안정화되는 중
                                binding.helloText.text = "SETTLING"
                            }
                        }
                    }

                    override fun onSlide(bottomSheet: View, slideOffset: Float) { //슬라이드 될때 offset / hide -1.0 ~ collapsed 0.0 ~ expended 1.0
                        icon.rotation = slideOffset * -180F

                        binding.bottomInclude.apply {
                            icon.rotation = slideOffset * -180F
                            bottomSheetRecycler.animate().x(bottomSheetRecycler.width * (1 - slideOffset)).setDuration(0).start()
                            bottomSheetRecycler.alpha = slideOffset
                        }
                        bottomSheetRecycler.animate().x(bottomSheetRecycler.width * (1 - slideOffset)).setDuration(0).start()
                        bottomSheetRecycler.alpha = slideOffset
                    }
                })

                saveFlags = BottomSheetBehavior.SAVE_ALL
            }

            bottomSheetRecycler.apply {
                val bottomSheetAdapter = BottomSheetTodoAdapter(this@BottomSheetActivity, list)
                bottomSheetAdapter.setOnItemClickListener(object :BottomSheetTodoAdapter.OnItemClickListener{
                    override fun onItemClick(v: View, pos: Int) {

                    }
                })
                adapter = bottomSheetAdapter
                layoutManager = LinearLayoutManager(this@BottomSheetActivity, LinearLayoutManager.HORIZONTAL,false)
                addItemDecoration(BottomSheetTodoAdapter.TodoItemDecoration(5.dp))
            }

            bottomBtn.setOnClickListener {
                bottomBehavior.state = if (bottomBehavior.state == BottomSheetBehavior.STATE_COLLAPSED){
                    BottomSheetBehavior.STATE_EXPANDED
                } else
                    BottomSheetBehavior.STATE_COLLAPSED

            }
        }
    }

    val Int.dp: Int
        get() {
            val metrics = resources.displayMetrics
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), metrics).toInt()
        }
}