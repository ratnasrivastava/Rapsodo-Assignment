package me.ratnasrivastava.golfperformancetracker.presentation.players

import me.ratnasrivastava.golfperformancetracker.domain.model.Player

data class PlayersUiState(
    val isLoading: Boolean = false,
    val players: List<Player> = emptyList(),
    val errorMessage: String? = null,
    val query: String = ""
) {
    val isEmpty: Boolean
        get() = !isLoading && players.isEmpty() && errorMessage == null
}
