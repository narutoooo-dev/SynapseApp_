package com.synapse.social.studioasinc.shared.data.repository

import com.synapse.social.studioasinc.shared.data.dto.CommentDto
import com.synapse.social.studioasinc.shared.data.mapper.CommentMapper
import com.synapse.social.studioasinc.shared.domain.model.Comment
import com.synapse.social.studioasinc.shared.domain.repository.CommentRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class SupabaseCommentRepository(
    private val supabaseClient: SupabaseClient
) : CommentRepository {

    override suspend fun getComments(postId: String, parentId: String?): Result<List<Comment>> = withContext(Dispatchers.IO) {
        runCatching {
            val response = supabaseClient.from("comments").select {
                filter {
                    eq("post_id", postId)
                    if (parentId == null) {
                        filter("parent_id", FilterOperator.IS, "null")
                    } else {
                        eq("parent_id", parentId)
                    }
                }
                order("created_at", Order.ASCENDING)
            }.decodeList<CommentDto>()
            CommentMapper.toDomainList(response)
        }
    }

    override suspend fun addComment(postId: String, content: String, parentId: String?, mediaUrl: String?): Result<Comment> = withContext(Dispatchers.IO) {
        runCatching {
            val userId = supabaseClient.auth.currentUserOrNull()?.id ?: throw Exception("User not authenticated")
            val commentDto = supabaseClient.from("comments").insert(
                mapOf(
                    "post_id" to postId,
                    "author_id" to userId,
                    "parent_id" to parentId,
                    "content" to content,
                    "media_url" to mediaUrl
                )
            ) {
                select()
            }.decodeSingle<CommentDto>()
            CommentMapper.toDomain(commentDto)
        }
    }

    override suspend fun deleteComment(commentId: String): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            supabaseClient.from("comments").delete {
                filter { eq("id", commentId) }
            }
            Unit
        }
    }
}
