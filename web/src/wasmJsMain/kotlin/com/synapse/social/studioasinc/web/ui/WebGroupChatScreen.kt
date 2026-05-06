package com.synapse.social.studioasinc.web.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.synapse.social.studioasinc.web.presentation.messages.GroupChatViewModel
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun WebGroupChatScreen() {
    val viewModel: GroupChatViewModel = koinInject()
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    val chatId = "demo-group-id"

    LaunchedEffect(Unit) {
        viewModel.loadMessages(chatId)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh, shape = MaterialTheme.shapes.small)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Synapse Dev Team",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "8 members",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Row {
                IconButton(onClick = {}) { Text("📞") }
                IconButton(onClick = {}) { Text("📹") }
                IconButton(onClick = {}) { Text("ℹ️") }
            }
        }
        Divider(color = MaterialTheme.colorScheme.outlineVariant)

        // Message List
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.messages.isEmpty()) {
                Text(
                    "No messages yet",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    reverseLayout = true
                ) {
                    items(uiState.messages) { message ->
                        val isMine = message.senderId == "current-user-id" // Assuming dummy check
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start
                        ) {
                            if (!isMine) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(MaterialTheme.colorScheme.surfaceContainerHigh, shape = MaterialTheme.shapes.small)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            Column(horizontalAlignment = if (isMine) Alignment.End else Alignment.Start) {
                                if (!isMine) {
                                    Text(
                                        text = message.senderId, // Usually sender name
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .background(
                                            color = if (isMine) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerHigh,
                                            shape = MaterialTheme.shapes.medium
                                        )
                                        .padding(12.dp)
                                ) {
                                    Text(
                                        text = message.content,
                                        color = if (isMine) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Message Input
        Divider(color = MaterialTheme.colorScheme.outlineVariant)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {}) { Text("📎") }
            OutlinedTextField(
                value = uiState.messageInput,
                onValueChange = { viewModel.updateMessageInput(it) },
                placeholder = { Text("Type a message...") },
                modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                shape = MaterialTheme.shapes.large,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                )
            )
            IconButton(
                onClick = { scope.launch { viewModel.sendMessage(chatId) } },
                enabled = !uiState.isSending && uiState.messageInput.isNotBlank()
            ) {
                if (uiState.isSending) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    Text("✈️", color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}
