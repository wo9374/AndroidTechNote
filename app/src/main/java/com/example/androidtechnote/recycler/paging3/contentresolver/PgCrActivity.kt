package com.example.androidtechnote.recycler.paging3.contentresolver

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView.ItemAnimator
import androidx.recyclerview.widget.SimpleItemAnimator
import com.example.androidtechnote.R
import com.example.androidtechnote.databinding.ActivityPgCrBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class PgCrActivity : AppCompatActivity() {
    private lateinit var pagingAdapter: PgCrAdapter
    private lateinit var viewModel : PgCrViewModel
    private lateinit var binding : ActivityPgCrBinding

    companion object {
        const val REQUEST_PERMISSION_CODE = 100
        var checkBoxOnOff = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pg_cr)

        pagingAdapter = PgCrAdapter()
        binding.pageRecycler.apply {
            adapter = pagingAdapter
            layoutManager = GridLayoutManager(this@PgCrActivity, 3)
        }

        //리사이클러뷰 notify animation 끄기
        val animator: ItemAnimator? = binding.pageRecycler.itemAnimator
        if (animator is SimpleItemAnimator)
            animator.supportsChangeAnimations = false
        binding.pageRecycler.itemAnimator = animator

        checkPermission()


       /* binding.refreshBtn.setOnClickListener {
            pagingAdapter.refresh()
        }*/

        binding.sendBtn.setOnClickListener {
            val selectList = mutableListOf<PhotoItem>()

            pagingAdapter.snapshot().items.forEach {
                if (it.checkBoolean){
                    selectList.add(it)
                }
            }
            Log.d("선택된 리스트", "$selectList")
        }
    }

    fun checkPermission() {
        val permissionCheck = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            setPaging()
        } else {
            requestPermission()
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )) {
                //거부했을 경우
                showToast("기능 사용을 위한 동의가 필요합니다.1")
            } else {
                showToast("기능 사용을 위한 동의가 필요합니다.2")
            }
        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            REQUEST_PERMISSION_CODE
        )
    }

    fun setPaging() {
        viewModel = PgCrViewModel(this)

        lifecycleScope.launch {
            viewModel.pagingFlow.collectLatest {
                pagingAdapter.submitData(it)
            }
        }
        /*pagingAdapter.setOnItemLongClickListener(object : PgCrAdapter.OnItemLongClickListener {
            override fun onItemClick(v: View, pos: Int) {
                PgCrAdapter.checkBoxOnOff = !PgCrAdapter.checkBoxOnOff
                for (i in pagingAdapter.snapshot().items.indices){
                    pagingAdapter.snapshot().items[i].onOffCheck = PgCrAdapter.checkBoxOnOff
                    pagingAdapter.notifyItemChanged(i)
                }
            }
        })*/
    }

    override fun onDestroy() {
        super.onDestroy()
        checkBoxOnOff = false
    }

    fun showToast(string: String){
        Toast.makeText(this, string, Toast.LENGTH_LONG)
    }
}