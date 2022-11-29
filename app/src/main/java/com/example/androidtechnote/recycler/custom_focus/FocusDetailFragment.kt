package com.example.androidtechnote.recycler.custom_focus

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.app.SharedElementCallback
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Transition
import androidx.transition.TransitionInflater
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.ORIENTATION_HORIZONTAL
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.androidtechnote.R
import com.example.androidtechnote.databinding.FragmentFocusDetailBinding
import com.example.androidtechnote.recycler.base.BaseFragment
import com.example.customlibrary.MoviesRepository
import kotlinx.coroutines.flow.collectLatest


class FocusDetailFragment : BaseFragment<FragmentFocusDetailBinding>(R.layout.fragment_focus_detail) {
    private val args: FocusDetailFragmentArgs by navArgs()

    lateinit var viewModel: CustomFocusViewModel

    val detailAdapter = MyDetailAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val transition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        transition.addListener(object : Transition.TransitionListener{
            override fun onTransitionStart(transition: Transition) {}
            override fun onTransitionEnd(transition: Transition) {
                transition.removeListener(this)

                binding.viewpager.visibility = View.VISIBLE
                binding.imageView.visibility = View.GONE
            }
            override fun onTransitionCancel(transition: Transition) {}
            override fun onTransitionPause(transition: Transition) {}
            override fun onTransitionResume(transition: Transition) {}
        })
        sharedElementEnterTransition = transition
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Transition Set
        binding.imageView.transitionName = args.transName

        viewModel = ViewModelProvider(requireActivity())[CustomFocusViewModel::class.java]

        (requireActivity() as CustomFocusActivity).binding.callBack = mCallBack
        binding.lifecycleOwner = this


        binding.viewpager.apply {
            orientation = ORIENTATION_HORIZONTAL
            getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
            adapter = detailAdapter

            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
                override fun onPageSelected(position: Int) {
                    //viewModel.setPosition(position)
                }
            })
        }


        lifecycleScope.launchWhenStarted {
            viewModel.uiState.collectLatest {
                when (it) {
                    is UiState.Success -> {
                        detailAdapter.submitList(it.data){
                            binding.viewpager.setCurrentItem(viewModel.focusPosition.value, false)
                        }

                        Glide
                            .with(binding.imageView)
                            .load(MoviesRepository.TMDB_POPULAR_MOVIE_IMG_ORIGINAL + it.data[viewModel.focusPosition.value].poster_path)
                            .override(1400,800)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .skipMemoryCache(true)
                            .into(binding.imageView)
                    }
                    else -> {}
                }
            }
        }
    }

    override fun prevFocus() {

    }

    override fun nextFocus() {

    }

    override fun okFocus() {

    }
}