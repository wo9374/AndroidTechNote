package com.example.androidtechnote.recycler.custom_focus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.customlibrary.FocusItem
import com.example.customlibrary.MoviesRepository
import com.example.customlibrary.NetworkState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CustomFocusViewModel : ViewModel(){
    private var moviesRepository : MoviesRepository = MoviesRepository()

    private val _uiState = MutableStateFlow<UiState<List<FocusItem>>>(UiState.Init)
    val uiState
        get() = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.value = when (val result = moviesRepository.getPopularMovies()) {
                is NetworkState.Success -> UiState.Success(result.data.movies)
                is NetworkState.Error -> UiState.Error(result.message)
                is NetworkState.Exception -> UiState.Error(result.throwable.message)
            }
        }
    }
}

sealed class UiState<out T> {
    object Init: UiState<Nothing>()
    data class Success<out T> (val data: T): UiState<T>()
    data class Error(val message: String?): UiState<Nothing>()
}