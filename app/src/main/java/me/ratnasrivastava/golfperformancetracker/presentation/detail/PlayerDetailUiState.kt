package me.ratnasrivastava.golfperformancetracker.presentation.detail

import me.ratnasrivastava.golfperformancetracker.domain.model.Player
import me.ratnasrivastava.golfperformancetracker.domain.model.Shot

data class PlayerDetailUiState(
    val isLoading: Boolean = false,
    val player: Player? = null,
    val shots: List<Shot> = emptyList(),
    val errorMessage: String? = null
) {
    val topBallSpeed: Double
        get() = shots.maxOfOrNull { it.ballSpeed } ?: 0.0

    val longestCarry: Double
        get() = shots.maxOfOrNull { it.carryDistance } ?: 0.0

    val avgLaunchAngle: Double
        get() = if (shots.isEmpty()) 0.0 else shots.map { it.launchAngle }.average()
}