package com.example.recipebook.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class Recipe(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val recipeTypeId: Int,
    val ingredients: String,
    val steps: String,
    val imageUri: String? = null
)