package com.synapse.social.studioasinc.shared.domain.usecase.post

import com.synapse.social.studioasinc.shared.domain.repository.PostActionsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Clock

class GetTimeCapsuleStatusUseCase(
    private val repository: PostActionsRepository
) {
    operator fun invoke(unlocksAt: Long): Long {
        val now = Clock.System.now().toEpochMilliseconds()
        return (unlocksAt - now).coerceAtLeast(0L)
    }

    fun isLocked(unlocksAt: Long): Boolean {
        val now = Clock.System.now().toEpochMilliseconds()
        return now < unlocksAt
    }
}
