package com.example.androidtechnote.room

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.androidtechnote.room.model.Test
import com.example.androidtechnote.room.model.TestDao
import com.example.androidtechnote.room.model.TestDatabase

class TestRepository(application: Application) {
    private val testDatabase: TestDatabase = TestDatabase.getInstance(application)!!
    private val testDao: TestDao = testDatabase.testDao()

    fun getAll(): LiveData<List<Test>> {
        return testDao.getAll()
    }
    fun insert(test: Test){
        testDao.insert(test)
    }
    fun delete(test: Test){
        testDao.delete(test)
    }

    fun update(test: Test){
        testDao.update(test)
    }
}