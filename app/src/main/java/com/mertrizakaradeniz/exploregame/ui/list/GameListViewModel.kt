package com.mertrizakaradeniz.exploregame.ui.list

import android.content.Context
import androidx.lifecycle.*
import androidx.paging.*
import com.mertrizakaradeniz.exploregame.data.models.Game
import com.mertrizakaradeniz.exploregame.data.models.GamesResponse
import com.mertrizakaradeniz.exploregame.data.repository.GameRepository
import com.mertrizakaradeniz.exploregame.utils.Constant.DEFAULT_SEARCH_QUERY
import com.mertrizakaradeniz.exploregame.utils.Resource
import com.mertrizakaradeniz.exploregame.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class GameListViewModel @Inject constructor(
    private val repository: GameRepository,
) : ViewModel() {

    private val _gameList: MutableLiveData<Resource<List<Game>>> = MutableLiveData()
    val gameList: LiveData<Resource<List<Game>>> = _gameList

    private val currentQuery = MutableLiveData(DEFAULT_SEARCH_QUERY)

    val games = currentQuery.switchMap { queryString ->
        repository.getSearchResults(queryString).cachedIn(viewModelScope)
    }

    fun searchGames(query: String) {
        currentQuery.value = query
    }

    fun getGameList(context: Context) = viewModelScope.launch {
        safeGetGameListCall(context)
    }

    private suspend fun safeGetGameListCall(context: Context) {
        _gameList.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(context)) {
                val response = repository.getGameList(null)
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