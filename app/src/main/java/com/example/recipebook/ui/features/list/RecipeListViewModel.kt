package com.example.recipebook.ui.features.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipebook.data.local.model.Recipe
import com.example.recipebook.data.repository.RecipeRepository
import com.example.recipebook.data.repository.model.RecipeType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RecipeListUiState(
    val recipes: List<Recipe> = emptyList(),
    val recipeTypes: List<RecipeType> = emptyList(),
    val selectedType: RecipeType? = null,
    val isLoading: Boolean = true
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class RecipeListViewModel @Inject constructor(
    private val repository: RecipeRepository
) : ViewModel() {
    private val _selectedRecipeType = MutableStateFlow<RecipeType?>(null)
    private val _recipeTypes = MutableStateFlow<List<RecipeType>>(emptyList())
    
    val uiState: StateFlow<RecipeListUiState> = combine(
        _selectedRecipeType.flatMapLatest { type ->
            if (type == null) repository.getAllRecipes()
            else repository.getRecipesByType(type.id)
        },
        _recipeTypes,
        _selectedRecipeType
    ) { recipes, types, selectedType ->
        RecipeListUiState(
            recipes = recipes,
            recipeTypes = types,
            selectedType = selectedType,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = RecipeListUiState(isLoading = true)
    )
    
    init {
        viewModelScope.launch {
            _recipeTypes.value = repository.getRecipeTypes()
        }
    }
    
    fun onFilterChanged(recipeType: RecipeType?) {
        _selectedRecipeType.value = recipeType
    }
}