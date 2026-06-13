package me.ratnasrivastava.golfperformancetracker.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.ratnasrivastava.golfperformancetracker.domain.model.Player
import me.ratnasrivastava.golfperformancetracker.domain.model.Resource
import me.ratnasrivastava.golfperformancetracker.domain.repository.GolfRepository
import javax.inject.Inject

class GetPlayersUseCase @Inject constructor(
    private val repository: GolfRepository
) {

    operator fun invoke(
        query: String = "",
        forceRefresh: Boolean = false
    ): Flow<Resource<List<Player>>> {
        return repository.getPlayers(forceRefresh).map { resource ->
            if (query.isBlank()) return@map resource
            resource.filterPlayers(query.trim())
        }
    }

    private fun Resource<List<Player>>.filterPlayers(query: String): Resource<List<Player>> {
        fun List<Player>.matching(): List<Player> = filter { player ->
            player.name.contains(query, ignoreCase = true) ||
                    player.club.contains(query, ignoreCase = true)
        }
        return when (this) {
            is Resource.Success -> Resource.Success(data.matching())
            is Resource.Loading -> Resource.Loading(data?.matching())
            is Resource.Error -> Resource.Error(message, data?.matching())
        }
    }
}
