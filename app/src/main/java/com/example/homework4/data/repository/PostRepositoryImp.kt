package com.example.homework4.data.repository

import com.example.homework4.data.remote.ApiService
import com.example.homework4.data.remote.toDomain
import com.example.homework4.domain.Post
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : PostRepository {

    override suspend fun getPosts(userId: Int?): List<Post> {
        return apiService.getPosts(userId).map { it.toDomain() }
    }

    override suspend fun getPost(id: Int): Post {
        return apiService.getPost(id).toDomain()
    }
}