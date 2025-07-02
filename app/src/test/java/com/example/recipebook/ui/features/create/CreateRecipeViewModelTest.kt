package com.example.recipebook.ui.features.create

import app.cash.turbine.test
import com.example.recipebook.data.repository.RecipeRepository
import com.example.recipebook.data.repository.model.RecipeType
import com.example.recipebook.ui.features.common.RecipeFormData
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CreateRecipeViewModelTest {
    private lateinit var viewModel: CreateRecipeViewModel
    private val mockRepository: RecipeRepository = mockk(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()
    
    private val sampleTypes = listOf(
        RecipeType(id = 1, name = "Breakfast"),
        RecipeType(id = 2, name = "Lunch")
    )
    
    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        coEvery { mockRepository.getRecipeTypes() } returns sampleTypes
        viewModel = CreateRecipeViewModel(mockRepository)
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `init loads recipe types and pre-selects first type`() = runTest {
        viewModel.uiState.test {
            val loadedState = awaitItem()
            assertEquals(sampleTypes, loadedState.recipeTypes)
            assertEquals(sampleTypes.first(), loadedState.formData.selectedType)
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `onFormDataChange updates the uiState correctly`() = runTest {
        // Ensure the init coroutine completes before we modify the state.
        advanceUntilIdle()
        
        val newFormData = RecipeFormData(name = "New Name", ingredients = "New Ingredients")
        viewModel.onFormDataChange(newFormData)
        
        viewModel.uiState.test {
            val updatedState = awaitItem()
            assertEquals("New Name", updatedState.formData.name)
            assertEquals("New Ingredients", updatedState.formData.ingredients)
            
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `saveRecipe calls repository and updates isRecipeSaved flag`() = runTest {
        // Ensure the init coroutine completes before we proceed with the test.
        // This guarantees the ViewModel is in a stable state.
        advanceUntilIdle()
        
        val formData = RecipeFormData(
            name = "Test Recipe",
            selectedType = sampleTypes[0],
            ingredients = "Testing",
            steps = "Testing"
        )
        
        viewModel.onFormDataChange(formData)
        viewModel.saveRecipe()
        
        // Verify the UI state was updated
        viewModel.uiState.test {
            awaitItem()
            assertTrue(awaitItem().isRecipeSaved)
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `saveRecipe does not call repository if name is blank`() = runTest {
        val formData = RecipeFormData(name = "", selectedType = sampleTypes[0])
        viewModel.onFormDataChange(formData)
        viewModel.saveRecipe()
        
        // Verify the repository was NOT called
        coVerify(exactly = 0) { mockRepository.addRecipe(any()) }
    }
}