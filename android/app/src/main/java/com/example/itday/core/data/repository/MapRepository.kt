package com.example.itday.core.data.repository

import com.example.itday.core.model.ItDayMapPlace

interface MapRepository {
    suspend fun getMapPlaces(): List<ItDayMapPlace>
}
