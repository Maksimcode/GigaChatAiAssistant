package com.example.gigachataiassistant.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gigachataiassistant.ui.chats.ChatListViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    viewModel: ChatListViewModel,
    onOpenChat: (String) -> Unit,
    onOpenProfile: () -> Unit,
    onOpenImages: () -> Unit
) {
    val searchInput by viewModel.searchInput.collectAsState()
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Чаты") })
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(
                    value = searchInput,
                    onValueChange = viewModel::onSearchInputChange,
                    modifier = Modifier.weight(1f),
                    label = { Text("Поиск") },
                    singleLine = true,
                )
                Button(onClick = { viewModel.applySearch() }) {
                    Text("Поиск")
                }
            }
            Button(
                onClick = {
                    scope.launch {
                        val id = viewModel.createNewChat()
                        onOpenChat(id)
                    }
                },
            ) {
                Text("Новый чат")
            }
            Button(onClick = onOpenProfile) {
                Text("Профиль")
            }
            Button(onClick = onOpenImages) {
                Text("Изображения")
            }
        }
    }
}