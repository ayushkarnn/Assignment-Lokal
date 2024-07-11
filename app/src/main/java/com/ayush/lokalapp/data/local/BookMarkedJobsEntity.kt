package com.ayush.lokalapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class BookMarkedJobsEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val companyName: String,
    val title: String,
    val location: String,
    val phone: String,
    val salaryRange: String,
    val datePosted: String,
    val jobType: String,
    val jobExperience: String,
    val jobQualification: String,
    val totalVacancies: String,
    val totalViews: String,
    val totalShare: String,
    val whatsAppLink: String,
    val dateBookMarked: String
)
