package me.ratnasrivastava.golfperformancetracker.data.mapper

import me.ratnasrivastava.golfperformancetracker.data.local.entity.ShotEntity
import me.ratnasrivastava.golfperformancetracker.domain.model.Shot

fun ShotEntity.toDomain(): Shot = Shot(
    id = id,
    playerId = playerId,
    ballSpeed = ballSpeed,
    launchAngle = launchAngle,
    carryDistance = carryDistance,
    spinRate = spinRate,
    clubType = clubType
)

fun Shot.toEntity(): ShotEntity = ShotEntity(
    id = id,
    playerId = playerId,
    ballSpeed = ballSpeed,
    launchAngle = launchAngle,
    carryDistance = carryDistance,
    spinRate = spinRate,
    clubType = clubType
)

fun List<ShotEntity>.toDomainShots(): List<Shot> = map { it.toDomain() }
fun List<Shot>.toShotEntities(): List<ShotEntity> = map { it.toEntity() }