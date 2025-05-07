package com.example.chaintechinterviewtask.presentation.screen

import android.util.Patterns
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.chaintechinterviewtask.data.repository.PasswordRepository
import com.example.chaintechinterviewtask.presentation.common.PasswordTextField
import com.example.chaintechinterviewtask.presentation.common.StandardButton
import com.example.chaintechinterviewtask.presentation.common.StandardTextField
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPasswordScreen(
    passwordRepository: PasswordRepository,
    onPasswordAdded: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    var accountType by remember { mutableStateOf("") }
    var accountName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordConfirm by remember { mutableStateOf("") }

    var accountTypeError by remember { mutableStateOf(false) }
    var usernameError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    var passwordMatchError by remember { mutableStateOf(false) }

    fun validateInputs(): Boolean {
        var isValid = true

        if (accountType.isBlank()) {
            accountTypeError = true
            isValid = false
        } else {
            accountTypeError = false
        }

        if (username.isBlank()) {
            usernameError = true
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
            usernameError = true
            isValid = false
        } else {
            usernameError = false
        }

        if (password.isBlank() || password.length < 8) {
            passwordError = true
            isValid = false
        } else {
            passwordError = false
        }

        if (password != passwordConfirm) {
            passwordMatchError = true
            isValid = false
        } else {
            passwordMatchError = false
        }

        return isValid
    }


    suspend fun savePassword() {
        if (!validateInputs()) {
            return
        }

        try {
            passwordRepository.addPassword(
                accountType = accountType,
                accountName = accountName,
                username = username,
                password = password
            )
            onPasswordAdded()
        } catch (e: Exception) {
            scope.launch {
                snackbarHostState.showSnackbar("Failed to save password: ${e.message}")
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            StandardTextField(
                value = accountType,
                onValueChange = { accountType = it },
                label = "Account Type (e.g., Google, Facebook)",
                isError = accountTypeError,
                errorMessage = "Account type is required"
            )

            Spacer(modifier = Modifier.height(8.dp))

            StandardTextField(
                value = accountName,
                onValueChange = { accountName = it },
                label = "Account Name (Optional)"
            )

            Spacer(modifier = Modifier.height(8.dp))

            StandardTextField(
                value = username,
                onValueChange = { username = it },
                label = "Username/Email",
                isError = usernameError,
                errorMessage = when {
                    username.isBlank() -> "Username is required"
                    !Patterns.EMAIL_ADDRESS.matcher(username).matches() -> "Enter a valid email address"
                    else -> ""
                }            )

            Spacer(modifier = Modifier.height(8.dp))

            PasswordTextField(
                password = password,
                onPasswordChange = { password = it },
                label = "Password",
                isError = passwordError || passwordMatchError,
                errorMessage = when {
                    passwordError && password.length < 8 -> "Password must be at least 8 characters"
                    passwordError -> "Password is required"
                    passwordMatchError -> "Passwords don't match"
                    else -> ""
                },
                imeAction = ImeAction.Next
            )

            Spacer(modifier = Modifier.height(8.dp))

            PasswordTextField(
                password = passwordConfirm,
                onPasswordChange = { passwordConfirm = it },
                label = "Confirm Password",
                isError = passwordMatchError,
                errorMessage = if (passwordMatchError) "Passwords don't match" else "",
                imeAction = ImeAction.Done,
                onImeAction = {
                    focusManager.clearFocus()
                    scope.launch { savePassword() }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            StandardButton(
                text = "Add New Account",
                onClick = {
                    scope.launch { savePassword() }
                }
            )
        }
    }
}