package me.ratnasrivastava.golfperformancetracker.domain.repository

import kotlinx.coroutines.flow.Flow
import me.ratnasrivastava.golfperformancetracker.domain.model.Player
import me.ratnasrivastava.golfperformancetracker.domain.model.Resource
import me.ratnasrivastava.golfperformancetracker.domain.model.Shot

interface GolfRepository {
    fun getPlayers(forceRefresh: Boolean = false): Flow<Resource<List<Player>>>

    /**
     * Streams a single player by [playerId].
     */
    fun getPlayer(playerId: String): Flow<Resource<Player>>

    /**
     * Streams the shots belonging to the given [playerId], refreshing from the
     * network when needed.
     *
     * @param forceRefresh when true, always attempt a network sync.
     */
    fun getShotsForPlayer(
        playerId: String,
        forceRefresh: Boolean = false
    ): Flow<Resource<List<Shot>>>
}