package com.example.homework4.domain

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {
    @Insert
    suspend fun insert(post: Post)

    @Insert
    suspend fun insertAll(posts: List<Post>)

    @Update
    suspend fun update(post: Post)

    @Delete
    suspend fun delete(post: Post)

    @Query("SELECT * FROM posts")
    fun getAllPosts(): Flow<List<Post>>

    @Query("SELECT * FROM posts WHERE id = :id")
    suspend fun getPostById(id: Int): Post?

    @Query("SELECT * FROM posts WHERE userId = :userId")
    fun getPostsByUserId(userId: Int): Flow<List<Post>>

    @Query("DELETE FROM posts")
    suspend fun deleteAll()
}