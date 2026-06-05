package com.example.homework4.ui.favorites

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
class FavoritesViewModel @Inject constructor(
    private val favoriteRepository: FavoriteRepository,
    private val postRepository: PostRepository
) : ViewModel() {

    private val _favoritePosts = MutableStateFlow<List<Post>>(emptyList())
    val favoritePosts: StateFlow<List<Post>> = _favoritePosts.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadFavorites()
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Получаем Flow и собираем его в список ID
                val favoriteIds = favoriteRepository.getFavoriteIds().first()
                val posts = favoriteIds.mapNotNull { postId ->
                    try {
                        postRepository.getPost(postId)
                    } catch (e: Exception) {
                        null  // если пост не найден, пропускаем
                    }
                }
                _favoritePosts.value = posts
            } catch (e: Exception) {
                _favoritePosts.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
}