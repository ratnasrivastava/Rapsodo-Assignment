package me.ratnasrivastava.golfperformancetracker.data.mapper

import me.ratnasrivastava.golfperformancetracker.data.network.dto.ShotDto
import me.ratnasrivastava.golfperformancetracker.domain.model.Shot

fun ShotDto.toDomain(overridePlayerId: String? = null): Shot = Shot(
    id = id.orEmpty(),
    playerId = overridePlayerId ?: playerId.orEmpty(),
    ballSpeed = ballSpeed ?: 0.0,
    launchAngle = launchAngle ?: 0.0,
    carryDistance = carryDistance ?: 0.0,
    spinRate = spinRate ?: 0.0,
    clubType = clubType?.takeIf { it.isNotBlank() } ?: "unknown"
)

/** Convenience extension to map a list of shot DTOs to domain models. */
fun List<ShotDto>.toDomain(): List<Shot> = map { it.toDomain() }