package com.umc.itday.core.data.repository

import com.umc.itday.core.data.result.ApiResult
import com.umc.itday.core.model.ItDayHomeData

interface HomeRepository {
    suspend fun getHomeData(): ApiResult<ItDayHomeData>
}
