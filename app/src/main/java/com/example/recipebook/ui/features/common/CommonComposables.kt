package com.example.recipebook.ui.features.common

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

// Common dialog composable for confirmation actions
@Composable
fun ConfirmationDialog(
    modifier: Modifier = Modifier,
    dialogTitle: String,
    dialogText: String,
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
) {
    AlertDialog(
        title = { Text(text = dialogTitle) },
        text = { Text(text = dialogText) },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = onConfirmation) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Dismiss")
            }
        },
        modifier = modifier
    )
}