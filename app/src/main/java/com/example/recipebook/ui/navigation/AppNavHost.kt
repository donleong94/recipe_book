package com.example.recipebook.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.recipebook.ui.features.create.CreateRecipeScreen
import com.example.recipebook.ui.features.detail.RecipeDetailScreen
import com.example.recipebook.ui.features.list.RecipeListScreen

// Main navigation host used to manage the navigation graph
@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.RecipeList.route) {
        composable(Screen.RecipeList.route) {
            RecipeListScreen(
                onRecipeClick = { recipeId ->
                    navController.navigate(Screen.RecipeDetail.createRoute(recipeId))
                },
                onAddRecipeClick = {
                    navController.navigate(Screen.CreateRecipe.route)
                }
            )
        }
        composable(Screen.CreateRecipe.route) {
            CreateRecipeScreen(
                onRecipeCreated = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.RecipeDetail.route,
            arguments = listOf(navArgument("recipeId") { type = NavType.LongType })
        ) {
            RecipeDetailScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}