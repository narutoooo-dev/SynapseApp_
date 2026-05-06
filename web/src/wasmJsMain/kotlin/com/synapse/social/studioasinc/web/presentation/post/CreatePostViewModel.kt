package com.synapse.social.studioasinc.web.presentation.post

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CreatePostViewModel {
    private val _uiState = MutableStateFlow(CreatePostUiState())
    val uiState: StateFlow<CreatePostUiState> = _uiState.asStateFlow()

    fun updatePostContent(content: String) {
        _uiState.update { it.copy(content = content) }
    }

    suspend fun createPost(): Boolean {
        if (_uiState.value.content.isBlank()) return false

        _uiState.update { it.copy(isPosting = true) }

        // Simulating post creation since there's no explicit CreatePostUseCase in /post
        // Typically this would call a usecase. For now, we simulate success.
        kotlinx.coroutines.delay(500)

        _uiState.update { it.copy(content = "", isPosting = false) }
        return true
    }
}

data class CreatePostUiState(
    val content: String = "",
    val isPosting: Boolean = false,
    val errorMessage: String? = null
)
