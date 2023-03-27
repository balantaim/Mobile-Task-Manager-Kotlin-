package com.baubuddy.application.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.baubuddy.application.data.Task
import com.baubuddy.application.data.TaskDatabase
import com.baubuddy.application.data.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application){

    private val readAllData: LiveData<List<Task>>
    private val repository: TaskRepository

    init{
        val taskDao = TaskDatabase.getDatabase(application).taskDao()
        repository = TaskRepository(taskDao)
        readAllData = repository.readAllData
    }

    fun addTask(task: Task){
        viewModelScope.launch(Dispatchers.IO) {
            repository.addTask(task)
        }
    }
    fun getAllTask(){
        viewModelScope.launch(Dispatchers.IO) {
            repository.readStoredData()
        }
    }

}