package me.ratnasrivastava.golfperformancetracker.presentation.players

import app.cash.turbine.test
import me.ratnasrivastava.golfperformancetracker.domain.model.Player
import me.ratnasrivastava.golfperformancetracker.domain.usecase.GetPlayersUseCase
import me.ratnasrivastava.golfperformancetracker.util.FakeGolfRepository
import me.ratnasrivastava.golfperformancetracker.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
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

    private fun viewModel(): PlayersViewModel {
        val useCase = GetPlayersUseCase(FakeGolfRepository(players = players))
        return PlayersViewModel(useCase)
    }

    @Test
    fun `initial state loads all players`() = runTest {
        val vm = viewModel()
        // Let the debounce window elapse so the first query emits.
        advanceTimeBy(300)

        vm.uiState.test {
            val state = awaitItem()
            assertEquals(3, state.players.size)
            assertTrue(!state.isLoading)
        }
    }

    @Test
    fun `changing query filters players`() = runTest {
        val vm = viewModel()
        advanceTimeBy(300)

        vm.onQueryChanged("tiger")
        advanceTimeBy(300)

        vm.uiState.test {
            val state = awaitItem()
            assertEquals(1, state.players.size)
            assertEquals("Tiger Woods", state.players.first().name)
            assertEquals("tiger", state.query)
        }
    }

    @Test
    fun `query for club returns multiple matches`() = runTest {
        val vm = viewModel()
        advanceTimeBy(300)

        vm.onQueryChanged("driver")
        advanceTimeBy(300)

        vm.uiState.test {
            val state = awaitItem()
            assertEquals(2, state.players.size)
        }
    }
}