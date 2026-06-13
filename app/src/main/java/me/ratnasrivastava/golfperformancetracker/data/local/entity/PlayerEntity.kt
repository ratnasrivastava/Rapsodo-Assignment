package me.ratnasrivastava.golfperformancetracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "players")
data class PlayerEntity(
    @PrimaryKey val id: String,
    val name: String,
    val club: String,
    val avgSpeed: Double,
    val avgDistance: Double,
    val imageUrl: String
)
