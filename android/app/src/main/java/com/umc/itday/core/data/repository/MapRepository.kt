package com.umc.itday.core.data.repository

interface MapRepository {
    suspend fun getMapPlaces(): MapPlacesResult
}
