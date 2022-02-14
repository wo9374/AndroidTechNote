package com.example.androidtechnote.recycler.paging3.basicforloop

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.androidtechnote.R
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PgBasicActivity : AppCompatActivity() {
    private lateinit var viewModelPg : PgBasicViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var pagingAdapter: PagingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basic_paging)

        recyclerView = findViewById(R.id.recyclerView)

        pagingAdapter = PagingAdapter()
        recyclerView.adapter = pagingAdapter

        val service = PgForLoopService()

        viewModelPg = PgBasicViewModel(PgBasicRepository(service))

        lifecycleScope.launch {
            viewModelPg.pagingData.collectLatest {
                pagingAdapter.submitData(it)
            }
        }
    }
}


