package com.mertrizakaradeniz.exploregame.di

import com.mertrizakaradeniz.exploregame.data.local.GameDao
import com.mertrizakaradeniz.exploregame.data.remote.GameApi
import com.mertrizakaradeniz.exploregame.data.repository.GameRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideGameRepository(
        gameApi: GameApi,
        gameDao: GameDao
    ): GameRepository = GameRepository(gameApi, gameDao)
}