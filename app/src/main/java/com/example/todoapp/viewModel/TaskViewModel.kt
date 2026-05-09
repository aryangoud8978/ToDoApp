package com.example.todoapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.Task
import com.example.todoapp.data.TaskDatabase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = TaskDatabase
        .getDatabase(application)
        .taskDao()

    val tasks = dao.getAllTasks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList()
        )

    fun addTask(title: String) {

        viewModelScope.launch {

            dao.insertTask(
                Task(title = title)
            )
        }
    }

    fun deleteTask(task: Task) {

        viewModelScope.launch {

            dao.deleteTask(task)
        }
    }

    fun updateTask(task: Task) {

        viewModelScope.launch {

            dao.updateTask(task)
        }
    }
}