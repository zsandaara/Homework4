package com.example.homework4.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.homework4.data.repository.FavoriteRepository
import com.example.homework4.data.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostListViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<PostListState>(PostListState.Loading)
    val uiState: StateFlow<PostListState> = _uiState.asStateFlow()

    private var allPosts: List<com.example.homework4.domain.Post> = emptyList()
    private var favoriteIds: Set<Int> = emptySet()
    private var currentUserId: Int? = null

    init {
        loadFavorites()
        loadPosts()
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            favoriteRepository.getFavoriteIds().collect { ids ->
                favoriteIds = ids.toSet()
                applyFilter()
            }
        }
    }

    fun loadPosts(userId: Int? = null) {
        currentUserId = userId
        viewModelScope.launch {
            _uiState.value = PostListState.Loading
            try {
                allPosts = postRepository.getPosts(userId)
                applyFilter()
            } catch (e: Exception) {
                _uiState.value = PostListState.Error(e.message ?: "Неизвестная ошибка")
            }
        }
    }

    private fun applyFilter() {
        // Добавляем проверку, что загрузка завершена, прежде чем обновлять состояние
        if (allPosts.isEmpty()) {
            _uiState.value = PostListState.Empty
        } else {
            _uiState.value = PostListState.Success(allPosts, favoriteIds)
        }
    }

    fun retry() {
        loadPosts(currentUserId)
    }

    fun toggleFavorite(postId: Int) {
        viewModelScope.launch {
            if (favoriteIds.contains(postId)) {
                favoriteRepository.removeFavorite(postId)
            } else {
                favoriteRepository.addFavorite(postId)
            }
        }
    }

    // ========== ДОБАВЛЕНО ДЛЯ ТЕСТИРОВАНИЯ ==========

    /**
     * Метод для тестирования - позволяет напрямую установить избранные ID
     * Используется только в тестах
     */
    internal fun setFavoriteIdsForTesting(ids: Set<Int>) {
        favoriteIds = ids
        applyFilter()
    }

    /**
     * Метод для тестирования - позволяет напрямую установить посты
     * Используется только в тестах
     */
    internal fun setAllPostsForTesting(posts: List<com.example.homework4.domain.Post>) {
        allPosts = posts
        applyFilter()
    }
}