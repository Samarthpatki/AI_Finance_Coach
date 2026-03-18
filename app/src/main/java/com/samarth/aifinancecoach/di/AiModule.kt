package com.samarth.aifinancecoach.di

import com.samarth.aifinancecoach.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AiModule {

    @Provides
    @Singleton
    @Named("geminiApiKey")
    fun provideGeminiApiKey(): String = BuildConfig.GEMINI_API_KEY
}
