package com.example.recipebook.ui.features.list

import app.cash.turbine.test
import com.example.recipebook.data.local.model.Recipe
import com.example.recipebook.data.repository.RecipeRepository
import com.example.recipebook.data.repository.model.RecipeType
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RecipeListViewModelTest {
    private lateinit var viewModel: RecipeListViewModel
    private val mockRepository: RecipeRepository = mockk(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()
    
    private val sampleTypes = listOf(
        RecipeType(id = 1, name = "Breakfast"),
        RecipeType(id = 2, name = "Lunch")
    )
    
    private val sampleRecipes = listOf(
        Recipe(id = 1, name = "Pancakes", recipeTypeId = 1, ingredients = "", steps = ""),
        Recipe(id = 2, name = "Salad", recipeTypeId = 2, ingredients = "", steps = "")
    )
    
    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        
        coEvery { mockRepository.getRecipeTypes() } returns sampleTypes
        coEvery { mockRepository.getAllRecipes() } returns flowOf(sampleRecipes)
        coEvery { mockRepository.getRecipesByType(1) } returns flowOf(listOf(sampleRecipes[0]))
        
        viewModel = RecipeListViewModel(mockRepository)
    }
    
    @After
    fun tearDown() {
        // Reset the main dispatcher
        Dispatchers.resetMain()
    }
    
    @Test
    fun `uiState initially loads all recipes and types`() = runTest {
        viewModel.uiState.test {
            // Await the first emission
            val initialState = awaitItem()
            assertEquals(true, initialState.isLoading)
            
            // Await the loaded state
            val loadedState = awaitItem()
            assertEquals(false, loadedState.isLoading)
            assertEquals(sampleRecipes, loadedState.recipes)
            assertEquals(sampleTypes, loadedState.recipeTypes)
            assertEquals(null, loadedState.selectedType)
            
            // Cancel to avoid waiting for more items
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `onFilterChanged updates uiState with filtered recipes`() = runTest {
        viewModel.uiState.test {
            // Skip initial loading states
            awaitItem() // Initial
            awaitItem() // Loaded
            
            // When filter is changed
            viewModel.onFilterChanged(sampleTypes[0])
            
            // Await Emission #1 (The "Intermediate" state)
            // Here, selectedType is updated, but recipes list is not yet.
            val intermediateState = awaitItem()
            assertEquals(sampleTypes[0], intermediateState.selectedType)
            assertEquals(sampleRecipes, intermediateState.recipes) // Still the old list
            
            // Await Emission #2 (The "Final" state)
            // Here, the recipes flow has emitted, and everything is in sync.
            val finalState = awaitItem()
            assertEquals(listOf(sampleRecipes[0]), finalState.recipes)
            assertEquals(sampleTypes[0], finalState.selectedType)
            
            cancelAndIgnoreRemainingEvents()
        }
    }
}