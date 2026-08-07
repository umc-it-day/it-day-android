package com.example.itday.core.location

data class LocationCoordinate(
    val latitude: Double,
    val longitude: Double,
)

interface LocationRepository {
    suspend fun getCurrentLocation(forceRefresh: Boolean = false): LocationCoordinate?
}
