package me.ratnasrivastava.golfperformancetracker.presentation.players

import me.ratnasrivastava.golfperformancetracker.domain.model.Player
import me.ratnasrivastava.golfperformancetracker.domain.usecase.GetPlayersUseCase
import me.ratnasrivastava.golfperformancetracker.util.FakeGolfRepository
import me.ratnasrivastava.golfperformancetracker.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import me.ratnasrivastava.golfperformancetracker.domain.usecase.RefreshPlayersUseCase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PlayersViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val players = listOf(
        Player("1", "Jordan Spieth", "driver", 150.0, 280.0, ""),
        Player("2", "Rory McIlroy", "iron", 145.0, 270.0, ""),
        Player("3", "Tiger Woods", "driver", 152.0, 290.0, "")
    )

    private fun viewModel(fake: FakeGolfRepository = FakeGolfRepository(players = players)): PlayersViewModel {
        return PlayersViewModel(
            getPlayersUseCase = GetPlayersUseCase(fake),
            refreshPlayersUseCase = RefreshPlayersUseCase(fake)
        )
    }

    @Test
    fun `onQueryChanged updates exposed query state`() = runTest {
        val vm = viewModel()
        vm.onQueryChanged("tiger")
        assertEquals("tiger", vm.query.value)
    }

    @Test
    fun `init triggers a player refresh`() = runTest {
        val fake = FakeGolfRepository(players = players)
        viewModel(fake)
        advanceTimeBy(50)
        assertTrue(fake.refreshCalled)
    }
}