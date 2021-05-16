package com.mertrizakaradeniz.exploregame.adapters

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mertrizakaradeniz.exploregame.data.local.GameDao
import com.mertrizakaradeniz.exploregame.data.models.Game
import com.mertrizakaradeniz.exploregame.data.remote.GameApi
import com.mertrizakaradeniz.exploregame.utils.Constant.STARTING_PAGE_INDEX
import com.mertrizakaradeniz.exploregame.utils.Data.gameList
import retrofit2.HttpException
import java.io.IOException


class GamePagingSource(
    private val gameDao: GameDao,
    private val gameApi: GameApi,
    private val query: String
) : PagingSource<Int, Game>() {

    override fun getRefreshKey(state: PagingState<Int, Game>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Game> {
        val currentPage = params.key ?: STARTING_PAGE_INDEX

        return try {
            val response = gameApi.searchGame(query, currentPage, params.loadSize)
            val games = response.body()?.results ?: emptyList()
            val gameData = mutableListOf<Game>()
            gameData.addAll(games)
            //gameDao.addAllGames(gameData) //Save all loaded games in database*/
            if (gameList.isNotEmpty()) {
                gameList.forEach { game ->
                    gameData.removeIf {
                        it.id == game.id
                    }
                }
            }
            LoadResult.Page(
                data = gameData,
                prevKey = if (currentPage == STARTING_PAGE_INDEX) null else currentPage.minus(1),
                nextKey = currentPage.plus(1)
            )
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }

}