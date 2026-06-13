package me.ratnasrivastava.golfperformancetracker.util

import me.ratnasrivastava.golfperformancetracker.domain.model.Player
import me.ratnasrivastava.golfperformancetracker.domain.model.Resource
import me.ratnasrivastava.golfperformancetracker.domain.model.Shot
import me.ratnasrivastava.golfperformancetracker.domain.repository.GolfRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import androidx.paging.PagingData

class FakeGolfRepository(
    private val players: List<Player> = emptyList(),
    private val shots: List<Shot> = emptyList()
) : GolfRepository {

    var refreshCalled = false
    var refreshForced = false

    override fun getPlayersPaged(query: String): Flow<PagingData<Player>> {
        val filtered = if (query.isBlank()) {
            players
        } else {
            players.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.club.contains(query, ignoreCase = true)
            }
        }
        return flowOf(PagingData.from(filtered))
    }

    override suspend fun refreshPlayers(force: Boolean): Resource<Unit> {
        refreshCalled = true
        refreshForced = force
        return Resource.Success(Unit)
    }

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