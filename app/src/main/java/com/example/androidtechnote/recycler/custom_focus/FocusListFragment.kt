package com.example.androidtechnote.recycler.custom_focus

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.androidtechnote.R
import com.example.androidtechnote.databinding.FragmentFocusListBinding
import com.example.androidtechnote.recycler.base.BaseFragment
import com.example.customlibrary.MoviesRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

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

                viewModel.uiState.collectLatest { uiState ->
                    when (uiState) {
                        is UiState.Init -> {
                            listAdapter = CustomFocusListAdapter(itemHighLight, itemCornerDp){ clickItem, position ->
                                val bundle = bundleOf("select" to position)
                                navController.navigate(R.id.focusDetailFragment, bundle)
                            }
                        }
                        is UiState.Success -> {
                            recyclerView.adapter = listAdapter

                            listAdapter.submitList(uiState.data) {
                                recyclerView.post {

                                    lifecycleScope.launch {
                                        viewModel.focusPosition.collectLatest {
                                            itemFocus(it)
                                            setBgGlide(getBgUrl(it))
                                        }
                                    }
                                }
                            }
                        }
                        is UiState.Error -> {
                            Toast.makeText(requireContext(), "${uiState.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    private fun getBgUrl(position: Int): String = MoviesRepository.TMDB_POPULAR_MOVIE_IMG_ORIGINAL + listAdapter.currentList[position].backdrop_path

    override fun prevFocus() {
        viewModel.minusPosition()
    }

    override fun nextFocus() {
        viewModel.plusPosition()
    }

    override fun okFocus() {
        binding.focusLayout.selectItem(viewModel.focusPosition.value)
    }
}