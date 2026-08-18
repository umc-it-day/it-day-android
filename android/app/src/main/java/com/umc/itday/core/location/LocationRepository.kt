package com.umc.itday.core.location

data class LocationCoordinate(
    val latitude: Double,
    val longitude: Double,
)

interface LocationRepository {
    suspend fun getCurrentLocation(forceRefresh: Boolean = false): LocationCoordinate?

    suspend fun getAddress(
        latitude: Double,
        longitude: Double,
    ): String?
}
