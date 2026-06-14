package com.example.di

import android.content.Context
import androidx.room.Room
import com.example.data.database.AppDatabase
import com.example.data.database.TaskDao

class DatabaseModule(private val context: Context) {
    val database: AppDatabase by lazy {
        Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "tasks_db"
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    val taskDao: TaskDao by lazy {
        database.taskDao()
    }
}
