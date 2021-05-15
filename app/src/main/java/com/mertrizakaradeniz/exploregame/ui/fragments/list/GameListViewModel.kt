package com.mertrizakaradeniz.exploregame.ui.fragments.list

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.mertrizakaradeniz.exploregame.ExploreGameApplication
import com.mertrizakaradeniz.exploregame.adapters.GamePagingSource
import com.mertrizakaradeniz.exploregame.data.models.Game
import com.mertrizakaradeniz.exploregame.data.models.GamesResponse
import com.mertrizakaradeniz.exploregame.data.remote.GameApi
import com.mertrizakaradeniz.exploregame.data.repository.GameRepository
import com.mertrizakaradeniz.exploregame.utils.Resource
import com.mertrizakaradeniz.exploregame.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class GameListViewModel @Inject constructor(
    private val gameApi: GameApi,
    private val gameRepository: GameRepository
) : ViewModel() {

    private val _gameList: MutableLiveData<Resource<List<Game>>> = MutableLiveData()
    val gameList: LiveData<Resource<List<Game>>> = _gameList

    val listData = Pager(PagingConfig(pageSize = 1)) {
        GamePagingSource(gameRepository)
    }.flow.cachedIn(viewModelScope)

    fun getGameList(context: Context) = viewModelScope.launch {
        safeGetGameListCall(context)
    }

    /*
        fun searchGame(context: Context, searchQuery: String) = viewModelScope.launch {
            safeSearchGameCall(context, searchQuery)
        }

        private suspend fun safeSearchGameCall(context: Context, searchQuery: String) {
            _gameList.postValue(Resource.Loading())
            try {
                if (Utils.hasInternetConnection(context)) {
                    val response = gameRepository.searchGame(gameListPage, searchQuery)
                    _gameList.postValue(handleSearchGameResponse(response))
                } else {
                    _gameList.postValue(Resource.Error("No internet connection"))
                }
            } catch (t: Throwable) {
                when (t) {
                    is IOException -> _gameList.postValue(Resource.Error("Network Failure"))
                    else -> _gameList.postValue(Resource.Error("An error occurred, please try again"))
                }
            }
        }

        private fun handleSearchGameResponse(response: Response<GamesResponse>): Resource<GamesResponse> {
            if (response.isSuccessful) {
                response.body()?.let { resultResponse ->
                    searchGamePage++
                    if (searchGameResponse == null) {
                        searchGameResponse = resultResponse
                    } else {
                        val oldGames = searchGameResponse?.results
                        val newGames = resultResponse.results
                        oldGames?.addAll(newGames)
                    }
                    return Resource.Success(searchGameResponse ?: resultResponse)
                }
            } else {
                return Resource.Error(response.message())
            }
            return Resource.Error("An error occurred, please try again")
        }
    */

    private suspend fun safeGetGameListCall(context: Context) {
        _gameList.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(context)) {
                val response = gameRepository.getGameList(null)
                _gameList.postValue(handleGameListResponse(response))
            } else {
                _gameList.postValue(Resource.Error("No internet connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> _gameList.postValue(Resource.Error("Network Failure"))
                else -> _gameList.postValue(Resource.Error("An error occurred, please try again"))
            }
        }
    }

    private fun handleGameListResponse(response: Response<GamesResponse>): Resource<List<Game>> {
        if (response.isSuccessful) {
            response.body()?.let {
                return Resource.Success(it.results)
            }
        } else {
            return Resource.Error(response.message())
        }
        return Resource.Error("An error occurred, please try again")
    }

}