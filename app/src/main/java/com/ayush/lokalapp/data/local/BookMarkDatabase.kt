package com.ayush.lokalapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [BookMarkedJobsEntity::class], version = 1, exportSchema = false)
abstract class BookMarkDatabase : RoomDatabase() {

    abstract fun bookMarkDao(): BookMarkDao

    companion object {
        @Volatile
        private var INSTANCE: BookMarkDatabase? = null

        fun getDatabase(context: Context): BookMarkDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BookMarkDatabase::class.java,
                    "bookmark_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
