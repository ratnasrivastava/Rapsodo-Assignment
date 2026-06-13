package me.ratnasrivastava.golfperformancetracker.domain.usecase

import app.cash.turbine.test
import me.ratnasrivastava.golfperformancetracker.domain.model.Player
import me.ratnasrivastava.golfperformancetracker.domain.model.Resource
import me.ratnasrivastava.golfperformancetracker.util.FakeGolfRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetPlayersUseCaseTest {

    private val players = listOf(
        player(id = "1", name = "Jordan Spieth", club = "driver"),
        player(id = "2", name = "Rory McIlroy", club = "iron"),
        player(id = "3", name = "Tiger Woods", club = "driver")
    )

    private fun useCaseWith(list: List<Player>) =
        GetPlayersUseCase(FakeGolfRepository(players = list))

    @Test
    fun `blank query returns all players`() = runTest {
        useCaseWith(players)(query = "").test {
            val result = awaitItem()
            assertTrue(result is Resource.Success)
            assertEquals(3, (result as Resource.Success).data.size)
            awaitComplete()
        }
    }

    @Test
    fun `query matches player name case-insensitively`() = runTest {
        useCaseWith(players)(query = "rory").test {
            val result = awaitItem() as Resource.Success
            assertEquals(1, result.data.size)
            assertEquals("Rory McIlroy", result.data.first().name)
            awaitComplete()
        }
    }

    @Test
    fun `query matches club type`() = runTest {
        useCaseWith(players)(query = "driver").test {
            val result = awaitItem() as Resource.Success
            assertEquals(2, result.data.size)
            assertTrue(result.data.all { it.club == "driver" })
            awaitComplete()
        }
    }

    @Test
    fun `query with no matches returns empty list`() = runTest {
        useCaseWith(players)(query = "zzz").test {
            val result = awaitItem() as Resource.Success
            assertTrue(result.data.isEmpty())
            awaitComplete()
        }
    }

    private fun player(id: String, name: String, club: String) = Player(
        id = id,
        name = name,
        club = club,
        avgSpeed = 100.0,
        avgDistance = 200.0,
        imageUrl = ""
    )
}