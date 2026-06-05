package com.example.homework4.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.homework4.ui.detail.PostDetailScreen
import com.example.homework4.ui.favorites.FavoritesScreen
import com.example.homework4.ui.list.PostListScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "list") {

        composable("list") {
            PostListScreen(navController)
        }

        composable(
            "detail/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: 0
            PostDetailScreen(
                navController = navController,
                postId = id
            )
        }

        composable("favorites") {
            FavoritesScreen(navController)
        }
    }
}