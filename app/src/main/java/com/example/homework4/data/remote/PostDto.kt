package com.example.homework4.data.remote

import com.example.homework4.domain.Post
import com.google.gson.annotations.SerializedName

data class PostDto(
    @SerializedName("id") val id: Int,
    @SerializedName("userId") val userId: Int,
    @SerializedName("title") val title: String,
    @SerializedName("body") val body: String
)

fun PostDto.toDomain() = Post(id, userId, title, body)