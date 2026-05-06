package com.synapse.social.studioasinc.web.di

import com.synapse.social.studioasinc.shared.data.repository.SupabaseAuthRepository
import com.synapse.social.studioasinc.shared.data.repository.SearchRepositoryImpl
import com.synapse.social.studioasinc.shared.domain.repository.AuthRepository
import com.synapse.social.studioasinc.shared.domain.repository.ISearchRepository
import com.synapse.social.studioasinc.shared.domain.usecase.auth.SignInUseCase
import com.synapse.social.studioasinc.shared.domain.usecase.search.SearchPostsUseCase
import com.synapse.social.studioasinc.shared.domain.usecase.search.GetSuggestedAccountsUseCase
import com.synapse.social.studioasinc.shared.domain.usecase.chat.GetConversationsUseCase
import com.synapse.social.studioasinc.shared.domain.usecase.chat.GetMessagesUseCase
import com.synapse.social.studioasinc.shared.domain.usecase.chat.SendMessageUseCase
import com.synapse.social.studioasinc.web.presentation.auth.AuthViewModel
import com.synapse.social.studioasinc.web.presentation.home.FeedViewModel
import com.synapse.social.studioasinc.web.presentation.explore.ExploreViewModel
import com.synapse.social.studioasinc.web.presentation.messages.DirectMessagesViewModel
import com.synapse.social.studioasinc.web.presentation.messages.GroupChatViewModel
import com.synapse.social.studioasinc.web.presentation.post.CreatePostViewModel
import org.koin.dsl.module

val webModule = module {
    single<AuthRepository> { SupabaseAuthRepository() }
    single { SignInUseCase(get()) }
    factory { AuthViewModel(get()) }

    single<ISearchRepository> { SearchRepositoryImpl() }
    single { SearchPostsUseCase(get()) }
    single { GetSuggestedAccountsUseCase(get()) }

    // Assuming ChatRepository is provided in shared module
    single { GetConversationsUseCase(get()) }
    single { GetMessagesUseCase(get()) }
    single { SendMessageUseCase(get()) }

    factory { FeedViewModel(get()) }
    factory { ExploreViewModel(get(), get()) }
    factory { DirectMessagesViewModel(get()) }
    factory { GroupChatViewModel(get(), get()) }
    factory { CreatePostViewModel() }
}
