package com.example.androidtechnote.room.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.androidtechnote.room.TestRepository
import com.example.androidtechnote.room.model.Test

class RoomDbViewModel(application: Application) : ViewModel() {
    private val repository = TestRepository(application)

    fun getAll():LiveData<List<Test>>{
        return repository.getAll()
    }

    fun insert(test: Test){
        repository.insert(test)
    }
    fun delete(test: Test){
        repository.delete(test)
    }

    override fun onCleared() {
        super.onCleared()
    }
}