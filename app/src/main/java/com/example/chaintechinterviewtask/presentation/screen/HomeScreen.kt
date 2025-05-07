package com.example.chaintechinterviewtask.presentation.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.chaintechinterviewtask.data.repository.PasswordRepository
import com.example.chaintechinterviewtask.domain.Password
import com.example.chaintechinterviewtask.presentation.common.PasswordCard
import com.example.chaintechinterviewtask.presentation.PasswordDetailsSheetContent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    passwordRepository: PasswordRepository
) {
    var passwords by remember { mutableStateOf<List<Password>>(emptyList()) }
    var selectedPasswordId by remember { mutableStateOf<Long?>(null) }
    var showAddPasswordSheet by remember { mutableStateOf(false) }

    val detailSheetState = rememberModalBottomSheetState()
    val addSheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        passwords = passwordRepository.getAllPasswords()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Password Manager",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showAddPasswordSheet = true
                    scope.launch { addSheetState.show() }
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Password")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            items(passwords) { password ->
                PasswordCard(
                    password = password,
                    onClick = {
                        selectedPasswordId = password.id
                        scope.launch { detailSheetState.show() }
                    }
                )
            }
        }

        // Password Details Bottom Sheet
        selectedPasswordId?.let { passwordId ->
            ModalBottomSheet(
                onDismissRequest = {
                    selectedPasswordId = null
                    scope.launch { detailSheetState.hide() }
                },
                sheetState = detailSheetState
            ) {
                PasswordDetailsSheetContent(
                    passwordId = passwordId,
                    passwordRepository = passwordRepository,
                    onDismiss = {
                        selectedPasswordId = null
                        scope.launch {
                            detailSheetState.hide()
                            passwords = passwordRepository.getAllPasswords() // Refresh after edit/delete
                        }
                    }
                )
            }
        }

        // Add Password Bottom Sheet
        if (showAddPasswordSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showAddPasswordSheet = false
                    scope.launch { addSheetState.hide() }
                },
                sheetState = addSheetState
            ) {
                AddPasswordScreen(
                    passwordRepository = passwordRepository,
                    onPasswordAdded = {
                        showAddPasswordSheet = false
                        scope.launch {
                            addSheetState.hide()
                            passwords = passwordRepository.getAllPasswords() // Refresh list
                        }
                    },
                    onNavigateBack = {
                        showAddPasswordSheet = false
                        scope.launch { addSheetState.hide() }
                    }
                )
            }
        }
    }
}
