package com.baubuddy.application.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTask(task: Task)

    @Query("DELETE FROM task_table")
    suspend fun dropMyStoredData()

    @Query("SELECT * FROM task_table")
    fun readStoredData(): LiveData<List<Task>>

//    @Query("SELECT * FROM task_table")
//    fun readStoredData(): LiveData<ArrayList<Task>>

}