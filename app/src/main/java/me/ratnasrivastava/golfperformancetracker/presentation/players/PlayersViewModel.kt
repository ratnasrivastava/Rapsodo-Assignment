package me.ratnasrivastava.golfperformancetracker.presentation.players

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import me.ratnasrivastava.golfperformancetracker.domain.model.Resource
import me.ratnasrivastava.golfperformancetracker.domain.usecase.GetPlayersUseCase
import javax.inject.Inject


@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class PlayersViewModel @Inject constructor(
    private val getPlayersUseCase: GetPlayersUseCase
) : ViewModel() {

    private val queryFlow = MutableStateFlow("")

    private val _uiState = MutableStateFlow(PlayersUiState(isLoading = true))
    val uiState: StateFlow<PlayersUiState> = _uiState.asStateFlow()

    init {
        observePlayers()
    }

    private fun observePlayers() {
        queryFlow
            .debounce(250)
            .distinctUntilChanged()
            .flatMapLatest { query ->
                getPlayersUseCase(query = query)
            }
            .onEach { resource ->
                _uiState.update { current ->
                    when (resource) {
                        is Resource.Loading -> current.copy(
                            isLoading = true,
                            players = resource.data ?: current.players,
                            errorMessage = null
                        )
                        is Resource.Success -> current.copy(
                            isLoading = false,
                            players = resource.data,
                            errorMessage = null
                        )
                        is Resource.Error -> current.copy(
                            isLoading = false,
                            players = resource.data ?: current.players,
                            errorMessage = resource.message
                        )
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    fun onQueryChanged(query: String) {
        queryFlow.value = query
        _uiState.update { it.copy(query = query) }
    }

    fun refresh() {
        getPlayersUseCase(query = queryFlow.value, forceRefresh = true)
            .onEach { resource ->
                _uiState.update { current ->
                    when (resource) {
                        is Resource.Loading -> current.copy(isLoading = true, errorMessage = null)
                        is Resource.Success -> current.copy(
                            isLoading = false,
                            players = resource.data,
                            errorMessage = null
                        )
                        is Resource.Error -> current.copy(
                            isLoading = false,
                            players = resource.data ?: current.players,
                            errorMessage = resource.message
                        )
                    }
                }
            }
            .launchIn(viewModelScope)
    }
}