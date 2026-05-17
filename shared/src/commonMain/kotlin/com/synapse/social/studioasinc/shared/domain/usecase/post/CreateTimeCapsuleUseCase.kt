package com.synapse.social.studioasinc.shared.domain.usecase.post

import com.synapse.social.studioasinc.shared.domain.repository.PostActionsRepository
import kotlinx.datetime.Clock

class CreateTimeCapsuleUseCase(
    private val repository: PostActionsRepository
) {
    fun validateUnlockDate(unlocksAt: Long): Boolean {
        val now = Clock.System.now().toEpochMilliseconds()
        return unlocksAt > now
    }
}
