package me.ratnasrivastava.golfperformancetracker.domain.model

data class Shot(
    val id: String,
    val playerId: String,
    val ballSpeed: Double,
    val launchAngle: Double,
    val carryDistance: Double,
    val spinRate: Double,
    val clubType: String
)
