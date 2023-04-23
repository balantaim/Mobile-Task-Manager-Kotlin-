package com.baubuddy.application.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.baubuddy.application.model.Task
import com.baubuddy.application.model.TaskDatabase
import com.baubuddy.application.model.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application){

    val readAllData: LiveData<List<Task>>
    val repository: TaskRepository

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