package com.example.androidtechnote.recycler.custom_focus

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.RecyclerView
import com.example.androidtechnote.R
import com.example.androidtechnote.databinding.FragmentFocusListBinding
import com.example.androidtechnote.recycler.base.BaseFragment
import com.example.customlibrary.FocusItem
import com.example.customlibrary.MoviesRepository
import kotlinx.coroutines.flow.collectLatest

class FocusListFragment : BaseFragment<FragmentFocusListBinding>(R.layout.fragment_focus_list){
    lateinit var viewModel: CustomFocusViewModel

    lateinit var listAdapter: CustomFocusListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[CustomFocusViewModel::class.java]

        (requireActivity() as CustomFocusActivity).binding.callBack = mCallBack

        binding.lifecycleOwner = this

        binding.focusLayout.apply {
            lifecycleScope.launchWhenStarted {
                viewModel.uiState.collectLatest {
                    when (it) {
                        is UiState.Init -> {
                            listAdapter = CustomFocusListAdapter(itemHighLight, itemCornerDp){ clickItem, position ->
                                val bundle = bundleOf("select" to position)
                                navController.navigate(R.id.focusDetailFragment, bundle)
                            }
                        }
                        is UiState.Success -> {
                            recyclerView.adapter = listAdapter

                            listAdapter.submitList(it.data) {
                                recyclerView.post {
                                    itemFocus()
                                    setBgGlide(getBgUrl())
                                }
                            }
                        }
                        is UiState.Error -> {
                            Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    fun getBgUrl(): String = MoviesRepository.TMDB_POPULAR_MOVIE_IMG_ORIGINAL + listAdapter.currentList[binding.focusLayout.focusPosition].backdrop_path

    override fun prevFocus() {
        binding.focusLayout.apply {
            prevItemFocus()
            setBgGlide(getBgUrl())
        }
    }

    override fun nextFocus() {
        binding.focusLayout.apply {
            nextItemFocus()
            setBgGlide(getBgUrl())
        }
    }

    override fun okFocus() {
        binding.focusLayout.selectItem()
    }
}