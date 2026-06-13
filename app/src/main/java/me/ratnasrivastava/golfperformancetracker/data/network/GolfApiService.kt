package me.ratnasrivastava.golfperformancetracker.data.network

import me.ratnasrivastava.golfperformancetracker.data.network.dto.PlayerDto
import me.ratnasrivastava.golfperformancetracker.data.network.dto.ShotDto
import retrofit2.http.GET
import retrofit2.http.Query

interface GolfApiService {

    @GET("players")
    suspend fun getPlayers(): List<PlayerDto>

    @GET("shots")
    suspend fun getShots(
        @Query("playerId") playerId: String? = null
    ): List<ShotDto>
}