package com.mertrizakaradeniz.exploregame.data.local

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import com.mertrizakaradeniz.exploregame.data.models.Game
import com.mertrizakaradeniz.exploregame.utils.Resource

@Dao
interface GameDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertGame(game: Game)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addAllGames(list: List<Game>)

    /*@Query("SELECT * FROM games ORDER BY name DESC")
    fun getAllGames(): List<Game>*/

    @Query("SELECT * FROM games WHERE is_fav = 1")
    fun getAllFavoriteGames(): LiveData<List<Game>>

    @Query("SELECT * FROM games WHERE id = :id")
    suspend fun searchGameById(id: Int): Game

    /*@Query("SELECT * FROM games WHERE name LIKE :name")
    suspend fun searchByName(name: String)*/

    @Query("SELECT * FROM games WHERE id = :id and is_fav = :isFav")
    fun checkGameIsFavorite(id: Int, isFav: Boolean = true): LiveData<List<Game>>

    @Delete
    suspend fun deleteGame(game: Game)

    @Query("DELETE FROM games WHERE is_fav = :isFav")
    suspend fun deleteAll(isFav: Boolean = true)

    @Query("DELETE FROM games WHERE is_fav = 'false'")
    suspend fun clearDatabase()

    @Query("SELECT * FROM games WHERE name LIKE :searchQuery and is_fav = :isFav")
    fun searchFavoriteGames(searchQuery: String, isFav: Boolean = true): LiveData<List<Game>>

    @Query("SELECT * FROM games")
    fun getAllGame(): PagingSource<Int, Game>

}