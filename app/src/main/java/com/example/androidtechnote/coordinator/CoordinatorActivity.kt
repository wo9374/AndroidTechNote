package com.example.androidtechnote.coordinator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.androidtechnote.R
import com.example.androidtechnote.databinding.ActivityCoordinatorBinding
import com.example.androidtechnote.recycler.paging3.basicforloop.PgBasicAdapter
import com.example.androidtechnote.recycler.paging3.basicforloop.PgBasicRepository
import com.example.androidtechnote.recycler.paging3.basicforloop.PgBasicViewModel
import com.example.androidtechnote.recycler.paging3.basicforloop.PgForLoopService
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CoordinatorActivity : AppCompatActivity() {

    private lateinit var viewModelPg : PgBasicViewModel
    private lateinit var pagingAdapter: PgBasicAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding : ActivityCoordinatorBinding = DataBindingUtil.setContentView(this, R.layout.activity_coordinator)
        setContentView(binding.root)

        pagingAdapter = PgBasicAdapter()
        pagingAdapter.setOnItemClick(object : PgBasicAdapter.ItemClickListener{
            override fun onClick(v: View, position: Int) {

            }
        })
        binding.recyclerView.adapter = pagingAdapter
        binding.recyclerView.layoutManager = GridLayoutManager(this , 4)

        val service = PgForLoopService()

        viewModelPg = PgBasicViewModel(PgBasicRepository(service))

        lifecycleScope.launch {
            viewModelPg.pagingData.collectLatest {
                pagingAdapter.submitData(it)
            }
        }
    }
}