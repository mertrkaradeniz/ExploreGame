package com.mertrizakaradeniz.exploregame.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.mertrizakaradeniz.exploregame.adapters.GamePagingSource
import com.mertrizakaradeniz.exploregame.data.local.GameDao
import com.mertrizakaradeniz.exploregame.data.models.Game
import com.mertrizakaradeniz.exploregame.data.remote.GameApi
import javax.inject.Inject

class GameRepository @Inject constructor(
    private val gameApi: GameApi,
    private val gameDao: GameDao
) {

    suspend fun getGameList(pageNumber: Int?) = gameApi.getGameList(pageNumber ?: 1)

    fun getSearchResults(query: String) =
        Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false,
                maxSize = 100
            ),
            pagingSourceFactory = { GamePagingSource(gameDao, gameApi, query) }
        ).liveData

    suspend fun getGameDetail(id: String) = gameApi.getGameDetail(id)

    suspend fun upsert(game: Game) = gameDao.upsertGame(game)

    suspend fun addAllGames(list: List<Game>) = gameDao.addAllGames(list)

    fun getAllGames() = gameDao.getAllGames()

    fun getAllFavoriteGames() = gameDao.getAllFavoriteGames()

    suspend fun deleteGame(game: Game) = gameDao.deleteGame(game)

    suspend fun clearDatabase() = gameDao.clearDatabase()

    suspend fun deleteAll() = gameDao.deleteAll()

    fun searchFavoriteGames(searchQuery: String) = gameDao.searchFavoriteGames(searchQuery)

    fun checkGameIsFavorite(id: Int) = gameDao.checkGameIsFavorite(id)

}