package com.example.homework4.integration

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.homework4.domain.Post
import com.example.homework4.domain.PostDao
import com.example.homework4.domain.PostDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import junit.framework.TestCase.assertEquals

@RunWith(AndroidJUnit4::class)
class RepositoryRoomIntegrationTest {

    private lateinit var database: PostDatabase
    private lateinit var dao: PostDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            PostDatabase::class.java
        ).allowMainThreadQueries().build()

        dao = database.postDao()
    }

    @After
    fun cleanup() {
        database.close()
    }

    @Test
    fun insertThenReadReturnsCorrectData() = runTest {
        val post = Post(id = 1, userId = 1, title = "Test Title", body = "Test Body")

        dao.insert(post)
        val result = dao.getAllPosts().first()

        assertEquals(1, result.size)
        assertEquals(post.id, result[0].id)
        assertEquals(post.title, result[0].title)
    }
}