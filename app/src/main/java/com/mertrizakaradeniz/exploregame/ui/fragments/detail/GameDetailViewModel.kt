package com.mertrizakaradeniz.exploregame.ui.fragments.detail

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mertrizakaradeniz.exploregame.data.models.Game
import com.mertrizakaradeniz.exploregame.data.models.GamesResponse
import com.mertrizakaradeniz.exploregame.data.repository.GameRepository
import com.mertrizakaradeniz.exploregame.utils.Resource
import com.mertrizakaradeniz.exploregame.utils.Utils
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class GameDetailViewModel @Inject constructor(
    private val gameRepository: GameRepository
) : ViewModel() {

    private val _gameDetail: MutableLiveData<Resource<Game>> = MutableLiveData()
    val gameDetail: LiveData<Resource<Game>> = _gameDetail



    fun fetchGameDetail(context: Context, id: String) = viewModelScope.launch {
        safeFetchGameDetailCall(context, id)
    }

    private suspend fun safeFetchGameDetailCall(context: Context, id: String) {
        _gameDetail.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(context)) {
                val response = gameRepository.getGameDetail(id)
                _gameDetail.postValue(handleGameDetailResponse(response))
            } else {
                _gameDetail.postValue(Resource.Error("No internet connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException ->
                    _gameDetail.postValue(Resource.Error("Network error"))
                else ->
                    _gameDetail.postValue(
                        Resource.Error("An error occurred, please try again")
                    )
            }
        }
    }

    private fun handleGameDetailResponse(response: Response<Game>): Resource<Game> {
        if (response.isSuccessful && response.body() != null) {
            response.body()?.let {
                return Resource.Success(it)
            }
        } else {
            return Resource.Error(response.message())
        }
        return Resource.Error("An error occurred, please try again")
    }

}