package com.example.androidtechnote.recycler.paging3.retrofit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn

class PgRetrofitViewModel(repository: PgRetrofitRepository) : ViewModel() {
    val pagingData = repository.getPagingRetrofit().cachedIn(viewModelScope)
}