package me.ratnasrivastava.golfperformancetracker.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import me.ratnasrivastava.golfperformancetracker.domain.model.Resource
import me.ratnasrivastava.golfperformancetracker.domain.usecase.GetPlayerUseCase
import me.ratnasrivastava.golfperformancetracker.domain.usecase.GetShotsForPlayerUseCase
import javax.inject.Inject

@HiltViewModel
class PlayerDetailViewModel @Inject constructor(
    private val getPlayerUseCase: GetPlayerUseCase,
    private val getShotsForPlayerUseCase: GetShotsForPlayerUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val playerId: String = savedStateHandle.get<String>(ARG_PLAYER_ID).orEmpty()

    private val _uiState = MutableStateFlow(PlayerDetailUiState(isLoading = true))
    val uiState: StateFlow<PlayerDetailUiState> = _uiState.asStateFlow()

    init {
        observePlayerAndShots()
    }

    private fun observePlayerAndShots() {
        val playerFlow = getPlayerUseCase(playerId)
        val shotsFlow = getShotsForPlayerUseCase(playerId)

        combine(playerFlow, shotsFlow) { playerRes, shotsRes ->
            buildState(playerRes, shotsRes)
        }
            .onEach { newState -> _uiState.value = newState }
            .launchIn(viewModelScope)
    }

    private fun buildState(
        playerRes: Resource<*>,
        shotsRes: Resource<*>
    ): PlayerDetailUiState {
        val current = _uiState.value

        val player = when (playerRes) {
            is Resource.Success -> playerRes.data as? me.ratnasrivastava.golfperformancetracker.domain.model.Player
            is Resource.Loading -> (playerRes.data as? me.ratnasrivastava.golfperformancetracker.domain.model.Player) ?: current.player
            is Resource.Error -> (playerRes.data as? me.ratnasrivastava.golfperformancetracker.domain.model.Player) ?: current.player
        }

        @Suppress("UNCHECKED_CAST")
        val shots = when (shotsRes) {
            is Resource.Success -> shotsRes.data as? List<me.ratnasrivastava.golfperformancetracker.domain.model.Shot> ?: emptyList()
            is Resource.Loading -> (shotsRes.data as? List<me.ratnasrivastava.golfperformancetracker.domain.model.Shot>) ?: current.shots
            is Resource.Error -> (shotsRes.data as? List<me.ratnasrivastava.golfperformancetracker.domain.model.Shot>) ?: current.shots
        }

        val isLoading = playerRes is Resource.Loading || shotsRes is Resource.Loading
        val error = (playerRes as? Resource.Error)?.message
            ?: (shotsRes as? Resource.Error)?.message

        return PlayerDetailUiState(
            isLoading = isLoading,
            player = player,
            shots = shots,
            errorMessage = error
        )
    }

    companion object {
        const val ARG_PLAYER_ID = "playerId"
    }
}