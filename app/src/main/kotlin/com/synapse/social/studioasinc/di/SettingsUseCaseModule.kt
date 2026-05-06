package com.synapse.social.studioasinc.di

import com.synapse.social.studioasinc.shared.domain.repository.SettingsRepository
import com.synapse.social.studioasinc.shared.domain.usecase.settings.GetContextualHeroCardsUseCase
import com.synapse.social.studioasinc.shared.domain.usecase.settings.SearchSettingsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SettingsUseCaseModule {

    @Provides
    @Singleton
    fun provideSearchSettingsUseCase(
        settingsRepository: SettingsRepository
    ): SearchSettingsUseCase = SearchSettingsUseCase(settingsRepository)

    @Provides
    @Singleton
    fun provideGetContextualHeroCardsUseCase(
        settingsRepository: SettingsRepository
    ): GetContextualHeroCardsUseCase = GetContextualHeroCardsUseCase(settingsRepository)
}
