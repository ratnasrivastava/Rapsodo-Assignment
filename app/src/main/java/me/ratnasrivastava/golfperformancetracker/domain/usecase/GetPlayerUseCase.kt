package me.ratnasrivastava.golfperformancetracker.domain.usecase

import kotlinx.coroutines.flow.Flow
import me.ratnasrivastava.golfperformancetracker.domain.model.Player
import me.ratnasrivastava.golfperformancetracker.domain.model.Resource
import me.ratnasrivastava.golfperformancetracker.domain.repository.GolfRepository
import javax.inject.Inject

class GetPlayerUseCase @Inject constructor(
    private val repository: GolfRepository
) {
    operator fun invoke(playerId: String): Flow<Resource<Player>> =
        repository.getPlayer(playerId)
}