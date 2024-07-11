package com.ayush.lokalapp.data.network

import com.ayush.lokalapp.datamodels.JobResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface JobApiService {

    @GET("common/jobs")
    suspend fun getJobs(@Query("page") page: Int): Response<JobResponse>
}
