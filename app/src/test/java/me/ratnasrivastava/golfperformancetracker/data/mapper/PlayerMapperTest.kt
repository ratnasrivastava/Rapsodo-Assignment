package me.ratnasrivastava.golfperformancetracker.data.mapper

import me.ratnasrivastava.golfperformancetracker.data.local.entity.PlayerEntity
import me.ratnasrivastava.golfperformancetracker.data.network.dto.PlayerDto
import org.junit.Assert.assertEquals
import org.junit.Test

class PlayerMapperTest {

    @Test
    fun `dto with all fields maps to domain correctly`() {
        val dto = PlayerDto(
            id = "1",
            name = "Ratna",
            club = "driver",
            avgSpeed = 150.0,
            avgDistance = 280.0,
            imageUrl = "http://img/1.png"
        )

        val player = dto.toDomain()

        assertEquals("1", player.id)
        assertEquals("Ratna", player.name)
        assertEquals("driver", player.club)
        assertEquals(150.0, player.avgSpeed, 0.0)
        assertEquals(280.0, player.avgDistance, 0.0)
        assertEquals("http://img/1.png", player.imageUrl)
    }

    @Test
    fun `dto with null fields falls back to safe defaults`() {
        val dto = PlayerDto(
            id = null,
            name = null,
            club = null,
            avgSpeed = null,
            avgDistance = null,
            imageUrl = null
        )

        val player = dto.toDomain()

        assertEquals("", player.id)
        assertEquals("Unknown Player", player.name)
        assertEquals("unknown", player.club)
        assertEquals(0.0, player.avgSpeed, 0.0)
        assertEquals(0.0, player.avgDistance, 0.0)
        assertEquals("", player.imageUrl)
    }

    @Test
    fun `blank name and club fall back to defaults`() {
        val dto = PlayerDto(
            id = "2",
            name = "   ",
            club = "",
            avgSpeed = 100.0,
            avgDistance = 200.0,
            imageUrl = ""
        )

        val player = dto.toDomain()

        assertEquals("Unknown Player", player.name)
        assertEquals("unknown", player.club)
    }

    @Test
    fun `entity to domain and back is symmetric`() {
        val entity = PlayerEntity(
            id = "3",
            name = "Tiger",
            club = "iron",
            avgSpeed = 145.0,
            avgDistance = 270.0,
            imageUrl = "http://img/3.png"
        )

        val roundTripped = entity.toDomain().toEntity()

        assertEquals(entity, roundTripped)
    }
}