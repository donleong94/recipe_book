package com.example.recipebook.data.repository

import com.example.recipebook.data.local.model.Recipe
import com.example.recipebook.data.repository.model.RecipeType
import kotlinx.coroutines.flow.Flow

// Abstraction for the repository
interface RecipeRepository {
    fun getAllRecipes(): Flow<List<Recipe>>
    fun getRecipesByType(typeId: Int): Flow<List<Recipe>>
    suspend fun getRecipe(id: Long): Recipe?
    suspend fun addRecipe(recipe: Recipe): Long
    suspend fun updateRecipe(recipe: Recipe)
    suspend fun deleteRecipe(recipe: Recipe)
    suspend fun getRecipeTypes(): List<RecipeType>
}