package com.example.recipebook.ui.features.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.recipebook.data.local.model.Recipe
import com.example.recipebook.data.repository.model.RecipeType
import com.example.recipebook.ui.theme.AppColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeListScreen(
    onRecipeClick: (Long) -> Unit,
    onAddRecipeClick: () -> Unit,
    viewModel: RecipeListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recipe Book") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AppColor.Purple80)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddRecipeClick) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add Recipe"
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            FilterDropdown(
                recipeTypes = uiState.recipeTypes,
                selectedType = uiState.selectedType,
                onFilterChanged = viewModel::onFilterChanged
            )
            
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (uiState.recipes.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No recipes found. Add one!",
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Text(
                    "Receipt List",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                )
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.recipes, key = { it.id }) { recipe ->
                        RecipeListItem(
                            recipe = recipe,
                            recipeType = uiState.recipeTypes.find { it.id == recipe.recipeTypeId },
                            onClick = { onRecipeClick(recipe.id) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterDropdown(
    recipeTypes: List<RecipeType>,
    selectedType: RecipeType?,
    onFilterChanged: (RecipeType?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    Box(modifier = Modifier.padding(16.dp)) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedType?.name ?: "All Recipes",
                onValueChange = {},
                readOnly = true,
                label = { Text("Filter by Type") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("All Recipes") },
                    onClick = {
                        onFilterChanged(null)
                        expanded = false
                    }
                )
                recipeTypes.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type.name) },
                        onClick = {
                            onFilterChanged(type)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun RecipeListItem(
    recipe: Recipe,
    recipeType: RecipeType?,
    onClick: () -> Unit
) {
    val recipeTypeText = recipeType?.name ?: ""
    val recipeName = if (recipeTypeText.isNotEmpty()) "${recipe.name} ($recipeTypeText)" else recipe.name
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = recipeName,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}