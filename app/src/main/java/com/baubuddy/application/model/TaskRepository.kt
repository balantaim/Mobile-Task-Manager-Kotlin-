package com.baubuddy.application.model

import androidx.lifecycle.LiveData

class TaskRepository(private val taskDao: TaskDao) {
    val readAllData: LiveData<List<Task>> = taskDao.readStoredData()

    suspend fun addTask(task: Task){
        taskDao.addTask(task)
    }
    suspend fun removeAllData(){
        taskDao.dropMyStoredData()
    }
    suspend fun readStoredData(){
        taskDao.readStoredData()
    }
}