package com.example.androidtechnote.customview

import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import android.util.Pair as UtilPair
import com.example.androidtechnote.R
import com.example.androidtechnote.databinding.ActivityCustomViewBinding
import kotlinx.coroutines.flow.collectLatest

class CustomViewActivity : AppCompatActivity() {

    lateinit var binding: ActivityCustomViewBinding
    private val viewModel: FocusCustomViewModel by viewModels()

    private var focusAdapter = FocusMovieAdapter()

    override fun onResume() {
        super.onResume()

        //DetailActivity 에서 돌아왔을때 아이템 포커싱
        if (viewModel.uiState.value is UiState.Success)
            binding.focusLayout.itemFocus()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_custom_view)
        binding.callBack = callBack

        binding.focusLayout.focusBinding.recycler.apply {
            focusAdapter.setOnItemClick(object : FocusMovieAdapter.OnItemClickListener {
                override fun onItemClick(v: View, position: Int, data: PopularMovie) {

                    val mIntent = Intent(applicationContext, DetailMovieActivity::class.java)
                    mIntent.putExtra("data", data.backdrop_path)
                    mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        val pair1 = UtilPair.create(v, "movie")
                        val options: ActivityOptions = ActivityOptions.makeSceneTransitionAnimation(this@CustomViewActivity, pair1)
                        startActivity(mIntent, options.toBundle())
                    } else {
                        startActivity(mIntent)
                    }
                }
            })
            adapter = focusAdapter

            addOnItemTouchListener(object : RecyclerView.SimpleOnItemTouchListener() {
                override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                    return true
                }
            })
        }

        lifecycleScope.launchWhenStarted {
            viewModel.uiState.collectLatest {
                when (it) {
                    is UiState.Init -> {
                        Log.d(applicationContext.javaClass.simpleName, "Init")
                    }
                    is UiState.Success -> {
                        Log.d(applicationContext.javaClass.simpleName, "Success")
                        focusAdapter.submitList(it.data) {
                            binding.focusLayout.apply {
                                focusBinding.recycler.post {
                                    itemFocus()
                                    setBgGlide(getBgUrl())
                                    setHasFixedSize(true)       //Adapter Item View의 내용이 변경되어도 RecyclerView의 크기는 고정
                                }
                            }
                        }
                    }
                    is UiState.Error -> {
                        Log.d(applicationContext.javaClass.simpleName, "Error")
                        Toast.makeText(applicationContext, "${it.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    interface CustomCallBack {
        fun onClick(view: View)
    }

    private val callBack = object : CustomCallBack {
        override fun onClick(view: View) {
            binding.apply {
                when (view.id) {
                    btnLeft.id -> {
                        binding.focusLayout.apply {
                            prevItemFocus()
                            setBgGlide(getBgUrl())
                        }
                    }
                    btnRight.id -> {
                        binding.focusLayout.apply {
                            nextItemFocus()
                            setBgGlide(getBgUrl())
                        }
                    }
                    btnOk.id -> {
                        binding.focusLayout.selectItem()
                    }
                }
            }
        }
    }

    private fun getBgUrl() : String = MoviesRepository.TMDB_POPULAR_MOVIE_IMG_ORIGINAL + focusAdapter.currentList[
            binding.focusLayout.focusPosition
    ].backdrop_path
}