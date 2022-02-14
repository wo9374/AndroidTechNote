package com.example.androidtechnote.recycler.paging3.basicforloop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn

class PgBasicViewModel(repository: PgBasicRepository) : ViewModel(){
    val pagingData = repository.getPagingData().cachedIn(viewModelScope)
}

