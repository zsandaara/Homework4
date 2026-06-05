package com.example.homework4.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * JUnit Rule для настройки диспатчеров корутин в тестах
 * Автоматически подменяет Dispatchers.Main на тестовый
 */
@ExperimentalCoroutinesApi
class TestDispatcherRule(
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : TestWatcher() {

    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }

    // Вспомогательный метод для паузы в тестах
    fun pause() {
        // Для TestDispatcher можно управлять временем
        // testDispatcher.scheduler.advanceUntilIdle()
    }
}