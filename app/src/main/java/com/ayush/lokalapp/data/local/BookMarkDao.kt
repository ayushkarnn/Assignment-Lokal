package com.ayush.lokalapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BookMarkDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookMark(bookMarkedJobsEntity: BookMarkedJobsEntity)

    @Query("SELECT * FROM bookmarks")
    suspend fun getBookMarkedJobs(): List<BookMarkedJobsEntity>

    @Query("DELETE FROM bookmarks WHERE id = :id")
    suspend fun deleteBookMark(id: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE id = :id)")
    suspend fun isAlreadyBookmark(id: Int): Boolean
}
