package com.example.androidtechnote

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidtechnote.databinding.ActivityFocusBinding
import com.example.androidtechnote.recycler.paging3.basicforloop.PgBasicAdapter
import com.example.androidtechnote.recycler.paging3.basicforloop.PgBasicRepository
import com.example.androidtechnote.recycler.paging3.basicforloop.PgBasicViewModel
import com.example.androidtechnote.recycler.paging3.basicforloop.PgForLoopService
import com.example.androidtechnote.room.model.Test
import com.example.androidtechnote.room.view.TestAdapter
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FocusActivity : AppCompatActivity() {

    lateinit var binding: ActivityFocusBinding

    private lateinit var viewModelPg : PgBasicViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_focus)
        binding.callBack = callBack

        setSupportActionBar(binding.toolBar)
        supportActionBar!!.apply {
            setDisplayShowCustomEnabled(true)
            setDisplayShowTitleEnabled(true) //기본 타이틀 유무
            setDisplayHomeAsUpEnabled(true)  //뒤로가기 버튼 자동 추가
        }

        val mAdapter = TestAdapter({ test -> {}}, { test -> {} })
        mAdapter.setTestItemList(
            listOf(
                Test(null, "Title1", "1", ""),
                Test(null, "Title2", "2", ""),
                Test(null, "Title3", "3", ""),
                Test(null, "Title4", "4", ""),
                Test(null, "Title5", "5", ""),
                Test(null, "Title6", "6", ""),
                Test(null, "Title7", "7", ""),
                Test(null, "Title8", "8", ""),
                Test(null, "Title9", "9", ""),
            )
        )

        binding.recycler.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_focus, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.home -> {
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    interface Callback{
        fun onClick(view : View)
    }

    private val callBack = object : Callback{
        override fun onClick(view : View){
            var intent = Intent()

            binding.apply {
                when(view.id){
                    btnLeft.id -> {

                    }
                    btnRight.id -> {

                    }
                    btnOk.id -> {}
                }
            }
            //startActivity(intent)
        }
    }
}