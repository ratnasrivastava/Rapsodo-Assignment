package me.ratnasrivastava.golfperformancetracker.data.repository

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
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException
import androidx.paging.PagingSource
import androidx.paging.testing.asSnapshot
import io.mockk.every

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
    fun `refresh fetches from network and stores when cache is empty`() = runTest {
        coEvery { playerDao.count() } returns 0
        coEvery { api.getPlayers() } returns listOf(
            PlayerDto("1", "Tiger", "driver", 150.0, 280.0, "")
        )

        val result = repository.refreshPlayers(force = false)

        assertTrue(result is Resource.Success)
        coVerify(exactly = 1) { api.getPlayers() }
        coVerify(exactly = 1) { playerDao.upsertAll(any()) }
    }

    @Test
    fun `refresh does not hit network when cache is populated and not forced`() = runTest {
        coEvery { playerDao.count() } returns 2

        val result = repository.refreshPlayers(force = false)

        assertTrue(result is Resource.Success)
        coVerify(exactly = 0) { api.getPlayers() }
        coVerify(exactly = 0) { playerDao.upsertAll(any()) }
    }

    @Test
    fun `refresh always hits network when forced`() = runTest {
        coEvery { playerDao.count() } returns 2
        coEvery { api.getPlayers() } returns emptyList()

        repository.refreshPlayers(force = true)

        coVerify(exactly = 1) { api.getPlayers() }
    }

    @Test
    fun `refresh returns error on network failure`() = runTest {
        coEvery { playerDao.count() } returns 0
        coEvery { api.getPlayers() } throws IOException("offline")

        val result = repository.refreshPlayers(force = true)

        assertTrue(result is Resource.Error)
    }

    @Test
    fun `paged players emits cached entities mapped to domain`() = runTest {
        every { playerDao.pagingSource(any()) } returns FakePlayerPagingSource(cachedEntities)

        val snapshot = repository.getPlayersPaged(query = "").asSnapshot()

        assertEquals(2, snapshot.size)
        assertEquals("Tiger", snapshot.first().name)
    }

    private class FakePlayerPagingSource(
        private val items: List<PlayerEntity>
    ) : PagingSource<Int, PlayerEntity>() {
        override fun getRefreshKey(state: androidx.paging.PagingState<Int, PlayerEntity>): Int? = null
        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PlayerEntity> =
            LoadResult.Page(data = items, prevKey = null, nextKey = null)
    }
}
