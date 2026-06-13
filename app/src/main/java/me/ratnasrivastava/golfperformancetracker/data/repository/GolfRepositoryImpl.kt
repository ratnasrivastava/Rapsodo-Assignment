package me.ratnasrivastava.golfperformancetracker.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import me.ratnasrivastava.golfperformancetracker.data.local.dao.PlayerDao
import me.ratnasrivastava.golfperformancetracker.data.local.dao.ShotDao
import me.ratnasrivastava.golfperformancetracker.data.mapper.toDomain
import me.ratnasrivastava.golfperformancetracker.data.mapper.toDomainPlayers
import me.ratnasrivastava.golfperformancetracker.data.mapper.toDomainShots
import me.ratnasrivastava.golfperformancetracker.data.mapper.toEntity
import me.ratnasrivastava.golfperformancetracker.data.network.GolfApiService
import me.ratnasrivastava.golfperformancetracker.data.util.DispatcherProvider
import me.ratnasrivastava.golfperformancetracker.domain.model.Resource
import me.ratnasrivastava.golfperformancetracker.domain.repository.GolfRepository
import me.ratnasrivastava.golfperformancetracker.domain.model.Player
import me.ratnasrivastava.golfperformancetracker.domain.model.Shot
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs

@Singleton
class GolfRepositoryImpl @Inject constructor(
    private val api: GolfApiService,
    private val playerDao: PlayerDao,
    private val shotDao: ShotDao,
    private val dispatchers: DispatcherProvider
) : GolfRepository {

    override fun getPlayers(forceRefresh: Boolean): Flow<Resource<List<Player>>> = flow {
        // 1. Show whatever is cached immediately, in a Loading state.
        val cached = playerDao.observePlayers()
        emit(Resource.Loading())

        // 2. Refresh from network if needed.
        var errorMessage: String? = null
        if (forceRefresh || playerDao.count() == 0) {
            try {
                val remote = api.getPlayers().toDomain()
                playerDao.upsertAll(remote.map { it.toEntity() })
            } catch (e: IOException) {
                errorMessage = "No internet connection. Showing saved data."
            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: "Something went wrong while loading players."
            }
        }

        // 3. Stream the cache as the source of truth, mapping entities to domain.
        emitAll(
            cached.map { entities ->
                val players = entities.toDomainPlayers()
                if (errorMessage != null) {
                    Resource.Error(errorMessage, players)
                } else {
                    Resource.Success(players)
                }
            }
        )
    }.flowOn(dispatchers.io)

    override fun getPlayer(playerId: String): Flow<Resource<Player>> =
        playerDao.observePlayer(playerId)
            .map { entity ->
                if (entity != null) {
                    Resource.Success(entity.toDomain())
                } else {
                    Resource.Error<Player>("Player not found.")
                }
            }
            .flowOn(dispatchers.io)

    override fun getShotsForPlayer(
        playerId: String,
        forceRefresh: Boolean
    ): Flow<Resource<List<Shot>>> = flow {
        val cached = shotDao.observeShotsForPlayer(playerId)
        emit(Resource.Loading())

        var errorMessage: String? = null
        if (forceRefresh || shotDao.countForPlayer(playerId) == 0) {
            try {
                syncShotsForPlayer(playerId)
            } catch (e: IOException) {
                errorMessage = "No internet connection. Showing saved data."
            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: "Something went wrong while loading shots."
            }
        }

        emitAll(
            cached.map { entities ->
                val shots = entities.toDomainShots()
                if (errorMessage != null) {
                    Resource.Error(errorMessage, shots)
                } else {
                    Resource.Success(shots)
                }
            }
        )
    }.flowOn(dispatchers.io)

    private suspend fun syncShotsForPlayer(playerId: String) {
        val allShots = api.getShots()
        if (allShots.isEmpty()) return

        // Deterministically pick which shots "belong" to this player.
        val bucketCount = 5 // shots per player to display
        val playerHash = abs(playerId.hashCode())
        val assigned = allShots
            .filterIndexed { index, _ -> (index + playerHash) % allShots.size < bucketCount }
            .take(bucketCount)
            .mapIndexed { index, dto ->
                // Override the random playerId with this player's id and ensure
                // a unique, stable shot id scoped to the player.
                dto.toDomain(overridePlayerId = playerId)
                    .copy(id = "${playerId}_shot_$index")
            }

        shotDao.clearForPlayer(playerId)
        shotDao.upsertAll(assigned.map { it.toEntity() })
    }
}