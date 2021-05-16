package com.mertrizakaradeniz.exploregame.data.repository

import androidx.lifecycle.LiveData
import com.mertrizakaradeniz.exploregame.data.local.GameDao
import com.mertrizakaradeniz.exploregame.data.models.Game
import com.mertrizakaradeniz.exploregame.data.remote.GameApi
import javax.inject.Inject

class GameRepository @Inject constructor(
    private val gameApi: GameApi,
    private val gameDao: GameDao
) {

    suspend fun getGameList(pageNumber: Int?) = gameApi.getGameList(pageNumber ?: 1)

    suspend fun searchGame(pageNumber: Int, searchQuery: String) =
        gameApi.getGameList(pageNumber, searchQuery)

    suspend fun getGameDetail(id: String) = gameApi.fetchGameDetail(id)

    suspend fun upsert(game: Game) = gameDao.upsertGame(game)

    suspend fun addAllGames(list: List<Game>) = gameDao.addAllGames(list)

    fun getAllGames() = gameDao.getAllGames()

    fun getAllFavoriteGames() = gameDao.getAllFavoriteGames()

    suspend fun searchGameById(id: Int) = gameDao.searchGameById(id)

    //suspend fun searchByName(name: String) = gameDao.searchByName(name)

    suspend fun deleteGame(game: Game) = gameDao.deleteGame(game)

    suspend fun clearDatabase() = gameDao.clearDatabase()

    suspend fun deleteAll() = gameDao.deleteAll()

    fun searchDatabase(searchQuery: String): LiveData<List<Game>> {
        return gameDao.searchDatabase(searchQuery)
    }

}