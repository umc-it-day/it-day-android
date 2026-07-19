package com.example.itday.core.data.repository

import com.example.itday.core.data.result.ApiResult
import com.example.itday.core.model.ItDayMapPlace
import com.example.itday.core.model.ItDayOnboardingPage
import com.example.itday.core.model.ItDayPaymentPlan
import com.example.itday.core.model.ItDaySettingItem

typealias MapPlacesResult = ApiResult<List<ItDayMapPlace>>
typealias OnboardingPagesResult = ApiResult<List<ItDayOnboardingPage>>
typealias PaymentPlansResult = ApiResult<List<ItDayPaymentPlan>>
typealias SettingsItemsResult = ApiResult<List<ItDaySettingItem>>
