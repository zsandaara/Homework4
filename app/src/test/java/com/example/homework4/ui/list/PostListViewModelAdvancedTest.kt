package com.example.homework4.ui.list

import com.example.homework4.data.repository.FavoriteRepository
import com.example.homework4.data.repository.PostRepository
import com.example.homework4.domain.Post
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay  // ← ДОБАВИТЬ
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*
import java.io.IOException

@ExperimentalCoroutinesApi
class PostListViewModelAdvancedTest {

    private lateinit var viewModel: PostListViewModel
    private lateinit var postRepository: PostRepository
    private lateinit var favoriteRepository: FavoriteRepository
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        postRepository = mock()
        favoriteRepository = mock()

        runBlocking {
            whenever(favoriteRepository.getFavoriteIds()).thenReturn(flowOf(emptySet()))
        }

        viewModel = PostListViewModel(postRepository, favoriteRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `retry actually triggers new network request`() = runTest {
        var callCount = 0

        whenever(postRepository.getPosts(anyOrNull())).thenAnswer {
            callCount++
            if (callCount == 1) throw IOException("Network error")
            listOf(Post(id = 1, userId = 1, title = "Success", body = ""))
        }
        whenever(favoriteRepository.getFavoriteIds()).thenReturn(flowOf(emptySet()))

        viewModel.loadPosts(null)
        delay(100)
        viewModel.retry()
        delay(100)

        assertThat(callCount).isEqualTo(2)
    }

    @Test
    fun `empty result returns Empty state not Success with empty list`() = runTest {
        whenever(postRepository.getPosts(anyOrNull())).thenReturn(emptyList())
        whenever(favoriteRepository.getFavoriteIds()).thenReturn(flowOf(emptySet()))

        viewModel.loadPosts(null)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(PostListState.Empty::class.java)
        assertThat(state).isNotInstanceOf(PostListState.Success::class.java)
    }
}