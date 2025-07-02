package com.example.recipebook.data.repository

import android.content.Context
import android.content.res.AssetManager
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.recipebook.data.local.AppDatabase
import com.example.recipebook.data.local.RecipeDao
import com.example.recipebook.data.local.model.Recipe
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.ByteArrayInputStream

// Use Robolectric to provide a real Android Context for the in-memory DB
@RunWith(RobolectricTestRunner::class)
class RecipeRepositoryTest {
    private lateinit var recipeDao: RecipeDao
    private lateinit var db: AppDatabase
    private lateinit var repository: RecipeRepository
    
    private val mockContext: Context = mockk()
    private val mockAssetManager: AssetManager = mockk()
    
    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        
        recipeDao = db.recipeDao()
        
        every { mockContext.assets } returns mockAssetManager
        val json = """[{"id":1,"name":"Breakfast"}]"""
        every { mockAssetManager.open(any()) } returns ByteArrayInputStream(json.toByteArray())
        
        repository = RecipeRepositoryImpl(recipeDao, mockContext)
    }
    
    @After
    fun closeDb() {
        db.close()
    }
    
    @Test
    fun `repository inserts and retrieves recipe correctly`() = runTest {
        val newRecipe = Recipe(
            id = 1,
            name = "Test Pancake",
            recipeTypeId = 1,
            ingredients = "ing",
            steps = "steps"
        )
        
        repository.addRecipe(newRecipe)
        val allRecipes = repository.getAllRecipes().first()
        
        assertEquals(1, allRecipes.size)
        assertEquals("Test Pancake", allRecipes[0].name)
    }
    
    @Test
    fun `repository inserts and delete recipe successfully`() = runTest {
        val newRecipe = Recipe(
            id = 2,
            name = "Test Pancake",
            recipeTypeId = 1,
            ingredients = "ing",
            steps = "steps"
        )
        
        repository.addRecipe(newRecipe)
        val allRecipes = repository.getAllRecipes().first()
        
        assertEquals(1, allRecipes.size)
        assertEquals("Test Pancake", allRecipes[0].name)
        
        repository.deleteRecipe(newRecipe)
        val recipesAfterDelete = repository.getAllRecipes().first()
        assertEquals(0, recipesAfterDelete.size)
    }
    
    @Test
    fun `getRecipeTypes parses JSON correctly`() = runTest {
        val recipeTypes = repository.getRecipeTypes()
        
        assertEquals(1, recipeTypes.size)
        assertEquals("Breakfast", recipeTypes[0].name)
    }
}