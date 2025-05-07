package com.example.chaintechinterviewtask.presentation.screen

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.chaintechinterviewtask.data.repository.PasswordRepository
import com.example.chaintechinterviewtask.presentation.navigation.AppNavigation
import com.example.chaintechinterviewtask.ui.theme.SecurePassTheme

class MainActivity : FragmentActivity() {
    private lateinit var passwordRepository: PasswordRepository
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        passwordRepository = PasswordRepository(applicationContext)

        setContent {
            var authFailed by remember { mutableStateOf(false) }
            var errorMessage by remember { mutableStateOf("") }

            SecurePassTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (authFailed) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(4.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = errorMessage,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Button(onClick = {
                                authFailed = false
                                startBiometricPrompt(
                                    onSuccess = { navigateToHomeScreen() },
                                    onFailure = {
                                        authFailed = true
                                        errorMessage = "Authentication failed. Please try again."
                                    },
                                    onError = { msg ->
                                        authFailed = true
                                        errorMessage = msg
                                    }
                                )
                            }) {
                                Text("Use Biometric", fontSize = 18.sp)
                            }
                        }
                    } else {
                        // Start biometric auth when the screen first loads
                        LaunchedEffect(Unit) {
                            startBiometricPrompt(
                                onSuccess = { navigateToHomeScreen() },
                                onFailure = {
                                    authFailed = true
                                    errorMessage = "Authentication failed. Please try again."
                                },
                                onError = { msg ->
                                    authFailed = true
                                    errorMessage = msg
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun startBiometricPrompt(
        onSuccess: () -> Unit,
        onFailure: () -> Unit,
        onError: (String) -> Unit
    ) {
        val executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(
            this,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onSuccess()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    onFailure()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    onError("Authentication error: $errString")
                }
            }
        )

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric Authentication")
            .setSubtitle("Log in using your biometric credential")
            .setConfirmationRequired(false)
            .setNegativeButtonText("Cancel")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    private fun navigateToHomeScreen() {
        setContent {
            SecurePassTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(passwordRepository)
                }
            }
        }
    }
}
