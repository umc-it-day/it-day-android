package com.example.itday.core.data.repository

import com.example.itday.core.model.ItDayHomeData

interface HomeRepository {
    suspend fun getHomeData(): ItDayHomeData
}
