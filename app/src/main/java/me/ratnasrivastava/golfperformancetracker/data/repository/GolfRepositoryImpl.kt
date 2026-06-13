package me.ratnasrivastava.golfperformancetracker.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import me.ratnasrivastava.golfperformancetracker.data.local.dao.PlayerDao
import me.ratnasrivastava.golfperformancetracker.data.local.dao.ShotDao
import me.ratnasrivastava.golfperformancetracker.data.mapper.toDomain
import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.withContext
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

    override fun getPlayersPaged(query: String): Flow<PagingData<Player>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false,
                initialLoadSize = PAGE_SIZE * 2
            ),
            pagingSourceFactory = { playerDao.pagingSource(query) }
        ).flow.map { pagingData ->
            // Map entity pages to domain pages lazily, page by page.
            pagingData.map { entity -> entity.toDomain() }
        }
    }

    override suspend fun refreshPlayers(force: Boolean): Resource<Unit> =
        withContext(dispatchers.io) {
            try {
                if (force || playerDao.count() == 0) {
                    val remote = api.getPlayers().map { it.toDomain() }
                    playerDao.upsertAll(remote.map { it.toEntity() })
                }
                Resource.Success(Unit)
            } catch (e: IOException) {
                Resource.Error("No internet connection. Showing saved data.")
            } catch (e: Exception) {
                Resource.Error(e.localizedMessage ?: "Failed to refresh players.")
            }
        }

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

    companion object {
        private const val PAGE_SIZE = 15
    }
}