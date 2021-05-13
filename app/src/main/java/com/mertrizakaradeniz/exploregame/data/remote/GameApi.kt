package com.mertrizakaradeniz.exploregame.data.remote

import com.mertrizakaradeniz.exploregame.data.models.Game
import com.mertrizakaradeniz.exploregame.data.models.GamesResponse
import com.mertrizakaradeniz.exploregame.utils.Constant.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GameApi {

    @GET("games")
    suspend fun fetchGameList(
        @Query("key")
        apiKey: String = API_KEY,
        @Query("page")
        page: Int = 1
    ): Response<GamesResponse>

    @GET("games/{id}")
    suspend fun fetchGameDetail(
        @Path("id")
        id: String,
        @Query("key")
    apiKey: String = API_KEY
    ): Response<Game>

}