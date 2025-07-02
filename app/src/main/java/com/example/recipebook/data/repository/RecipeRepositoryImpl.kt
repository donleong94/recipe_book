package com.example.recipebook.data.repository

import android.content.Context
import com.example.recipebook.data.local.RecipeDao
import com.example.recipebook.data.local.model.Recipe
import com.example.recipebook.data.repository.model.RecipeType
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecipeRepositoryImpl @Inject constructor(
    private val recipeDao: RecipeDao,
    @ApplicationContext private val context: Context
) : RecipeRepository {
    override fun getAllRecipes(): Flow<List<Recipe>> = recipeDao.getAllRecipes()
    
    override fun getRecipesByType(typeId: Int): Flow<List<Recipe>> = recipeDao.getRecipesByType(typeId)
    
    override suspend fun getRecipe(id: Long): Recipe? = recipeDao.getRecipeById(id)
    
    override suspend fun addRecipe(recipe: Recipe): Long = recipeDao.insertRecipe(recipe)
    
    override suspend fun updateRecipe(recipe: Recipe) = recipeDao.updateRecipe(recipe)
    
    override suspend fun deleteRecipe(recipe: Recipe) = recipeDao.deleteRecipe(recipe)
    
    override suspend fun getRecipeTypes(): List<RecipeType> {
        return withContext(Dispatchers.IO) {
            val jsonString = context.assets.open("recipetypes.json")
                .bufferedReader()
                .use { it.readText() }
            
            val listType = object : TypeToken<List<RecipeType>>() {}.type
            Gson().fromJson(jsonString, listType)
        }
    }
}