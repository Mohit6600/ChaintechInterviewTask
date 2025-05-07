package com.example.chaintechinterviewtask.data.repository

import android.content.Context
import androidx.room.Room
import com.example.chaintechinterviewtask.security.CryptoManager
import com.example.chaintechinterviewtask.domain.Password
import com.example.chaintechinterviewtask.data.local.PasswordDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date

class PasswordRepository(context: Context) {
    private val database = Room.databaseBuilder(
        context.applicationContext,
        PasswordDatabase::class.java,
        "password_database"
    ).build()

    private val passwordDao = database.passwordDao()
    private val cryptoManager = CryptoManager()

    suspend fun getAllPasswords(): List<Password> {
        return withContext(Dispatchers.IO) {
            passwordDao.getAllPasswords()
        }
    }

    suspend fun getPasswordById(id: Long): Password? {
        return passwordDao.getPasswordById(id)
    }

    suspend fun addPassword(accountType: String, accountName: String, username: String, password: String): Long {
        val (encryptedPassword, iv) = cryptoManager.encrypt(password)

        val passwordEntity = Password(
            accountType = accountType,
            accountName = accountName,
            username = username,
            encryptedPassword = encryptedPassword,
            iv = iv,
            createdAt = Date(),
            updatedAt = Date()
        )

        return passwordDao.insertPassword(passwordEntity)
    }

    /**
     * Updates password record.
     * If newPassword is null or blank, retains the old encrypted password.
     */
    suspend fun updatePassword(
        id: Long,
        accountType: String,
        accountName: String,
        username: String,
        newPassword: String? = null
    ) {
        val existingPassword = passwordDao.getPasswordById(id) ?: return

        val (encryptedPassword, iv) = if (!newPassword.isNullOrBlank()) {
            cryptoManager.encrypt(newPassword)
        } else {
            existingPassword.encryptedPassword to existingPassword.iv
        }

        val updatedPassword = existingPassword.copy(
            accountType = accountType,
            accountName = accountName,
            username = username,
            encryptedPassword = encryptedPassword,
            iv = iv,
            updatedAt = Date()
        )

        passwordDao.updatePassword(updatedPassword)
    }

    suspend fun deletePassword(id: Long) {
        passwordDao.deletePasswordById(id)
    }

    fun decryptPassword(encryptedPassword: String, iv: String): String {
        return cryptoManager.decrypt(encryptedPassword, iv)
    }
}
