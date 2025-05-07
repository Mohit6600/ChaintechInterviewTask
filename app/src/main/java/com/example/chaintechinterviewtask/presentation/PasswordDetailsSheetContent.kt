package com.example.chaintechinterviewtask.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FileCopy
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chaintechinterviewtask.data.repository.PasswordRepository
import com.example.chaintechinterviewtask.domain.Password
import com.example.chaintechinterviewtask.presentation.common.PasswordTextField
import com.example.chaintechinterviewtask.presentation.common.StandardTextField
import kotlinx.coroutines.launch

@Composable
fun PasswordDetailsSheetContent(
    passwordId: Long,
    passwordRepository: PasswordRepository,
    onDismiss: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val clipboardManager: ClipboardManager = LocalClipboardManager.current

    var password by remember { mutableStateOf<Password?>(null) }
    var decryptedPassword by remember { mutableStateOf<String?>(null) }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }

    LaunchedEffect(passwordId) {
        password = passwordRepository.getPasswordById(passwordId)
    }

    fun copyToClipboard(text: String, label: String) {
        clipboardManager.setText(AnnotatedString(text))
        scope.launch {
            snackbarHostState.showSnackbar("$label copied to clipboard")
        }
    }

    fun togglePasswordVisibility() {
        if (isPasswordVisible) {
            isPasswordVisible = false
        } else {
            password?.let {
                decryptedPassword = passwordRepository.decryptPassword(it.encryptedPassword, it.iv)
                isPasswordVisible = true
            }
        }
    }

    suspend fun deletePassword() {
        try {
            passwordRepository.deletePassword(passwordId)
            onDismiss()
        } catch (e: Exception) {
            scope.launch {
                snackbarHostState.showSnackbar("Failed to delete password: ${e.message}")
            }
        }
    }

    password?.let { pwd ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Password Details",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    DetailField("Account Type", pwd.accountType) {
                        copyToClipboard(pwd.accountType, "Account type")
                    }

                    if (pwd.accountName.isNotBlank()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        DetailField("Account Name", pwd.accountName) {
                            copyToClipboard(pwd.accountName, "Account name")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    DetailField("Username/Email", pwd.username) {
                        copyToClipboard(pwd.username, "Username")
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    PasswordDetailField(
                        label = "Password",
                        value = if (isPasswordVisible) decryptedPassword ?: "" else "••••••••••",
                        isVisible = isPasswordVisible,
                        onToggleVisibility = ::togglePasswordVisibility,
                        onCopy = {
                            if (!isPasswordVisible) togglePasswordVisibility()
                            decryptedPassword?.let {
                                copyToClipboard(it, "Password")
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp), // Optional padding to avoid screen edge
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = { showEditDialog = true },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp), // Increased height
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text(
                        "Edit", color = Color.White, fontSize = 18.sp // Increased text size
                    )
                }

                Spacer(modifier = Modifier.width(16.dp)) // Space between the two buttons

                Button(
                    onClick = { showDeleteConfirmDialog = true },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp), // Increased height
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text(
                        "Delete", color = Color.White, fontSize = 18.sp // Increased text size
                    )
                }
            }

        }

        if (showDeleteConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirmDialog = false },
                title = { Text("Delete Password") },
                text = { Text("Are you sure you want to delete this password? This action cannot be undone.") },
                confirmButton = {
                    Button(onClick = {
                        showDeleteConfirmDialog = false
                        scope.launch { deletePassword() }
                    }) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirmDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        if (showEditDialog) {
            var editedAccountType by remember { mutableStateOf(pwd.accountType) }
            var editedAccountName by remember { mutableStateOf(pwd.accountName) }
            var editedUsername by remember { mutableStateOf(pwd.username) }
            var editedPassword by remember { mutableStateOf("") }
            var confirmPassword by remember { mutableStateOf("") }
            var passwordMismatch by remember { mutableStateOf(false) }

            AlertDialog(
                onDismissRequest = { showEditDialog = false },
                title = { Text("Edit Password") },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(bottom = 16.dp)
                    ) {
                        StandardTextField(
                            value = editedAccountType,
                            onValueChange = { editedAccountType = it },
                            label = "Account Type"
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        StandardTextField(
                            value = editedAccountName,
                            onValueChange = { editedAccountName = it },
                            label = "Account Name"
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        StandardTextField(
                            value = editedUsername,
                            onValueChange = { editedUsername = it },
                            label = "Username"
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        PasswordTextField(
                            password = editedPassword,
                            onPasswordChange = { editedPassword = it },
                            label = "New Password",
                            imeAction = ImeAction.Next,
                            )
                        Spacer(modifier = Modifier.height(8.dp))
                        PasswordTextField(
                            password = confirmPassword,
                            onPasswordChange = { confirmPassword = it },
                            label = "Confirm Password",
                            isError = passwordMismatch,
                            errorMessage = "Passwords do not match",
                            imeAction = ImeAction.Next,

                            )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        if (editedPassword != confirmPassword) {
                            passwordMismatch = true
                        } else {
                            passwordMismatch = false
                            scope.launch {
                                try {
                                    (if (editedPassword.isNotEmpty()) editedPassword else null)?.let {
                                        passwordRepository.updatePassword(
                                            id = pwd.id,
                                            accountType = editedAccountType,
                                            accountName = editedAccountName,
                                            username = editedUsername,
                                            newPassword = it
                                        )
                                    }
                                    showEditDialog = false
                                    onDismiss()
                                } catch (e: Exception) {
                                    snackbarHostState.showSnackbar("Failed to update: ${e.message}")
                                }
                            }
                        }
                    }) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showEditDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }


    }
}

@Composable
fun DetailField(
    label: String,
    value: String,
    onCopy: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )

            IconButton(
                onClick = onCopy,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.FileCopy,
                    contentDescription = "Copy to clipboard"
                )
            }
        }
    }
}

@Composable
fun PasswordDetailField(
    label: String,
    value: String,
    isVisible: Boolean,
    onToggleVisibility: () -> Unit,
    onCopy: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.weight(1f)
            )

            IconButton(
                onClick = onToggleVisibility
            ) {
                Icon(
                    imageVector = if (isVisible)
                        Icons.Outlined.VisibilityOff
                    else
                        Icons.Outlined.Visibility,
                    contentDescription = if (isVisible) "Hide password" else "Show password"
                )
            }

            IconButton(
                onClick = onCopy
            ) {
                Icon(
                    imageVector = Icons.Outlined.FileCopy,

                    contentDescription = "Copy to clipboard"
                )
            }
        }
    }
}