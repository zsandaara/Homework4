package com.example.homework4.ui.detail

import com.example.homework4.domain.Post

sealed class PostDetailState {
    object Loading : PostDetailState()
    data class Success(val post: Post, val isFavorite: Boolean = false) : PostDetailState()
    data class Error(val message: String) : PostDetailState()
}