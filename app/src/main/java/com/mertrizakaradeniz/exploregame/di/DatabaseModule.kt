package com.mertrizakaradeniz.exploregame.di

import android.content.Context
import androidx.room.Room
import com.mertrizakaradeniz.exploregame.data.local.GameDatabase
import com.mertrizakaradeniz.exploregame.utils.Constant.DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideGameDatabase(@ApplicationContext context: Context): GameDatabase {
        return Room.databaseBuilder(
            context,
            GameDatabase::class.java,
            DATABASE_NAME
        ).build()
    }

    @Singleton
    @Provides
    fun provideGameDao(gameDatabase: GameDatabase) = gameDatabase.getGameDao()
}