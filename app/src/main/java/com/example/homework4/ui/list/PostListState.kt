package com.example.homework4.ui.list

import com.example.homework4.domain.Post

sealed class PostListState {
    object Loading : PostListState()
    data class Success(val posts: List<Post>, val favoriteIds: Set<Int>) : PostListState()
    data class Error(val message: String) : PostListState()
    object Empty : PostListState()
}