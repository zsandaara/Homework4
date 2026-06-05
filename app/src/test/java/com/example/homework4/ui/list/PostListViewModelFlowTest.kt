package com.example.homework4.ui.list

import app.cash.turbine.test
import com.example.homework4.data.repository.FavoriteRepository
import com.example.homework4.data.repository.PostRepository
import com.example.homework4.domain.Post
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*
import java.io.IOException

@ExperimentalCoroutinesApi
class PostListViewModelFlowTest {

    private lateinit var viewModel: PostListViewModel
    private lateinit var postRepository: PostRepository
    private lateinit var favoriteRepository: FavoriteRepository
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        postRepository = mock()
        favoriteRepository = mock()

        // Простая настройка моков
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
    fun `state sequence loading to error to loading to success`() = runTest {
        var attempt = 0
        val posts = listOf(
            Post(id = 1, userId = 1, title = "Success Post", body = "Content")
        )

        runBlocking {
            whenever(postRepository.getPosts(anyOrNull())).thenAnswer {
                attempt++
                if (attempt == 1) {
                    throw IOException("Network error")
                }
                posts
            }
            whenever(favoriteRepository.getFavoriteIds()).thenReturn(flowOf(emptySet()))
        }

        viewModel.uiState.test {
            skipItems(1)

            viewModel.loadPosts(null)

            assertThat(awaitItem()).isInstanceOf(PostListState.Loading::class.java)

            val error = awaitItem()
            assertThat(error).isInstanceOf(PostListState.Error::class.java)
            assertThat((error as PostListState.Error).message).contains("Network error")

            viewModel.retry()

            assertThat(awaitItem()).isInstanceOf(PostListState.Loading::class.java)

            val success = awaitItem()
            assertThat(success).isInstanceOf(PostListState.Success::class.java)
            assertThat((success as PostListState.Success).posts).hasSize(1)
            assertThat((success as PostListState.Success).posts[0].title).isEqualTo("Success Post")

            expectNoEvents()
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `favorite updates trigger state refresh correctly`() = runTest {
        val posts = listOf(
            Post(id = 1, userId = 1, title = "Post 1", body = ""),
            Post(id = 2, userId = 1, title = "Post 2", body = "")
        )

        val favoriteIdsFlow = MutableStateFlow<Set<Int>>(emptySet())

        runBlocking {
            whenever(postRepository.getPosts(anyOrNull())).thenReturn(posts)
            whenever(favoriteRepository.getFavoriteIds()).thenReturn(favoriteIdsFlow)
        }

        val testViewModel = PostListViewModel(postRepository, favoriteRepository)

        testViewModel.uiState.test {
            skipItems(1)

            val success1 = awaitItem()
            assertThat(success1).isInstanceOf(PostListState.Success::class.java)
            assertThat((success1 as PostListState.Success).favoriteIds).isEmpty()

            favoriteIdsFlow.value = setOf(1)

            val success2 = awaitItem()
            assertThat(success2).isInstanceOf(PostListState.Success::class.java)
            assertThat((success2 as PostListState.Success).favoriteIds).contains(1)

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `empty result returns Empty state`() = runTest {
        runBlocking {
            whenever(postRepository.getPosts(anyOrNull())).thenReturn(emptyList())
            whenever(favoriteRepository.getFavoriteIds()).thenReturn(flowOf(emptySet()))
        }

        val testViewModel = PostListViewModel(postRepository, favoriteRepository)

        testViewModel.uiState.test {
            skipItems(1)

            val state = awaitItem()
            assertThat(state).isInstanceOf(PostListState.Empty::class.java)
            assertThat(state).isNotInstanceOf(PostListState.Success::class.java)

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `new subscriber receives current state immediately`() = runTest {
        val posts = listOf(
            Post(id = 1, userId = 1, title = "Existing Post", body = "")
        )

        runBlocking {
            whenever(postRepository.getPosts(anyOrNull())).thenReturn(posts)
            whenever(favoriteRepository.getFavoriteIds()).thenReturn(flowOf(emptySet()))
        }

        val testViewModel = PostListViewModel(postRepository, favoriteRepository)

        advanceUntilIdle()

        testViewModel.uiState.test {
            val state = awaitItem()
            assertThat(state).isInstanceOf(PostListState.Success::class.java)
            cancelAndConsumeRemainingEvents()
        }
    }
}

// Вспомогательная функция
fun runBlocking(block: suspend () -> Unit) {
    kotlinx.coroutines.runBlocking { block() }
}