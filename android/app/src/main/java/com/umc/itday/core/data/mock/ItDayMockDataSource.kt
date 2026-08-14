package com.umc.itday.core.data.mock

import com.umc.itday.core.data.MockDataSourceContract
import com.umc.itday.core.model.ItDayBarcodeData
import com.umc.itday.core.model.ItDayHomeData
import com.umc.itday.core.model.ItDayMapPlace
import com.umc.itday.core.model.ItDayOnboardingPage
import com.umc.itday.core.model.ItDayPaymentPlan
import com.umc.itday.core.model.ItDayReportData
import com.umc.itday.core.model.ItDaySettingItem

class ItDayMockDataSource : MockDataSourceContract {
    fun getHomeData(): ItDayHomeData = ItDayMockData.homeData

    fun getMapPlaces(): List<ItDayMapPlace> = ItDayMockData.mapPlaces

    fun getBarcodeData(): ItDayBarcodeData = ItDayMockData.barcodeData

    fun getReportData(): ItDayReportData = ItDayMockData.reportData

    fun getOnboardingPages(): List<ItDayOnboardingPage> = ItDayMockData.onboardingPages

    fun getPaymentPlans(): List<ItDayPaymentPlan> = ItDayMockData.paymentPlans

    fun getSettingItems(): List<ItDaySettingItem> = ItDayMockData.settingItems
}
