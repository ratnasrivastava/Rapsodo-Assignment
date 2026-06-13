package me.ratnasrivastava.golfperformancetracker.domain.usecase

import me.ratnasrivastava.golfperformancetracker.domain.model.Resource
import me.ratnasrivastava.golfperformancetracker.domain.repository.GolfRepository
import javax.inject.Inject

class RefreshPlayersUseCase @Inject constructor(
    private val repository: GolfRepository
) {
    suspend operator fun invoke(force: Boolean = false): Resource<Unit> =
        repository.refreshPlayers(force)
}