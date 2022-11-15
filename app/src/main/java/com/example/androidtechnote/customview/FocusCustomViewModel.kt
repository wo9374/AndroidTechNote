package com.example.androidtechnote.customview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FocusCustomViewModel : ViewModel(){

    private var moviesRepository : MoviesRepository = MoviesRepository()

    private val _uiState = MutableStateFlow<UiState<List<PopularMovie>>>(UiState.Init)
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