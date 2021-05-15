package com.mertrizakaradeniz.exploregame.data.repository

import com.mertrizakaradeniz.exploregame.data.local.GameDatabase
import com.mertrizakaradeniz.exploregame.data.models.Game
import com.mertrizakaradeniz.exploregame.data.remote.GameApi
import javax.inject.Inject

class GameRepository @Inject constructor(
    private val gameApi: GameApi,
    private val database: GameDatabase
) {

    suspend fun getGameList(pageNumber: Int?) = gameApi.getGameList(pageNumber ?: 1)

    suspend fun searchGame(pageNumber: Int, searchQuery: String) =
        gameApi.getGameList(pageNumber, searchQuery)

    suspend fun getGameDetail(id: String) = gameApi.fetchGameDetail(id)

    suspend fun upsert(game: Game) = database.getGameDao().upsert(game)

    fun getSavedGames() = database.getGameDao().getAllGames()

    suspend fun deleteGame(game: Game) = database.getGameDao().deleteGame(game)
}