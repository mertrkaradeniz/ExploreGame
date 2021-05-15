package com.mertrizakaradeniz.exploregame.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mertrizakaradeniz.exploregame.data.models.Game

@Database(entities = [Game::class], version = 1, exportSchema = false)
abstract class GameDatabase : RoomDatabase() {

    abstract fun getGameDao(): GameDao
}