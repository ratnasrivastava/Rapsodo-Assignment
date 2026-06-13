package me.ratnasrivastava.golfperformancetracker.data.repository

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import me.ratnasrivastava.golfperformancetracker.data.local.dao.PlayerDao
import me.ratnasrivastava.golfperformancetracker.data.local.dao.ShotDao
import me.ratnasrivastava.golfperformancetracker.data.local.entity.PlayerEntity
import me.ratnasrivastava.golfperformancetracker.data.network.GolfApiService
import me.ratnasrivastava.golfperformancetracker.data.network.dto.PlayerDto
import me.ratnasrivastava.golfperformancetracker.domain.model.Resource
import me.ratnasrivastava.golfperformancetracker.util.TestDispatcherProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class GolfRepositoryImplTest {

    private lateinit var api: GolfApiService
    private lateinit var playerDao: PlayerDao
    private lateinit var shotDao: ShotDao
    private lateinit var repository: GolfRepositoryImpl

    private val cachedEntities = listOf(
        PlayerEntity("1", "Tiger", "driver", 150.0, 280.0, ""),
        PlayerEntity("2", "Rory", "iron", 145.0, 270.0, "")
    )

    @Before
    fun setUp() {
        api = mockk()
        playerDao = mockk(relaxed = true)
        shotDao = mockk(relaxed = true)
        repository = GolfRepositoryImpl(
            api = api,
            playerDao = playerDao,
            shotDao = shotDao,
            dispatchers = TestDispatcherProvider()
        )
    }

    @Test
    fun `when cache is empty it fetches from network and stores result`() = runTest {
        coEvery { playerDao.count() } returns 0
        coEvery { api.getPlayers() } returns listOf(
            PlayerDto("1", "Tiger", "driver", 150.0, 280.0, "")
        )
        coEvery { playerDao.observePlayers() } returns flowOf(cachedEntities)

        repository.getPlayers(forceRefresh = false).test {
            // First emission is Loading.
            assertTrue(awaitItem() is Resource.Loading)
            // Then Success backed by the cache.
            val success = awaitItem()
            assertTrue(success is Resource.Success)
            assertEquals(2, (success as Resource.Success).data.size)
            awaitComplete()
        }

        // Verify the network was called and results persisted.
        coVerify(exactly = 1) { api.getPlayers() }
        coVerify(exactly = 1) { playerDao.upsertAll(any()) }
    }

    @Test
    fun `when cache is populated and no refresh it does not hit network`() = runTest {
        coEvery { playerDao.count() } returns 2
        coEvery { playerDao.observePlayers() } returns flowOf(cachedEntities)

        repository.getPlayers(forceRefresh = false).test {
            assertTrue(awaitItem() is Resource.Loading)
            assertTrue(awaitItem() is Resource.Success)
            awaitComplete()
        }

        coVerify(exactly = 0) { api.getPlayers() }
    }

    @Test
    fun `on network failure it emits error with cached data attached`() = runTest {
        coEvery { playerDao.count() } returns 0
        coEvery { api.getPlayers() } throws IOException("offline")
        coEvery { playerDao.observePlayers() } returns flowOf(cachedEntities)

        repository.getPlayers(forceRefresh = true).test {
            assertTrue(awaitItem() is Resource.Loading)
            val result = awaitItem()
            assertTrue(result is Resource.Error)
            // Cached data is still present despite the network error.
            assertEquals(2, (result as Resource.Error).data?.size)
            awaitComplete()
        }
    }
}