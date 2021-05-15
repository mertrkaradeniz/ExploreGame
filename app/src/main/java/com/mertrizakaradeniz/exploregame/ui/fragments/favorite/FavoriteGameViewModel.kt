package com.mertrizakaradeniz.exploregame.ui.fragments.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mertrizakaradeniz.exploregame.data.models.Game
import com.mertrizakaradeniz.exploregame.data.repository.GameRepository
import com.mertrizakaradeniz.exploregame.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteGameViewModel @Inject constructor(
    private val gameRepository: GameRepository
) : ViewModel() {

    fun getAllFavoriteGames() = gameRepository.getAllFavoriteGames()

    fun deleteGame(game: Game) = viewModelScope.launch {
        gameRepository.deleteGame(game)
    }

    fun saveGame(game: Game) = viewModelScope.launch {
        gameRepository.upsert(game)
    }


}