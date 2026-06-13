package me.ratnasrivastava.golfperformancetracker.domain.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import me.ratnasrivastava.golfperformancetracker.domain.model.Player
import me.ratnasrivastava.golfperformancetracker.domain.model.Resource
import me.ratnasrivastava.golfperformancetracker.domain.model.Shot

interface GolfRepository {

    /**
     * Streams a paged, optionally filtered list of players from the local
     * cache. Paging reads from Room; [refreshPlayers] handles network sync.
     *
     * @param query case-insensitive search term matched against name and club.
     */
    fun getPlayersPaged(query: String): Flow<PagingData<Player>>

    /**
     * Ensures the player cache is populated, fetching from the network when the
     * cache is empty or when [force] is true. Returns a one-shot result the UI
     * can use to show refresh progress/errors. Paging picks up the new data
     * automatically because it observes Room.
     */
    suspend fun refreshPlayers(force: Boolean = false): Resource<Unit>

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