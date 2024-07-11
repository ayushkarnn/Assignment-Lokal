package com.ayush.lokalapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ayush.lokalapp.data.local.BookMarkedJobsEntity
import com.ayush.lokalapp.datamodels.JobResponse
import com.ayush.lokalapp.repository.JobDataRepository
import com.ayush.lokalapp.utilities.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class JobDataViewModel(
    private val repository: JobDataRepository
) : ViewModel() {
    val jobResponseLiveData: LiveData<Resource<JobResponse>>
        get() = repository.jobLiveData

    val bookmarkedJobsLiveData: LiveData<List<BookMarkedJobsEntity>>
        get() = repository.bookmarkedJobsLiveData

    fun fetchCompanyDetails(page: Int) {
        viewModelScope.launch {
            repository.fetchJobs(page)
        }
    }

    suspend fun isJobBookmarked(id: Int): Boolean {
        return withContext(Dispatchers.IO) {
            repository.isJobBookmarked(id)
        }
    }

    fun insertBookMark(bookMarkedJobsEntity: BookMarkedJobsEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertBookMark(bookMarkedJobsEntity)
        }
    }

    fun deleteBookMark(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteBookMark(id)
        }
    }

     fun fetchBookmarkedJobs() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.fetchBookmarkedJobs()
        }
    }
}


