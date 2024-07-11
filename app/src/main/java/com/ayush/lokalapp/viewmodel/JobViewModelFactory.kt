package com.ayush.lokalapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ayush.lokalapp.data.local.BookMarkDao
import com.ayush.lokalapp.data.network.JobApiService
import com.ayush.lokalapp.repository.JobDataRepository

class JobViewModelFactory(
    private val jobApiService: JobApiService,
    private val bookMarkDao: BookMarkDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(JobDataViewModel::class.java)) {
            val repository = JobDataRepository(jobApiService, bookMarkDao)
            return JobDataViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
