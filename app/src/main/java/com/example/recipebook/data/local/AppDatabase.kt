package com.example.recipebook.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.recipebook.data.local.model.Recipe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

// Database class for handling local data storage
@Database(entities = [Recipe::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recipeDao(): RecipeDao
    
    class PrepopulateCallback @Inject constructor(
        private val recipeDaoProvider: Provider<RecipeDao>
    ) : Callback() {
        private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            applicationScope.launch { populateDatabase() }
        }
        
        private suspend fun populateDatabase() {
            val recipeDao = recipeDaoProvider.get()
            
            // Sample recipes
            val sampleRecipes = listOf(
                Recipe(
                    name = "Classic Pancakes",
                    recipeTypeId = 1,
                    ingredients = "1 1/2 cups all-purpose flour\n3 1/2 teaspoons baking powder\n1 teaspoon salt\n1 tablespoon white sugar\n1 1/4 cups milk\n1 egg\n3 tablespoons butter, melted",
                    steps = "1. In a large bowl, sift together the flour, baking powder, salt and sugar.\n2. Make a well in the center and pour in the milk, egg and melted butter; mix until smooth.\n3. Heat a lightly oiled griddle or frying pan over medium high heat.\n4. Pour or scoop the batter onto the griddle, using approximately 1/4 cup for each pancake.\n5. Brown on both sides and serve hot.",
                    imageUri = null
                ),
                Recipe(
                    name = "Grilled Chicken Salad",
                    recipeTypeId = 2,
                    ingredients = "2 boneless, skinless chicken breasts\n1 head of romaine lettuce, chopped\n1 cup cherry tomatoes, halved\n1/2 cucumber, sliced\n1/4 red onion, thinly sliced\nYour favorite vinaigrette dressing",
                    steps = "1. Season chicken breasts with salt, pepper, and herbs.\n2. Grill chicken over medium-high heat for 6-8 minutes per side, until cooked through. Let it rest, then slice.\n3. In a large bowl, combine lettuce, tomatoes, cucumber, and red onion.\n4. Top the salad with the sliced grilled chicken.\n5. Drizzle with vinaigrette dressing just before serving.",
                    imageUri = null
                ),
                Recipe(
                    name = "Spaghetti Carbonara",
                    recipeTypeId = 3,
                    ingredients = "200g spaghetti\n100g pancetta, diced\n2 large eggs\n50g Pecorino cheese, grated\nBlack pepper",
                    steps = "1. Cook spaghetti according to package directions.\n2. While pasta cooks, fry pancetta in a pan until crisp. Remove from heat.\n3. In a bowl, whisk eggs and Pecorino cheese. Season with black pepper.\n4. Drain the pasta, reserving a little pasta water. Immediately add the hot pasta to the pan with the pancetta. Stir to coat.\n5. Quickly pour in the egg and cheese mixture, stirring constantly to create a creamy sauce. If it's too thick, add a splash of reserved pasta water.\n6. Serve immediately with extra cheese and pepper.",
                    imageUri = null
                ),
                Recipe(
                    name = "Chocolate Chip Cookies",
                    recipeTypeId = 4,
                    ingredients = "1 cup butter, softened\n1 cup white sugar\n1 cup packed brown sugar\n2 eggs\n2 teaspoons vanilla extract\n3 cups all-purpose flour\n1 teaspoon baking soda\n2 teaspoons hot water\n1/2 teaspoon salt\n2 cups semisweet chocolate chips",
                    steps = "1. Preheat oven to 350°F (175°C).\n2. Cream together the butter, white sugar, and brown sugar until smooth.\n3. Beat in the eggs one at a time, then stir in the vanilla.\n4. Dissolve baking soda in hot water. Add to batter along with salt.\n5. Stir in flour and chocolate chips.\n6. Drop by large spoonfuls onto ungreased pans.\n7. Bake for about 10 minutes, or until edges are nicely browned.",
                    imageUri = null
                )
            )
            
            // Insert all sample recipes into the database
            sampleRecipes.forEach { recipe ->
                recipeDao.insertRecipe(recipe)
            }
        }
    }
}