package com.example.recipebook.di

import android.content.Context
import androidx.room.Room
import com.example.recipebook.data.local.AppDatabase
import com.example.recipebook.data.local.RecipeDao
import com.example.recipebook.data.repository.RecipeRepository
import com.example.recipebook.data.repository.RecipeRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Provider
import javax.inject.Singleton

// Dagger Hilt module for providing application-level dependencies
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        callback: Provider<AppDatabase.PrepopulateCallback>
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "recipe_book_db"
        )
            .fallbackToDestructiveMigrationOnDowngrade(false)
            .addCallback(callback.get())
            .build()
    }
    
    @Provides
    fun providePrepopulateCallback(recipeDaoProvider: Provider<RecipeDao>): AppDatabase.PrepopulateCallback {
        return AppDatabase.PrepopulateCallback(recipeDaoProvider)
    }
    
    @Provides
    fun provideRecipeDao(appDatabase: AppDatabase): RecipeDao {
        return appDatabase.recipeDao()
    }
}

// Binding interface to implementation for RecipeRepository
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindRecipeRepository(recipeRepositoryImpl: RecipeRepositoryImpl): RecipeRepository
}
