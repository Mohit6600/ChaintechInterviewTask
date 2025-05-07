package com.example.chaintechinterviewtask.presentation.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun StandardTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {}
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction
        ),
        keyboardActions = KeyboardActions(
            onDone = { onImeAction() }
        ),
        isError = isError,
        supportingText = if (isError) {
            { Text(errorMessage, color = MaterialTheme.colorScheme.error) }
        } else null,
        singleLine = true
    )
}

@Composable
fun PasswordTextField(
    password: String,
    onPasswordChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String = "",
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {}
) {
    var isPasswordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = password,
        onValueChange = onPasswordChange,
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = imeAction
        ),
        keyboardActions = KeyboardActions(
            onDone = { onImeAction() }
        ),
        isError = isError,
        supportingText = if (isError) {
            { Text(errorMessage, color = MaterialTheme.colorScheme.error) }
        } else null,
        singleLine = true,
        visualTransformation = if (isPasswordVisible)
            VisualTransformation.None
        else
            PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                Icon(
                    imageVector = if (isPasswordVisible)
                        Icons.Default.VisibilityOff
                    else
                        Icons.Default.Visibility,
                    contentDescription = if (isPasswordVisible) "Hide password" else "Show password"
                )
            }
        }
    )
}

@Composable
fun StandardButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = onClick,
            enabled = enabled,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black,
                contentColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(text)
        }
    }
}