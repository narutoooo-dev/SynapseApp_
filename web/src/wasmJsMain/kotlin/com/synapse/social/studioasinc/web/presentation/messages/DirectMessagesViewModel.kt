package com.synapse.social.studioasinc.web.presentation.messages

import com.synapse.social.studioasinc.shared.domain.model.chat.Conversation
import com.synapse.social.studioasinc.shared.domain.usecase.chat.GetConversationsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DirectMessagesViewModel(
    private val getConversationsUseCase: GetConversationsUseCase
) {
    private val _uiState = MutableStateFlow(DirectMessagesUiState())
    val uiState: StateFlow<DirectMessagesUiState> = _uiState.asStateFlow()

    suspend fun loadConversations() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        val result = getConversationsUseCase()

        _uiState.update { it.copy(isLoading = false) }

        if (result.isSuccess) {
            _uiState.update { it.copy(conversations = result.getOrNull() ?: emptyList()) }
        } else {
            _uiState.update { it.copy(errorMessage = result.exceptionOrNull()?.message ?: "Failed to load messages") }
        }
    }
}

data class DirectMessagesUiState(
    val conversations: List<Conversation> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
