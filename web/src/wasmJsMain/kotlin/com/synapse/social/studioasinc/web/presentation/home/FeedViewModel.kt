package com.synapse.social.studioasinc.web.presentation.home

import com.synapse.social.studioasinc.shared.domain.model.SearchPost
import com.synapse.social.studioasinc.shared.domain.usecase.search.SearchPostsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FeedViewModel(
    private val searchPostsUseCase: SearchPostsUseCase
) {
    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    suspend fun loadFeed() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        val result = searchPostsUseCase("")
        _uiState.update { it.copy(isLoading = false) }

        if (result.isSuccess) {
            _uiState.update { it.copy(posts = result.getOrNull() ?: emptyList()) }
        } else {
            _uiState.update { it.copy(errorMessage = result.exceptionOrNull()?.message ?: "Failed to load feed") }
        }
    }
}

data class FeedUiState(
    val posts: List<SearchPost> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
