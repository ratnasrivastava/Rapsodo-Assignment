package me.ratnasrivastava.golfperformancetracker.data.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PlayerDto(
    @Json(name = "id") val id: String?,
    @Json(name = "name") val name: String?,
    @Json(name = "club") val club: String?,
    @Json(name = "avgSpeed") val avgSpeed: Double?,
    @Json(name = "avgDistance") val avgDistance: Double?,
    @Json(name = "imageUrl") val imageUrl: String?
)
