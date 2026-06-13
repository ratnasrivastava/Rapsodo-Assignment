package me.ratnasrivastava.golfperformancetracker.domain.usecase

import me.ratnasrivastava.golfperformancetracker.domain.model.Player
import me.ratnasrivastava.golfperformancetracker.util.FakeGolfRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import androidx.paging.testing.asSnapshot


@OptIn(ExperimentalCoroutinesApi::class)
class GetPlayersUseCaseTest {

    private val players = listOf(
        player(id = "1", name = "Jordan Spieth", club = "driver"),
        player(id = "2", name = "Rory McIlroy", club = "iron"),
        player(id = "3", name = "Tiger Woods", club = "driver")
    )

    private fun useCase() =
        GetPlayersUseCase(FakeGolfRepository(players = players))

    @Test
    fun `blank query returns all players`() = runTest {
        val snapshot = useCase()(query = "").asSnapshot()
        assertEquals(3, snapshot.size)
    }

    @Test
    fun `query matches player name case-insensitively`() = runTest {
        val snapshot = useCase()(query = "rory").asSnapshot()
        assertEquals(1, snapshot.size)
        assertEquals("Rory McIlroy", snapshot.first().name)
    }

    @Test
    fun `query matches club type`() = runTest {
        val snapshot = useCase()(query = "driver").asSnapshot()
        assertEquals(2, snapshot.size)
        assertTrue(snapshot.all { it.club == "driver" })
    }

    @Test
    fun `query with no matches returns empty list`() = runTest {
        val snapshot = useCase()(query = "zzz").asSnapshot()
        assertTrue(snapshot.isEmpty())
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