package com.example.recipebook.ui.features.create

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.recipebook.ui.features.common.RecipeForm
import com.example.recipebook.ui.theme.AppColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRecipeScreen(
    onRecipeCreated: () -> Unit,
    viewModel: CreateRecipeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    LaunchedEffect(uiState.isRecipeSaved) {
        if (uiState.isRecipeSaved) {
            onRecipeCreated()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Recipe") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AppColor.Purple80),
                navigationIcon = {
                    IconButton(onClick = onRecipeCreated) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar {
                Row(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Button(
                        onClick = viewModel::saveRecipe,
                        modifier = Modifier.weight(1f),
                        enabled = uiState.formData.name.isNotBlank() && uiState.formData.selectedType != null
                    ) {
                        Text("Save Recipe")
                    }
                }
            }
        }
    ) { paddingValues ->
        RecipeForm(
            formData = uiState.formData,
            onFormDataChange = viewModel::onFormDataChange,
            recipeTypes = uiState.recipeTypes,
            modifier = Modifier.padding(paddingValues)
        )
    }
}