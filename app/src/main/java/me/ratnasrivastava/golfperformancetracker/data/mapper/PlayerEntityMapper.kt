package me.ratnasrivastava.golfperformancetracker.data.mapper

import me.ratnasrivastava.golfperformancetracker.data.local.entity.PlayerEntity
import me.ratnasrivastava.golfperformancetracker.domain.model.Player

fun PlayerEntity.toDomain(): Player = Player(
    id = id,
    name = name,
    club = club,
    avgSpeed = avgSpeed,
    avgDistance = avgDistance,
    imageUrl = imageUrl
)

fun Player.toEntity(): PlayerEntity = PlayerEntity(
    id = id,
    name = name,
    club = club,
    avgSpeed = avgSpeed,
    avgDistance = avgDistance,
    imageUrl = imageUrl
)

fun List<PlayerEntity>.toDomainPlayers(): List<Player> = map { it.toDomain() }
fun List<Player>.toPlayerEntities(): List<PlayerEntity> = map { it.toEntity() }