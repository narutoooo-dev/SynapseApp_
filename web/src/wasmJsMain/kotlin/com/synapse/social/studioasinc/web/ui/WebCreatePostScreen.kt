package com.synapse.social.studioasinc.web.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.synapse.social.studioasinc.web.presentation.post.CreatePostViewModel
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun WebCreatePostScreen() {
    val viewModel: CreatePostViewModel = koinInject()
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { /* Close */ }) {
                    Text("✖", color = MaterialTheme.colorScheme.onSurface)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Create Post",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Button(
                onClick = { scope.launch { viewModel.createPost() } },
                enabled = !uiState.isPosting && uiState.content.isNotBlank()
            ) {
                if (uiState.isPosting) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                } else {
                    Text("Post")
                }
            }
        }
        Divider(color = MaterialTheme.colorScheme.outlineVariant)

        // Post Input Area
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh, shape = MaterialTheme.shapes.small)
            )
            Spacer(modifier = Modifier.width(16.dp))
            OutlinedTextField(
                value = uiState.content,
                onValueChange = { viewModel.updatePostContent(it) },
                placeholder = { Text("What's on your mind?") },
                modifier = Modifier.weight(1f).fillMaxHeight(0.3f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )
        }

        Divider(color = MaterialTheme.colorScheme.outlineVariant)

        // Media Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {}) { Text("🖼️") }
            IconButton(onClick = {}) { Text("📊") }
            IconButton(onClick = {}) { Text("😀") }
        }
    }
}
