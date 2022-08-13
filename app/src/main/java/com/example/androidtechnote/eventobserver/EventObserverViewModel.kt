package com.example.androidtechnote.eventobserver

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class EventObserverViewModel : ViewModel() {
    private val _callEvent = MutableLiveData<EventWrapper<String>>()

    val callEvent: LiveData<EventWrapper<String>>
    get() = _callEvent


    private fun handleError(exception: Throwable) {
        val message = exception.message ?: ""
        _callEvent.value = EventWrapper(message)
    }
}