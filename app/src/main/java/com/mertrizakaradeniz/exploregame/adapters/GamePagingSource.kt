package com.mertrizakaradeniz.exploregame.adapters

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mertrizakaradeniz.exploregame.data.models.Game
import com.mertrizakaradeniz.exploregame.data.remote.GameApi
import com.mertrizakaradeniz.exploregame.utils.Constant.API_KEY
import java.lang.Exception


class GamePagingSource(
    private val apiService: GameApi
) : PagingSource<Int, Game>() {

    override fun getRefreshKey(state: PagingState<Int, Game>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Game> {

        return try {
            val currentPage = params.key ?: 1
            val response = apiService.fetchGameList(API_KEY,currentPage)
            val data = response.body()?.results ?: emptyList()
            val responseData = mutableListOf<Game>()
            responseData.addAll(data)

            LoadResult.Page(
                data = responseData,
                prevKey = if (currentPage == 1) null else -1,
                nextKey = currentPage.plus(1)
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}