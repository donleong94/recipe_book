package com.example.recipebook.ui.features.common

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.example.recipebook.data.repository.model.RecipeType
import com.example.recipebook.ui.theme.AppColor

// A shared state holder for the form fields
data class RecipeFormData(
    val imageUri: String? = null,
    val selectedType: RecipeType? = null,
    val name: String = "",
    val ingredients: String = "",
    val steps: String = "",
)

// A composable function to display a form for creating or editing a recipe
// Can be used commonly across different screens
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeForm(
    formData: RecipeFormData,
    onFormDataChange: (RecipeFormData) -> Unit,
    recipeTypes: List<RecipeType>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    
    // Image picker launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            // Persist access permissions
            val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
            context.contentResolver.takePersistableUriPermission(uri, flag)
            
            onFormDataChange(formData.copy(imageUri = uri.toString()))
        }
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Image selection section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(width = 1.dp, color = AppColor.Black, shape = RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (formData.imageUri != null) {
                AsyncImage(
                    model = formData.imageUri.toUri(),
                    contentDescription = "Recipe Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text("No Image Selected")
            }
        }
        
        // Image selection buttons
        Row {
            Button(
                onClick = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Text("Select Image")
            }
            
            Button(
                enabled = !formData.imageUri.isNullOrEmpty(),
                onClick = {
                    onFormDataChange(formData.copy(imageUri = null))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp)
                    .weight(1f)
            ) {
                Text("Delete Image")
            }
        }
        
        // Recipe name
        OutlinedTextField(
            value = formData.name,
            onValueChange = { onFormDataChange(formData.copy(name = it)) },
            label = { Text("Recipe Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        
        // Recipe type dropdown
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = formData.selectedType?.name ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Recipe Type") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                recipeTypes.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type.name) },
                        onClick = {
                            onFormDataChange(formData.copy(selectedType = type))
                            expanded = false
                        }
                    )
                }
            }
        }
        
        // Ingredients
        OutlinedTextField(
            value = formData.ingredients,
            onValueChange = { onFormDataChange(formData.copy(ingredients = it)) },
            label = { Text("Ingredients") },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 150.dp)
        )
        
        // Steps
        OutlinedTextField(
            value = formData.steps,
            onValueChange = { onFormDataChange(formData.copy(steps = it)) },
            label = { Text("Steps") },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 200.dp)
        )
    }
}