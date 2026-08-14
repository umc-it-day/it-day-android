package com.umc.itday.core.data.repository

import com.umc.itday.core.data.result.ApiResult
import com.umc.itday.core.model.ItDayMapPlace
import com.umc.itday.core.model.ItDayOnboardingPage
import com.umc.itday.core.model.ItDayPaymentPlan
import com.umc.itday.core.model.ItDaySettingItem

typealias MapPlacesResult = ApiResult<List<ItDayMapPlace>>
typealias OnboardingPagesResult = ApiResult<List<ItDayOnboardingPage>>
typealias PaymentPlansResult = ApiResult<List<ItDayPaymentPlan>>
typealias SettingsItemsResult = ApiResult<List<ItDaySettingItem>>
