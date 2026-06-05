package com.example.homework4.data.repository

import com.example.homework4.data.local.FavoriteDao
import com.example.homework4.data.local.FavoriteEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoriteRepository @Inject constructor(
    private val dao: FavoriteDao
) {
    private val _favoriteIds = MutableStateFlow<Set<Int>>(emptySet())
    val favoriteIds: Flow<Set<Int>> = _favoriteIds.asStateFlow()

    suspend fun addFavorite(postId: Int) {
        dao.insert(FavoriteEntity(postId))
        refreshFavorites()
    }

    suspend fun removeFavorite(postId: Int) {
        dao.delete(postId)
        refreshFavorites()
    }

    suspend fun getFavoriteIds(): Flow<Set<Int>> {
        refreshFavorites()
        return favoriteIds
    }

    suspend fun isFavorite(postId: Int): Boolean {
        refreshFavorites()
        return _favoriteIds.value.contains(postId)
    }

    private suspend fun refreshFavorites() {
        val ids = dao.getAllIds().toSet()
        _favoriteIds.value = ids
    }
}