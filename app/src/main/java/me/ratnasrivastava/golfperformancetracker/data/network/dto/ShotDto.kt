package me.ratnasrivastava.golfperformancetracker.data.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ShotDto(
    @Json(name = "id") val id: String?,
    @Json(name = "playerId") val playerId: String?,
    @Json(name = "ballSpeed") val ballSpeed: Double?,
    @Json(name = "launchAngle") val launchAngle: Double?,
    @Json(name = "carryDistance") val carryDistance: Double?,
    @Json(name = "spinRate") val spinRate: Double?,
    @Json(name = "clubType") val clubType: String?
)
