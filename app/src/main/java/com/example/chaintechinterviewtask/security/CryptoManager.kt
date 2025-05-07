package com.example.chaintechinterviewtask.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class CryptoManager {
    private val KEY_ALIAS = "SECURE_PASS_KEY"
    private val ANDROID_KEYSTORE = "AndroidKeyStore"
    private val TRANSFORMATION = "${KeyProperties.KEY_ALGORITHM_AES}/${KeyProperties.BLOCK_MODE_GCM}/${KeyProperties.ENCRYPTION_PADDING_NONE}"
    private val TAG_LENGTH = 128
    private fun getOrCreateKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply {
            load(null)
        }

        return if (keyStore.containsAlias(KEY_ALIAS)) {
            val entry = keyStore.getEntry(KEY_ALIAS, null) as KeyStore.SecretKeyEntry
            entry.secretKey
        } else {
            val keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                ANDROID_KEYSTORE
            )
            val spec = KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .setUserAuthenticationRequired(false)
                .build()

            keyGenerator.init(spec)
            keyGenerator.generateKey()
        }
    }

    fun encrypt(plainText: String): Pair<String, String> {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateKey())

        val iv = cipher.iv
        val encryptedBytes = cipher.doFinal(plainText.toByteArray())

        return Pair(
            Base64.encodeToString(encryptedBytes, Base64.DEFAULT),
            Base64.encodeToString(iv, Base64.DEFAULT)
        )
    }

    fun decrypt(encryptedData: String, ivString: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val iv = Base64.decode(ivString, Base64.DEFAULT)
        val spec = GCMParameterSpec(TAG_LENGTH, iv)

        cipher.init(Cipher.DECRYPT_MODE, getOrCreateKey(), spec)
        val decryptedBytes = cipher.doFinal(Base64.decode(encryptedData, Base64.DEFAULT))

        return String(decryptedBytes)
    }
}
