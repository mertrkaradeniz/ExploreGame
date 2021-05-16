package com.mertrizakaradeniz.exploregame.ui.fragments.list

import android.content.Context
import androidx.lifecycle.*
import androidx.paging.*
import com.mertrizakaradeniz.exploregame.data.local.GameDao
import com.mertrizakaradeniz.exploregame.data.models.Game
import com.mertrizakaradeniz.exploregame.data.models.GamesResponse
import com.mertrizakaradeniz.exploregame.data.remote.GameApi
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
    private val gameApi: GameApi,
    private val repository: GameRepository,
    private val gameDao: GameDao
) : ViewModel() {

    private val currentQuery = MutableLiveData(DEFAULT_SEARCH_QUERY)

    val games = currentQuery.switchMap { queryString ->
        repository.getSearchResults(queryString).cachedIn(viewModelScope)
    }

    fun searchGames(query: String) {
        currentQuery.value = query
    }

    /*val listDataDB = Pager(
        PagingConfig(pageSize = QUERY_PAGE_SIZE)
    ) {
        repository.getAllGameFromDatabase()
    }.flow.cachedIn(viewModelScope)*/

    /*val listData = Pager(PagingConfig(pageSize = QUERY_PAGE_SIZE)) {
        GamePagingSource(repository)
    }.flow.cachedIn(viewModelScope)*/

    private val _gameList: MutableLiveData<Resource<List<Game>>> = MutableLiveData()
    val gameList: LiveData<Resource<List<Game>>> = _gameList

    private val _searchGameList: MutableLiveData<Resource<List<Game>>> = MutableLiveData()
    val searchGameList: LiveData<Resource<List<Game>>> = _searchGameList

    fun getGameList(context: Context) = viewModelScope.launch {
        safeGetGameListCall(context)
    }

    /*fun searchDatabase(searchQuery: String): LiveData<List<Game>> {
        return repository.searchFavoriteGames(searchQuery)
    }*/

    /*fun searchGame(context: Context, searchQuery: String) = viewModelScope.launch {
        safeSearchGameCall(context, searchQuery)
    }

    private suspend fun safeSearchGameCall(context: Context, searchQuery: String) {
        _searchGameList.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(context)) {
                val response = repository.searchGame(searchQuery)
                _searchGameList.postValue(handleSearchGameResponse(response))
            } else {
                _searchGameList.postValue(Resource.Error("No internet connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> _searchGameList.postValue(Resource.Error("Network Failure"))
                else -> _searchGameList.postValue(Resource.Error("An error occurred, please try again"))
            }
        }
    }

    private fun handleSearchGameResponse(response: Response<GamesResponse>): Resource<List<Game>> {
        if (response.isSuccessful) {
            response.body()?.let {
                return Resource.Success(it.results)
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