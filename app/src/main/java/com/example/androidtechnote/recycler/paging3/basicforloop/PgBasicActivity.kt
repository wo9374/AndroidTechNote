package com.example.androidtechnote.recycler.paging3.basicforloop

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidtechnote.R
import com.example.androidtechnote.databinding.ActivityBasicPagingBinding
import com.example.androidtechnote.recycler.focus.SpaceItemSpaceDeco
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class PgBasicActivity : AppCompatActivity() {
    private lateinit var viewModelPg : PgBasicViewModel
    private lateinit var pagingAdapter: PgBasicAdapter

    private lateinit var binding: ActivityBasicPagingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_basic_paging)

        pagingAdapter = PgBasicAdapter()
        pagingAdapter.setOnItemClick(object : PgBasicAdapter.ItemClickListener{
            override fun onClick(v: View, position: Int) {

            }
        })

        binding.recyclerView.apply {
            adapter = pagingAdapter
            layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        }

        val service = PgForLoopService()

        viewModelPg = PgBasicViewModel(PgBasicRepository(service))

        lifecycleScope.launch {
            viewModelPg.pagingData.collectLatest {
                pagingAdapter.submitData(it)
            }
        }
    }
}


