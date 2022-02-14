package com.example.androidtechnote.room.model

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TestDao {
    @Query("SELECT * FROM Test")
    fun getAll(): LiveData<List<Test>>

    @Insert(onConflict = OnConflictStrategy.REPLACE) // 중복 ID일 경우 교체
    fun insert(todo: Test)

    @Update
    fun update(todo: Test)

    @Delete
    fun delete(todo: Test)
}