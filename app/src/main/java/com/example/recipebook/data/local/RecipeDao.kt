package com.example.recipebook.data.local

import androidx.room.*
import com.example.recipebook.data.local.model.Recipe
import kotlinx.coroutines.flow.Flow

// DAO interface for managing recipes in the database
@Dao
interface RecipeDao {
    //ORDER BY name ASC
    @Query("SELECT * FROM recipes")
    fun getAllRecipes(): Flow<List<Recipe>>
    
    @Query("SELECT * FROM recipes WHERE recipeTypeId = :typeId")
    fun getRecipesByType(typeId: Int): Flow<List<Recipe>>
    
    @Query("SELECT * FROM recipes WHERE id = :id")
    suspend fun getRecipeById(id: Long): Recipe?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: Recipe): Long
    
    @Update
    suspend fun updateRecipe(recipe: Recipe)
    
    @Delete
    suspend fun deleteRecipe(recipe: Recipe)
}