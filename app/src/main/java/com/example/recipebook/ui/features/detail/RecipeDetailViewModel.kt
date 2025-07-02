package com.example.recipebook.ui.features.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipebook.data.local.model.Recipe
import com.example.recipebook.data.repository.RecipeRepository
import com.example.recipebook.data.repository.model.RecipeType
import com.example.recipebook.ui.features.common.RecipeFormData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RecipeDetailUiState(
    val recipe: Recipe? = null,
    val formData: RecipeFormData = RecipeFormData(),
    val recipeTypes: List<RecipeType> = emptyList(),
    val isLoading: Boolean = true,
    val isRecipeDeleted: Boolean = false,
    val showDeleteConfirmDialog: Boolean = false
)

@HiltViewModel
class RecipeDetailViewModel @Inject constructor(
    private val repository: RecipeRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val recipeId: Long = checkNotNull(savedStateHandle["recipeId"])
    private val _uiState = MutableStateFlow(RecipeDetailUiState())
    val uiState = _uiState.asStateFlow()
    
    init {
        viewModelScope.launch {
            val recipe = repository.getRecipe(recipeId)
            val types = repository.getRecipeTypes()
            
            _uiState.update {
                val initialFormData = if (recipe != null) {
                    RecipeFormData(
                        name = recipe.name,
                        ingredients = recipe.ingredients,
                        steps = recipe.steps,
                        imageUri = recipe.imageUri,
                        selectedType = types.find { it.id == recipe.recipeTypeId }
                    )
                } else RecipeFormData()
                
                it.copy(
                    recipe = recipe,
                    formData = initialFormData,
                    recipeTypes = types,
                    isLoading = false
                )
            }
        }
    }
    
    fun onFormDataChange(newFormData: RecipeFormData) {
        _uiState.update { it.copy(formData = newFormData) }
    }
    
    fun updateRecipe() {
        viewModelScope.launch {
            val currentState = _uiState.value
            val formData = currentState.formData
            if (currentState.recipe != null && formData.name.isNotBlank() && formData.selectedType != null) {
                val updatedRecipe = currentState.recipe.copy(
                    name = formData.name,
                    recipeTypeId = formData.selectedType.id,
                    ingredients = formData.ingredients,
                    steps = formData.steps,
                    imageUri = formData.imageUri
                )
                repository.updateRecipe(updatedRecipe)
            }
        }
    }
    
    fun showDeleteDialog(show: Boolean) {
        _uiState.update {
            it.copy(showDeleteConfirmDialog = show)
        }
    }
    
    fun deleteRecipe() {
        viewModelScope.launch {
            _uiState.value.recipe?.let { recipe ->
                repository.deleteRecipe(recipe)
                _uiState.update { it.copy(isRecipeDeleted = true) }
            }
        }
    }
}