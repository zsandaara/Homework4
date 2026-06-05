package com.example.homework4.data.repository

import com.example.homework4.domain.Post

interface PostRepository {
    suspend fun getPosts(userId: Int?): List<Post>
    suspend fun getPost(id: Int): Post
}