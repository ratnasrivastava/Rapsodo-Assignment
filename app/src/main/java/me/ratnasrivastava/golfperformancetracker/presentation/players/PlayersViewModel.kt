package me.ratnasrivastava.golfperformancetracker.presentation.players

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import me.ratnasrivastava.golfperformancetracker.domain.model.Player
import me.ratnasrivastava.golfperformancetracker.domain.usecase.GetPlayersUseCase
import me.ratnasrivastava.golfperformancetracker.domain.usecase.RefreshPlayersUseCase
import javax.inject.Inject


@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class PlayersViewModel @Inject constructor(
    private val getPlayersUseCase: GetPlayersUseCase,
    private val refreshPlayersUseCase: RefreshPlayersUseCase
) : ViewModel() {
    private val queryFlow = MutableStateFlow("")

    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()

    private val _refreshError = MutableStateFlow<String?>(null)
    val refreshError = _refreshError.asStateFlow()

    val players: Flow<PagingData<Player>> = queryFlow
        .debounce(250)
        .distinctUntilChanged()
        .flatMapLatest { q -> getPlayersUseCase(q) }
        .cachedIn(viewModelScope)

    init {
        refresh(force = false)
    }

    fun onQueryChanged(query: String) {
        queryFlow.value = query
        _query.value = query
    }

    fun refresh(force: Boolean = true) {
        viewModelScope.launch {
            val result = refreshPlayersUseCase(force)
            _refreshError.value =
                (result as? me.ratnasrivastava.golfperformancetracker.domain.model.Resource.Error)?.message
        }
    }

    fun clearRefreshError() {
        _refreshError.value = null
    }
}