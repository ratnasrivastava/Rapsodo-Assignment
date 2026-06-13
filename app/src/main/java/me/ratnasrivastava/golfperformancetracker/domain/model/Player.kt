package me.ratnasrivastava.golfperformancetracker.domain.model

data class Player(
    val id: String,
    val name: String,
    val club: String,
    val avgSpeed: Double,
    val avgDistance: Double,
    val imageUrl: String
)