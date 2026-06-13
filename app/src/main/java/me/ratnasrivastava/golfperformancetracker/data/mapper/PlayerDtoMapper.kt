package me.ratnasrivastava.golfperformancetracker.data.mapper

import me.ratnasrivastava.golfperformancetracker.data.network.dto.PlayerDto
import me.ratnasrivastava.golfperformancetracker.domain.model.Player

fun PlayerDto.toDomain(): Player = Player(
    id = id.orEmpty(),
    name = name?.takeIf { it.isNotBlank() } ?: "Unknown Player",
    club = club?.takeIf { it.isNotBlank() } ?: "unknown",
    avgSpeed = avgSpeed ?: 0.0,
    avgDistance = avgDistance ?: 0.0,
    imageUrl = imageUrl.orEmpty()
)

/** Convenience extension to map a list of player DTOs to domain models. */
fun List<PlayerDto>.toDomain(): List<Player> = map { it.toDomain() }