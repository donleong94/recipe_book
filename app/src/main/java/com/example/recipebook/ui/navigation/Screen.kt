package com.example.recipebook.ui.navigation

// Constant screen routes values
sealed class Screen(val route: String) {
    data object RecipeList : Screen("recipe_list")
    data object CreateRecipe : Screen("create_recipe")
    
    data object RecipeDetail : Screen("recipe_detail/{recipeId}") {
        fun createRoute(recipeId: Long): String {
            return "recipe_detail/$recipeId"
        }
    }
}