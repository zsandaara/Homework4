package com.example.homework4.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.homework4.data.repository.FavoriteRepository
import com.example.homework4.data.repository.PostRepository
import com.example.homework4.domain.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostDetailViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val favoriteRepository: FavoriteRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val postId: Int = checkNotNull(savedStateHandle["id"])

    private val _uiState = MutableStateFlow<PostDetailState>(PostDetailState.Loading)
    val uiState: StateFlow<PostDetailState> = _uiState.asStateFlow()

    init {
        loadPost()
    }

    private fun loadPost() {
        viewModelScope.launch {
            _uiState.value = PostDetailState.Loading
            try {
                val post = postRepository.getPost(postId)
                val favoriteIds = favoriteRepository.getFavoriteIds().first()
                val isFavorite = postId in favoriteIds
                _uiState.value = PostDetailState.Success(post, isFavorite)
            } catch (e: Exception) {
                _uiState.value = PostDetailState.Error(e.message ?: "Неизвестная ошибка")
            }
        }
    }

    fun retry() {
        loadPost()
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState is PostDetailState.Success) {
                if (currentState.isFavorite) {
                    favoriteRepository.removeFavorite(postId)
                } else {
                    favoriteRepository.addFavorite(postId)
                }
                // Обновляем состояние после изменения избранного
                loadPost()
            }
        }
    }
}