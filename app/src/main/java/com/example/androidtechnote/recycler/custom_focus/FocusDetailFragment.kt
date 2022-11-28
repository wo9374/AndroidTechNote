package com.example.androidtechnote.recycler.custom_focus

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.ORIENTATION_HORIZONTAL
import com.example.androidtechnote.R
import com.example.androidtechnote.databinding.FragmentFocusDetailBinding
import com.example.androidtechnote.recycler.base.BaseFragment
import kotlinx.coroutines.flow.collectLatest

class FocusDetailFragment : BaseFragment<FragmentFocusDetailBinding>(R.layout.fragment_focus_detail) {
    lateinit var viewModel: CustomFocusViewModel

    var selectedPosition = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[CustomFocusViewModel::class.java]

        (requireActivity() as CustomFocusActivity).binding.callBack = mCallBack
        binding.lifecycleOwner = this

        selectedPosition = arguments?.getInt("select") ?: 0

        val detailAdapter = MyDetailAdapter()

        binding.viewpager.apply {
            orientation = ORIENTATION_HORIZONTAL
            getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
            adapter = detailAdapter

            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
                override fun onPageSelected(position: Int) {
                    selectedPosition = position
                }
            })
        }

        lifecycleScope.launchWhenStarted {
            viewModel.uiState.collectLatest {
                when (it) {
                    is UiState.Success -> {
                        detailAdapter.submitList(it.data){
                            binding.viewpager.setCurrentItem(selectedPosition, false)
                        }
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