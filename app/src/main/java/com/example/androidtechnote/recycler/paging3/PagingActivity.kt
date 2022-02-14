package com.example.androidtechnote.recycler.paging3

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.androidtechnote.R
import com.example.androidtechnote.databinding.ActivityPagingBinding
import com.example.androidtechnote.recycler.paging3.basicforloop.PgBasicActivity
import com.example.androidtechnote.recycler.paging3.contentresolver.PgCrActivity
import com.example.androidtechnote.recycler.paging3.retrofit.PgRetrofitActivity

class PagingActivity : AppCompatActivity() {
    lateinit var binding : ActivityPagingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_paging)

        binding.pagingForloopBtn.setOnClickListener {
            val intent = Intent(this, PgBasicActivity::class.java)
            startActivity(intent)
        }

        binding.pagingBtn.setOnClickListener {
            val intent = Intent(this, PgRetrofitActivity::class.java)
            startActivity(intent)
        }

        binding.pgcrBtn.setOnClickListener {
            val intent = Intent(this, PgCrActivity::class.java)
            startActivity(intent)
        }
    }
}