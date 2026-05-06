package com.synapse.social.studioasinc.web.presentation.explore

import com.synapse.social.studioasinc.shared.domain.model.SearchPost
import com.synapse.social.studioasinc.shared.domain.model.SearchAccount
import com.synapse.social.studioasinc.shared.domain.usecase.search.GetSuggestedAccountsUseCase
import com.synapse.social.studioasinc.shared.domain.usecase.search.SearchPostsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ExploreViewModel(
    private val searchPostsUseCase: SearchPostsUseCase,
    private val getSuggestedAccountsUseCase: GetSuggestedAccountsUseCase
) {
    private val _uiState = MutableStateFlow(ExploreUiState())
    val uiState: StateFlow<ExploreUiState> = _uiState.asStateFlow()

    suspend fun loadExploreData() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        val postsResult = searchPostsUseCase("")
        val accountsResult = getSuggestedAccountsUseCase("")

        _uiState.update {
            it.copy(
                isLoading = false,
                posts = postsResult.getOrNull() ?: emptyList(),
                suggestedAccounts = accountsResult.getOrNull() ?: emptyList(),
                errorMessage = if (postsResult.isFailure && accountsResult.isFailure) "Failed to load explore data" else null
            )
        }
    }
}

data class ExploreUiState(
    val posts: List<SearchPost> = emptyList(),
    val suggestedAccounts: List<SearchAccount> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
