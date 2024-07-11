package com.ayush.lokalapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ayush.lokalapp.data.local.BookMarkDao
import com.ayush.lokalapp.data.local.BookMarkedJobsEntity
import com.ayush.lokalapp.data.network.JobApiService
import com.ayush.lokalapp.datamodels.JobResponse
import com.ayush.lokalapp.utilities.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class JobDataRepository(
    private val jobApiService: JobApiService,
    private val bookMarkDao: BookMarkDao
) {
    private val _jobLiveData = MutableLiveData<Resource<JobResponse>>()
    val jobLiveData: LiveData<Resource<JobResponse>>
        get() = _jobLiveData

    private val _bookmarkedJobsLiveData = MutableLiveData<List<BookMarkedJobsEntity>>()
    val bookmarkedJobsLiveData: LiveData<List<BookMarkedJobsEntity>>
        get() = _bookmarkedJobsLiveData

    suspend fun fetchJobs(page: Int) {
        _jobLiveData.postValue(Resource.Loading)
        try {
            val response = jobApiService.getJobs(page = page)
            if (response.isSuccessful) {
                _jobLiveData.postValue(Resource.Success(response.body()))
            } else {
                _jobLiveData.postValue(Resource.Error("Failed to fetch data: ${response.code()}"))
            }
        } catch (e: Exception) {
            _jobLiveData.postValue(Resource.Error("Network error: ${e.message}"))
        }
    }

    suspend fun isJobBookmarked(id: Int): Boolean {
        return bookMarkDao.isAlreadyBookmark(id)
    }

    suspend fun insertBookMark(bookMarkedJobsEntity: BookMarkedJobsEntity) {
        withContext(Dispatchers.IO) {
            bookMarkDao.insertBookMark(bookMarkedJobsEntity)
        }
    }

    suspend fun deleteBookMark(id: Int) {
        withContext(Dispatchers.IO) {
            bookMarkDao.deleteBookMark(id)
        }
    }

    suspend fun fetchBookmarkedJobs() {
        withContext(Dispatchers.IO) {
            val bookmarkedJobs = bookMarkDao.getBookMarkedJobs()
            _bookmarkedJobsLiveData.postValue(bookmarkedJobs)
        }
    }
}
