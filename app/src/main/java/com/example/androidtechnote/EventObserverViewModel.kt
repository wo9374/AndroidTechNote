package com.example.androidtechnote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class EventObserverViewModel : ViewModel() {
    private val _callEvent = MutableLiveData<EventWrapper<String>>()

    val callEvent: LiveData<EventWrapper<String>>
    get() = _callEvent


    private fun handleError(exception: Throwable) {
        val message = exception.message ?: ""
        _callEvent.value = EventWrapper(message)
    }
}