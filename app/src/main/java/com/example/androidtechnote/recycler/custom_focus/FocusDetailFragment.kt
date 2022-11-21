package com.example.androidtechnote.recycler.custom_focus

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.app.SharedElementCallback
import androidx.core.view.doOnPreDraw
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionInflater
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.ORIENTATION_HORIZONTAL
import com.example.androidtechnote.R
import com.example.androidtechnote.databinding.FragmentFocusDetailBinding
import com.example.androidtechnote.recycler.base.BaseFragment
import com.example.focusablelistview.OnImageReadyListener
import kotlinx.coroutines.flow.collectLatest
import kotlin.properties.Delegates

class FocusDetailFragment : BaseFragment<FragmentFocusDetailBinding>(R.layout.fragment_focus_detail) {
    lateinit var viewModel: CustomFocusViewModel

    var focusPosition by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(R.transition.image_shared_element_transition)
        else
            sharedElementEnterTransition = null
        
        focusPosition = arguments?.getInt("select") ?: 0
        prepareTransitions()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[CustomFocusViewModel::class.java]

        (requireActivity() as CustomFocusActivity).binding.callBack = mCallBack
        binding.lifecycleOwner = this

        val detailAdapter = MyDetailAdapter(object : OnImageReadyListener{
            override fun onImageReady(position: Int) {
                if (position == focusPosition)
                    startPostponedEnterTransition()
            }
        })

        binding.viewpager.apply {
            orientation = ORIENTATION_HORIZONTAL
            getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
            adapter = detailAdapter
        }

        lifecycleScope.launchWhenStarted {
            viewModel.uiState.collectLatest {
                when (it) {
                    is UiState.Success -> { detailAdapter.submitList(it.data) }
                    else -> {}
                }
            }
        }
        postponeEnterTransition()

        binding.viewpager.apply {
            doOnPreDraw { setCurrentItem(0, false) }
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
                override fun onPageSelected(position: Int) {
                    focusPosition = position
                }
            })
        }
    }

    private fun prepareTransitions(){
        setEnterSharedElementCallback(object : SharedElementCallback(){
            override fun onMapSharedElements(names: MutableList<String>?, sharedElements: MutableMap<String, View>?) {
                val position = focusPosition
                binding.viewpager.findViewWithTag<ViewGroup>(focusPosition)?.findViewById<ImageView>(R.id.image)?.let {  }
            }
        })
    }

    override fun prevFocus() {
        TODO("Not yet implemented")
    }

    override fun nextFocus() {
        TODO("Not yet implemented")
    }

    override fun okFocus() {
        TODO("Not yet implemented")
    }


}