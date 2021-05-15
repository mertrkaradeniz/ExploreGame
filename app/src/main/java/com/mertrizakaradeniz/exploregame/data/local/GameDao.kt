package com.mertrizakaradeniz.exploregame.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.mertrizakaradeniz.exploregame.data.models.Game
import com.mertrizakaradeniz.exploregame.utils.Resource

@Dao
interface GameDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertGame(game: Game)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAllGames(list: List<Game>)

    @Query("SELECT * FROM games ORDER BY name DESC")
    fun getAllGames(): LiveData<List<Game>>

    @Query("SELECT * FROM games ORDER BY name")
    fun getAllFavoriteGames(): LiveData<List<Game>>

    @Query("SELECT * FROM games WHERE id = :id")
    suspend fun searchGameById(id: Int): Game

    /*@Query("SELECT * FROM games WHERE name LIKE :name")
    suspend fun searchByName(name: String)
*/
    @Delete
    suspend fun deleteGame(game: Game)

    @Query("DELETE FROM games WHERE is_fav = 'false'")
    suspend fun clearDatabase()

}