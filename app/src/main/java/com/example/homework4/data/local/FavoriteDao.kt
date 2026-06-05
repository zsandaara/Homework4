package com.example.homework4.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FavoriteDao {
    @Insert
    suspend fun insert(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE postId = :postId")
    suspend fun delete(postId: Int)

    @Query("SELECT postId FROM favorites")
    suspend fun getAllIds(): List<Int>
}