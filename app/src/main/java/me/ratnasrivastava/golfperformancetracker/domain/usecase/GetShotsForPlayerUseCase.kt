package me.ratnasrivastava.golfperformancetracker.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.ratnasrivastava.golfperformancetracker.domain.model.Resource
import me.ratnasrivastava.golfperformancetracker.domain.model.Shot
import me.ratnasrivastava.golfperformancetracker.domain.repository.GolfRepository
import javax.inject.Inject

class GetShotsForPlayerUseCase @Inject constructor(
    private val repository: GolfRepository
) {

    /**
     * @param playerId     player whose shots to load.
     * @param clubFilter   optional club type to filter by; blank returns all shots.
     * @param forceRefresh when true, triggers a network sync via the repository.
     */
    operator fun invoke(
        playerId: String,
        clubFilter: String = "",
        forceRefresh: Boolean = false
    ): Flow<Resource<List<Shot>>> {
        return repository.getShotsForPlayer(playerId, forceRefresh).map { resource ->
            if (clubFilter.isBlank()) return@map resource
            resource.filterByClub(clubFilter.trim())
        }
    }

    private fun Resource<List<Shot>>.filterByClub(club: String): Resource<List<Shot>> {
        fun List<Shot>.matching(): List<Shot> =
            filter { it.clubType.equals(club, ignoreCase = true) }
        return when (this) {
            is Resource.Success -> Resource.Success(data.matching())
            is Resource.Loading -> Resource.Loading(data?.matching())
            is Resource.Error -> Resource.Error(message, data?.matching())
        }
    }
}