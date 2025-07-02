package com.example.recipebook.ui.features.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipebook.data.local.model.Recipe
import com.example.recipebook.data.repository.RecipeRepository
import com.example.recipebook.data.repository.model.RecipeType
import com.example.recipebook.ui.features.common.RecipeFormData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CreateRecipeUiState(
    val formData: RecipeFormData = RecipeFormData(),
    val recipeTypes: List<RecipeType> = emptyList(),
    val isRecipeSaved: Boolean = false
)

@HiltViewModel
class CreateRecipeViewModel @Inject constructor(
    private val repository: RecipeRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(CreateRecipeUiState())
    val uiState = _uiState.asStateFlow()
    
    init {
        viewModelScope.launch {
            val types = repository.getRecipeTypes()
            
            _uiState.update {
                it.copy(
                    recipeTypes = types,
                    formData = it.formData.copy(selectedType = types.firstOrNull()) // Pre-select the first type if available
                )
            }
        }
    }
    
    fun onFormDataChange(newFormData: RecipeFormData) {
        _uiState.update { it.copy(formData = newFormData) }
    }
    
    fun saveRecipe() {
        viewModelScope.launch {
            val formData = _uiState.value.formData
            
            if (formData.name.isNotBlank() && formData.selectedType != null) {
                val newRecipe = Recipe(
                    name = formData.name,
                    recipeTypeId = formData.selectedType.id,
                    ingredients = formData.ingredients,
                    steps = formData.steps,
                    imageUri = formData.imageUri
                )
                
                repository.addRecipe(newRecipe)
                _uiState.update { it.copy(isRecipeSaved = true) }
            }
        }
    }
}