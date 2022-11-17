package com.example.androidtechnote.recycler.custom_focus

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.example.androidtechnote.R
import com.example.androidtechnote.databinding.ActivityCustomFocusBinding
import com.example.customlibrary.MoviesRepository
import kotlinx.coroutines.flow.collectLatest

class CustomFocusActivity : AppCompatActivity() {

    lateinit var binding : ActivityCustomFocusBinding
    private val viewModel : CustomFocusViewModel by viewModels()

    lateinit var listAdapter: CustomFocusListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_custom_focus)
        binding.callBack = callBack

        binding.apply {
            lifecycleScope.launchWhenStarted {
                viewModel.uiState.collectLatest {
                    when (it) {
                        is UiState.Init -> {
                            listAdapter = CustomFocusListAdapter(
                                binding.focusLayout.itemHighLight,
                                binding.focusLayout.itemCornerDp
                            )
                        }
                        is UiState.Success -> {
                            focusLayout.recyclerView.adapter = listAdapter

                            listAdapter.submitList(it.data) {
                                focusLayout.recyclerView.post {
                                    focusLayout.itemFocus()
                                    focusLayout.setBgGlide(getBgUrl())
                                }
                            }
                        }
                        is UiState.Error -> {
                            Toast.makeText(applicationContext, "${it.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    fun getBgUrl(): String = MoviesRepository.TMDB_POPULAR_MOVIE_IMG_ORIGINAL + listAdapter.currentList[binding.focusLayout.focusPosition].backdrop_path

    interface CallBack {
        fun onClick(view: View)
    }

    private val callBack = object : CallBack {
        override fun onClick(view: View) {
            binding.apply {
                when (view.id) {
                    btnLeft.id -> {
                        binding.focusLayout.apply {
                            if(prevItemFocus()) setBgGlide(getBgUrl())
                        }
                    }
                    btnRight.id -> {
                        binding.focusLayout.apply {
                            if (nextItemFocus()) setBgGlide(getBgUrl())
                        }
                    }
                    btnOk.id -> {
                        binding.focusLayout.selectItem()
                    }
                }
            }
        }
    }
}