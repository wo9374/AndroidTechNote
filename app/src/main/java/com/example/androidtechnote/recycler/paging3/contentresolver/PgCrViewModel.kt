package com.example.androidtechnote.recycler.paging3.contentresolver

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn

class PgCrViewModel(context: Context) : ViewModel() {
    val repository = PgCrRepository(context)
    val pagingFlow = repository.getPagingData().cachedIn(viewModelScope)
}