package com.example.recipebook.ui.features.detail

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.recipebook.ui.features.common.ConfirmationDialog
import com.example.recipebook.ui.features.common.RecipeForm
import com.example.recipebook.ui.theme.AppColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    onNavigateBack: () -> Unit,
    viewModel: RecipeDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    LaunchedEffect(uiState.isRecipeDeleted) {
        if (uiState.isRecipeDeleted) {
            onNavigateBack()
        }
    }
    
    if (uiState.showDeleteConfirmDialog) {
        ConfirmationDialog(
            dialogTitle = "Delete Recipe",
            dialogText = "Are you sure you want to delete this recipe? This action cannot be undone.",
            onDismissRequest = {
                viewModel.showDeleteDialog(false)
            },
            onConfirmation = {
                viewModel.showDeleteDialog(false)
                viewModel.deleteRecipe()
            },
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.recipe?.name ?: "Recipe Detail") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AppColor.Purple80),
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.showDeleteDialog(true) }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete Recipe"
                        )
                    }
                }
            )
        },
        bottomBar = {
            val isFormValid = uiState.formData.name.isNotBlank() && uiState.formData.selectedType != null
            
            BottomAppBar {
                Row(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Button(
                        onClick = {
                            viewModel.updateRecipe()
                            onNavigateBack()
                        },
                        modifier = Modifier.weight(1f),
                        enabled = isFormValid
                    ) {
                        Text("Save Changes")
                    }
                }
            }
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            CircularProgressIndicator()
        } else if (uiState.recipe == null) {
            Text("Recipe not found.")
        } else {
            RecipeForm(
                formData = uiState.formData,
                onFormDataChange = viewModel::onFormDataChange,
                recipeTypes = uiState.recipeTypes,
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}