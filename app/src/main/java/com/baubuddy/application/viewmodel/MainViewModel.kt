package com.baubuddy.application.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import com.baubuddy.application.data.Task
import com.baubuddy.application.data.TaskDatabase
import com.baubuddy.application.data.TaskRepository

class MainViewModel() : ViewModel() {
    val listOfData = MutableLiveData<ArrayList<Task>>()
    val checker = MutableLiveData<Boolean>(false)


}