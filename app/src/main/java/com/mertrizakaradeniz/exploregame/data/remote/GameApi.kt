package com.mertrizakaradeniz.exploregame.data.remote

import com.mertrizakaradeniz.exploregame.data.models.Game
import com.mertrizakaradeniz.exploregame.data.models.GamesResponse
import com.mertrizakaradeniz.exploregame.utils.Constant.API_KEY
import com.mertrizakaradeniz.exploregame.utils.Constant.STARTING_PAGE_INDEX
import com.mertrizakaradeniz.exploregame.utils.Constant.QUERY_PAGE_SIZE
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GameApi {

    @GET("games")
    suspend fun getGameList(
        @Query("page") page: Int? = STARTING_PAGE_INDEX,
        @Query("page_size") pageSize: Int = QUERY_PAGE_SIZE,
        @Query("key") apiKey: String = API_KEY
    ): Response<GamesResponse>

    @GET("games")
    suspend fun searchGame(
        @Query("search") searchQuery: String,
        @Query("page") page: Int,
        @Query("page_size") pageSize: Int,
        @Query("key") apiKey: String = API_KEY
    ): Response<GamesResponse>

    @GET("games/{id}")
    suspend fun getGameDetail(
        @Path("id") id: String,
        @Query("key") apiKey: String = API_KEY
    ): Response<Game>

}