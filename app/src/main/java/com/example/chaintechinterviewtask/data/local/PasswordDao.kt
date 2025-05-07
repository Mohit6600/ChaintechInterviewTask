package com.example.chaintechinterviewtask.data.local

import androidx.room.*
import com.example.chaintechinterviewtask.domain.Password
import kotlin.collections.List

@Dao
interface PasswordDao {
    @Query("SELECT * FROM passwords ORDER BY updatedAt DESC")
    suspend fun getAllPasswords(): List<Password>

    @Query("SELECT * FROM passwords WHERE id = :id")
    suspend fun getPasswordById(id: Long): Password?

    @Insert
    suspend fun insertPassword(password: Password): Long

    @Update
    suspend fun updatePassword(password: Password)

    @Delete
    suspend fun deletePassword(password: Password)

    @Query("DELETE FROM passwords WHERE id = :id")
    suspend fun deletePasswordById(id: Long)
}
