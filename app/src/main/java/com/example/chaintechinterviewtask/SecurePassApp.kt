package com.example.chaintechinterviewtask

import android.app.Application
import androidx.room.Room
import com.example.chaintechinterviewtask.data.local.PasswordDatabase

class SecurePassApp : Application() {
    lateinit var database: PasswordDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            applicationContext,
            PasswordDatabase::class.java,
            "password_database"
        ).build()
    }
}