package com.mertrizakaradeniz.exploregame.data.repository

import com.mertrizakaradeniz.exploregame.data.remote.GameApi
import javax.inject.Inject


class GameRepository @Inject constructor(
    private val gameApi: GameApi
) {

    suspend fun fetchGameList() = gameApi.fetchGameList()

    suspend fun fetchGameDetail(id: String) = gameApi.fetchGameDetail(id)

}