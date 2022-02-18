package com.example.androidtechnote.recycler.epoxy

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidtechnote.R
import com.example.androidtechnote.databinding.ActivityEpoxyBinding
import com.example.androidtechnote.singlefoodLayout


class EpoxyActivity : AppCompatActivity() {

    lateinit var binding: ActivityEpoxyBinding

    private val testController: SingleFoodController by lazy { SingleFoodController() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_epoxy)

        initRecycler()
    }

    private fun initRecycler() {
        val linearLayoutManager = LinearLayoutManager(this)

        //일반 Epoxy
        /*binding.epoxyRecycler.apply {
            layoutManager = linearLayoutManager
            setHasFixedSize(true)
            adapter = testController.adapter
            addItemDecoration(DividerItemDecoration(this@EpoxyActivity, linearLayoutManager.orientation))
        }*/
        //testController.requestModelBuild()


        //EpoxyDataBinding 으로 구성
        val dataList = FoodDataFactory.getFoodItems(50)

        binding.epoxyRecycler.apply {
            layoutManager = linearLayoutManager
            setHasFixedSize(true)
            addItemDecoration(DividerItemDecoration(this@EpoxyActivity, linearLayoutManager.orientation))

            //EpoxyRecyclerView 를 찾아서 withModels 를 사용하면 별도의 epoxyController 생성없이 EpoxyRecyclerView 에 모델을 추가가능
            withModels {
                dataList.forEachIndexed { index, forEachFood ->
                    singlefoodLayout {
                        id(index)
                        food(forEachFood)
                        onClickItem { model, parentView, clickedView, position ->
                            Log.d("Epoxy", "$position 눌러졌다옹")
                        }
                    }
                }
            }
        } //binding.epoxyRecycler.apply
    }
}