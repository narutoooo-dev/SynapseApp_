package com.synapse.social.studioasinc.shared.core.network

import kotlin.test.Test
import kotlin.test.assertEquals

class SupabaseClientTest {

    private val baseUrl = "https://example.supabase.co"

    @Test
    fun testConstructStorageUrl_HappyPath() {
        val bucket = "avatars"
        val path = "user123.png"
        val expected = "$baseUrl/storage/v1/object/public/$bucket/$path"

        val actual = SupabaseClient.constructStorageUrlInternal(baseUrl, bucket, path)

        assertEquals(expected, actual)
    }

    @Test
    fun testConstructStorageUrl_WithLeadingSlash() {
        val bucket = "posts"
        val path = "/images/post1.jpg"
        val expected = "$baseUrl/storage/v1/object/public/$bucket/images/post1.jpg"

        val actual = SupabaseClient.constructStorageUrlInternal(baseUrl, bucket, path)

        assertEquals(expected, actual)
    }

    @Test
    fun testConstructStorageUrl_AlreadyFullHttpUrl() {
        val bucket = "any"
        val path = "http://external.com/image.png"

        val actual = SupabaseClient.constructStorageUrlInternal(baseUrl, bucket, path)

        assertEquals(path, actual)
    }

    @Test
    fun testConstructStorageUrl_AlreadyFullHttpsUrl() {
        val bucket = "any"
        val path = "https://external.com/image.png"

        val actual = SupabaseClient.constructStorageUrlInternal(baseUrl, bucket, path)

        assertEquals(path, actual)
    }

    @Test
    fun testConstructStorageUrl_NestedPath() {
        val bucket = "docs"
        val path = "folder/subfolder/file.pdf"
        val expected = "$baseUrl/storage/v1/object/public/$bucket/$path"

        val actual = SupabaseClient.constructStorageUrlInternal(baseUrl, bucket, path)

        assertEquals(expected, actual)
    }
}
