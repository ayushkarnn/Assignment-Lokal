package com.ayush.lokalapp.datamodels


data class JobUIModel(
    val id: Int,
    val companyName: String,
    val title: String,
    val location: String,
    val phone: String,
    val salaryRange: String,
    val isOpen: Boolean,
    var isBookmarked: Boolean,
    val datePosted: String,
    val imageUrl: String,
    val jobType: String,
    val jobExperience: String,
    val jobQualification: String,
    val totalVacancies: String,
    val totalViews:String,
    val totalShare:String,
    val whatsAppLink:String
)

