package com.example.androidtechnote.recycler.epoxy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidtechnote.R
import com.example.androidtechnote.databinding.ActivityEpoxyBinding

class EpoxyActivity : AppCompatActivity() {

    lateinit var binding: ActivityEpoxyBinding

    private val singleFoodController : SingleFoodController by lazy{ SingleFoodController() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_epoxy)

        val linearLayoutManager = LinearLayoutManager(this)

        binding.epoxyRecycler.apply {
            layoutManager = linearLayoutManager
            setHasFixedSize(true)
            adapter = singleFoodController.adapter
            addItemDecoration(DividerItemDecoration(this@EpoxyActivity, linearLayoutManager.orientation))
        }

        //This statement builds model and add it to the recycler view
        singleFoodController.requestModelBuild()
    }
}