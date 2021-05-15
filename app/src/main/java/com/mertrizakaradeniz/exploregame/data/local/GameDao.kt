package com.mertrizakaradeniz.exploregame.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.mertrizakaradeniz.exploregame.data.models.Game

@Dao
interface GameDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(games: Game)

    @Query("SELECT * FROM games")
    fun getAllGames(): LiveData<List<Game>>

    @Delete
    suspend fun deleteGame(games: Game)
}