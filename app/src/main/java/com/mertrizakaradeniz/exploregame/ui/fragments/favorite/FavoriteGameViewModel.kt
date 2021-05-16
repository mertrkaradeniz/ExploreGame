package com.mertrizakaradeniz.exploregame.ui.fragments.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mertrizakaradeniz.exploregame.data.models.Game
import com.mertrizakaradeniz.exploregame.data.repository.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteGameViewModel @Inject constructor(
    private val repository: GameRepository
) : ViewModel() {

    fun getAllFavoriteGames() = repository.getAllFavoriteGames()

    fun deleteGame(game: Game) = viewModelScope.launch {
        repository.deleteGame(game)
    }

    fun saveGame(game: Game) = viewModelScope.launch {
        repository.upsert(game)
    }

    fun deleteAll() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAll()
        }
    }

    fun searchDatabase(searchQuery: String): LiveData<List<Game>> {
        return repository.searchDatabase(searchQuery)
    }

}