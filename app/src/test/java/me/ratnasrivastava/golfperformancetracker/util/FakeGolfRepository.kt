package me.ratnasrivastava.golfperformancetracker.util

import me.ratnasrivastava.golfperformancetracker.domain.model.Player
import me.ratnasrivastava.golfperformancetracker.domain.model.Resource
import me.ratnasrivastava.golfperformancetracker.domain.model.Shot
import me.ratnasrivastava.golfperformancetracker.domain.repository.GolfRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeGolfRepository(
    private val players: List<Player> = emptyList(),
    private val shots: List<Shot> = emptyList()
) : GolfRepository {

    override fun getPlayers(forceRefresh: Boolean): Flow<Resource<List<Player>>> =
        flowOf(Resource.Success(players))

    override fun getPlayer(playerId: String): Flow<Resource<Player>> {
        val player = players.firstOrNull { it.id == playerId }
        return flowOf(
            if (player != null) Resource.Success(player)
            else Resource.Error("Player not found.")
        )
    }

    override fun getShotsForPlayer(
        playerId: String,
        forceRefresh: Boolean
    ): Flow<Resource<List<Shot>>> =
        flowOf(Resource.Success(shots.filter { it.playerId == playerId }))
}