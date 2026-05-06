package com.synapse.social.studioasinc.web.presentation.messages

import com.synapse.social.studioasinc.shared.domain.model.chat.Message
import com.synapse.social.studioasinc.shared.domain.usecase.chat.GetMessagesUseCase
import com.synapse.social.studioasinc.shared.domain.usecase.chat.SendMessageUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GroupChatViewModel(
    private val getMessagesUseCase: GetMessagesUseCase,
    private val sendMessageUseCase: SendMessageUseCase
) {
    private val _uiState = MutableStateFlow(GroupChatUiState())
    val uiState: StateFlow<GroupChatUiState> = _uiState.asStateFlow()

    fun updateMessageInput(text: String) {
        _uiState.update { it.copy(messageInput = text) }
    }

    suspend fun loadMessages(chatId: String) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        val result = getMessagesUseCase(chatId)

        _uiState.update { it.copy(isLoading = false) }

        if (result.isSuccess) {
            _uiState.update { it.copy(messages = result.getOrNull() ?: emptyList()) }
        } else {
            _uiState.update { it.copy(errorMessage = result.exceptionOrNull()?.message ?: "Failed to load messages") }
        }
    }

    suspend fun sendMessage(chatId: String) {
        val text = _uiState.value.messageInput
        if (text.isBlank()) return

        _uiState.update { it.copy(isSending = true) }
        val result = sendMessageUseCase(chatId, text)

        if (result.isSuccess) {
            _uiState.update { it.copy(messageInput = "", isSending = false) }
            loadMessages(chatId) // Reload to get new message
        } else {
            _uiState.update {
                it.copy(
                    isSending = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "Failed to send message"
                )
            }
        }
    }
}

data class GroupChatUiState(
    val messages: List<Message> = emptyList(),
    val messageInput: String = "",
    val isLoading: Boolean = true,
    val isSending: Boolean = false,
    val errorMessage: String? = null
)
