package com.example.androidtechnote.recycler.custom_focus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidtechnote.recycler.custom_focus.data.FocusItem
import com.example.androidtechnote.recycler.custom_focus.data.MoviesRepository
import com.example.androidtechnote.recycler.custom_focus.data.NetworkState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

class CustomFocusViewModel : ViewModel(){
    private var moviesRepository : MoviesRepository = MoviesRepository()

    private val _uiState = MutableStateFlow<UiState<List<FocusItem>>>(UiState.Init)
    val uiState
        get() = _uiState.asStateFlow()

    var focusPosition = MutableStateFlow(0)

    var maxPosition by Delegates.notNull<Int>()

    init {
        viewModelScope.launch {
            _uiState.value = when (val result = moviesRepository.getPopularMovies()) {
                is NetworkState.Success -> UiState.Success(result.data.movies).apply {
                    maxPosition = data.size.minus(1)
                }
                is NetworkState.Error -> UiState.Error(result.message)
                is NetworkState.Exception -> UiState.Error(result.throwable.message)
            }
        }
    }

    fun minusPosition(){
        if (focusPosition.value > 0) {
            viewModelScope.launch {
                focusPosition.value -= 1
            }
        }
    }

    fun plusPosition(){
        if (focusPosition.value < maxPosition){
            viewModelScope.launch {
                focusPosition.value += 1
            }
        }
    }

    fun setPosition(position: Int){
        focusPosition.value = position
    }
}

sealed class UiState<out T> {
    object Init: UiState<Nothing>()
    data class Success<out T> (val data: T): UiState<T>()
    data class Error(val message: String?): UiState<Nothing>()
}