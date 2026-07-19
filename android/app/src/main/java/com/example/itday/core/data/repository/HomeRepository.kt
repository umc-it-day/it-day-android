package com.example.itday.core.data.repository

import com.example.itday.core.data.result.ApiResult
import com.example.itday.core.model.ItDayHomeData

interface HomeRepository {
    suspend fun getHomeData(): ApiResult<ItDayHomeData>
}
