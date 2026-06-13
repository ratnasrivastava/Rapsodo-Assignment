package me.ratnasrivastava.golfperformancetracker.domain.usecase

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import me.ratnasrivastava.golfperformancetracker.domain.model.Player
import me.ratnasrivastava.golfperformancetracker.domain.repository.GolfRepository
import javax.inject.Inject

class GetPlayersUseCase @Inject constructor(
    private val repository: GolfRepository
) {

    operator fun invoke(query: String = ""): Flow<PagingData<Player>> =
        repository.getPlayersPaged(query.trim())
}
