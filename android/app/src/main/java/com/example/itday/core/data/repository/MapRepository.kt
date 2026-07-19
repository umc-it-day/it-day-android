package com.example.itday.core.data.repository

interface MapRepository {
    suspend fun getMapPlaces(): MapPlacesResult
}
