package com.mertrizakaradeniz.exploregame.adapters

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mertrizakaradeniz.exploregame.data.local.GameDatabase
import com.mertrizakaradeniz.exploregame.data.models.Game
import com.mertrizakaradeniz.exploregame.data.remote.GameApi
import com.mertrizakaradeniz.exploregame.data.repository.GameRepository
import java.lang.Exception


class GamePagingSource(
    private val repository: GameRepository
) : PagingSource<Int, Game>() {

    override fun getRefreshKey(state: PagingState<Int, Game>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Game> {

        return try {
            val currentPage = params.key ?: 1
            val response = repository.getGameList(currentPage)
            val data = response.body()?.results ?: emptyList()
            val responseData = mutableListOf<Game>()
            responseData.addAll(data)
            removeItemsForViewPager(responseData)
            LoadResult.Page(
                data = responseData,
                prevKey = if (currentPage == 1) null else -1,
                nextKey = currentPage.plus(1)
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    private fun removeItemsForViewPager(responseData: MutableList<Game>) {
        for (i in 0..2) {
            responseData.removeAt(0)
        }
    }
}