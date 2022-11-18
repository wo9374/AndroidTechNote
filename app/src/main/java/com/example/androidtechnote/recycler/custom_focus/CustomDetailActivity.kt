package com.example.androidtechnote.recycler.custom_focus

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.androidtechnote.R
import com.example.androidtechnote.databinding.ActivityCustomDetailBinding
import com.example.androidtechnote.databinding.ItemBaseTestBinding
import com.example.androidtechnote.recycler.base.BaseListAdapter
import com.example.customlibrary.FocusItem
import kotlinx.coroutines.flow.collectLatest

class CustomDetailActivity : AppCompatActivity() {

    lateinit var binding: ActivityCustomDetailBinding

    private val viewModel: CustomFocusViewModel by viewModels()

    override fun onEnterAnimationComplete() {
        super.onEnterAnimationComplete()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_custom_detail)

        val mAdapter = BaseListAdapter<FocusItem>(DiffUtil())


        lifecycleScope.launchWhenStarted {
            viewModel.uiState.collectLatest {
                when(it){
                    is UiState.Init -> {
                        binding.viewpager.apply {
                            orientation = ViewPager2.ORIENTATION_HORIZONTAL
                            (getChildAt(0) as RecyclerView).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
                            adapter = mAdapter
                        }
                    }
                    is UiState.Success -> {
                        /*Glide.with(binding.detailImg)
                            .load(MoviesRepository.TMDB_POPULAR_MOVIE_IMG_ORIGINAL + it.data[0].backdrop_path)
                            .override(1400, 800)
                            .into(binding.detailImg)*/

                        mAdapter.apply {
                            submitList(it.data)
                            expressionOnCreateViewHolder  = { viewGroup ->
                                ItemBaseTestBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
                            }
                            expressionViewHolderBinding = { focusItem, viewBinding ->
                                var view = viewBinding as ItemBaseTestBinding
                                view.title.text = focusItem.title
                            }
                        }
                    }
                    else -> {

                    }
                }

            }
        }
    }
}