package com.example.androidtechnote.recycler.paging3.retrofit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.androidtechnote.R
import com.example.androidtechnote.databinding.ActivityPgRetrofitBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PgRetrofitActivity : AppCompatActivity() {
    private lateinit var pgRetrofitAdapter: PgRetrofitAdapter
    private lateinit var viewModel: PgRetrofitViewModel
    private lateinit var binding: ActivityPgRetrofitBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pg_retrofit)

        pgRetrofitAdapter = PgRetrofitAdapter()
        binding.pageRecycler.apply {
            adapter = pgRetrofitAdapter
            layoutManager = GridLayoutManager(this@PgRetrofitActivity, 3)
        }

        viewModel = PgRetrofitViewModel(PgRetrofitRepository())

        lifecycleScope.launch {
            viewModel.pagingData.collectLatest {
                pgRetrofitAdapter.submitData(it)
            }
        }

        binding.refreshBtn.setOnClickListener {
            pgRetrofitAdapter.refresh()
        }
    }
}