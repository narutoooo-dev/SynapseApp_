package com.synapse.social.studioasinc.shared.data.mapper

import com.synapse.social.studioasinc.shared.data.dto.CommentDto
import com.synapse.social.studioasinc.shared.domain.model.Comment
import kotlinx.datetime.Instant

object CommentMapper {
    fun toDomain(dto: CommentDto): Comment {
        return Comment(
            id = dto.id,
            postId = dto.postId,
            authorId = dto.authorId,
            text = dto.content,
            timestamp = try {
                Instant.parse(dto.createdAt).toEpochMilliseconds()
            } catch (e: Exception) {
                0L
            },
            likesCount = dto.likesCount,
            repliesCount = dto.repliesCount,
            parentCommentId = dto.parentId,
            isDeleted = false
        )
    }

    fun toDomainList(dtos: List<CommentDto>): List<Comment> = dtos.map { toDomain(it) }
}
