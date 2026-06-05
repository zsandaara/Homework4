package com.example.homework4.ui.list

import com.example.homework4.data.repository.FavoriteRepository
import com.example.homework4.data.repository.PostRepository
import com.example.homework4.domain.Post
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*
import java.io.IOException

@ExperimentalCoroutinesApi
class PostListViewModelTest {

    private lateinit var viewModel: PostListViewModel
    private lateinit var postRepository: PostRepository
    private lateinit var favoriteRepository: FavoriteRepository

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        postRepository = mock()
        favoriteRepository = mock()

        runBlockingTest {
            whenever(favoriteRepository.getFavoriteIds()).thenReturn(flowOf(emptySet()))
        }

        viewModel = PostListViewModel(postRepository, favoriteRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Loading`() {
        assertThat(viewModel.uiState.value).isInstanceOf(PostListState.Loading::class.java)
    }

    @Test
    fun `load success returns Success state with data`() = runTest {
        val posts = listOf(Post(1, 1, "Test Title", "Test Body"))

        runBlockingTest {
            whenever(postRepository.getPosts(anyOrNull())).thenReturn(posts)
        }

        viewModel.loadPosts(null)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(PostListState.Success::class.java)
        assertThat((state as PostListState.Success).posts).isEqualTo(posts)
    }

    @Test
    fun `load error returns Error state`() = runTest {
        runBlockingTest {
            whenever(postRepository.getPosts(anyOrNull())).thenThrow(IOException("Network error"))
        }

        viewModel.loadPosts(null)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(PostListState.Error::class.java)
    }

    @Test
    fun `retry after error triggers new load`() = runTest {
        var callCount = 0
        runBlockingTest {
            whenever(postRepository.getPosts(anyOrNull())).thenAnswer {
                callCount++
                if (callCount == 1) throw IOException()
                listOf(Post(1, 1, "Success", ""))
            }
        }

        viewModel.loadPosts(null)
        advanceUntilIdle()
        viewModel.retry()
        advanceUntilIdle()

        assertThat(callCount).isEqualTo(2)
    }

    @Test
    fun `empty data returns Empty state`() = runTest {
        runBlockingTest {
            whenever(postRepository.getPosts(anyOrNull())).thenReturn(emptyList())
        }

        viewModel.loadPosts(null)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(PostListState.Empty::class.java)
    }

    @Test
    fun `load posts with userId filter works correctly`() = runTest {
        val posts = listOf(
            Post(1, 1, "User 1 Post", ""),
            Post(2, 2, "User 2 Post", "")
        )
        runBlockingTest {
            whenever(postRepository.getPosts(eq(1))).thenReturn(listOf(posts[0]))
            whenever(postRepository.getPosts(anyOrNull())).thenReturn(posts)
        }

        viewModel.loadPosts(1)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(PostListState.Success::class.java)
        assertThat((state as PostListState.Success).posts).hasSize(1)
    }
}

fun runBlockingTest(block: suspend () -> Unit) {
    kotlinx.coroutines.runBlocking { block() }
}