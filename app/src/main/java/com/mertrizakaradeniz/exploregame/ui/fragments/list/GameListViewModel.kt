package com.mertrizakaradeniz.exploregame.ui.fragments.list

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
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
        GamePagingSource(gameApi)
    }.flow.cachedIn(viewModelScope)

    fun fetchGameList(context: Context) = viewModelScope.launch {
        safeFetchGameListCall(context)
    }

    private suspend fun safeFetchGameListCall(context: Context) {
        _gameList.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(context)) {
                val response = gameRepository.fetchGameList()
                _gameList.postValue(handleGameListResponse(response))
            } else {
                _gameList.postValue(Resource.Error("No internet connection", emptyList()))
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