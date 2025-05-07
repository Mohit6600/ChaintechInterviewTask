package com.example.chaintechinterviewtask.domain

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "passwords")
data class Password(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val accountType: String,
    val accountName: String,
    val username: String,
    val encryptedPassword: String, // Stored encrypted
    val iv: String, // Initialization vector for encryption
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)