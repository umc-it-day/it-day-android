package com.example.itday.core.data.mock

import com.example.itday.core.data.MockDataSourceContract
import com.example.itday.core.model.ItDayBarcodeData
import com.example.itday.core.model.ItDayHomeData
import com.example.itday.core.model.ItDayMapPlace
import com.example.itday.core.model.ItDayOnboardingPage
import com.example.itday.core.model.ItDayPaymentPlan
import com.example.itday.core.model.ItDayReportData
import com.example.itday.core.model.ItDaySettingItem

class ItDayMockDataSource : MockDataSourceContract {
    fun getHomeData(): ItDayHomeData = ItDayMockData.homeData

    fun getMapPlaces(): List<ItDayMapPlace> = ItDayMockData.mapPlaces

    fun getBarcodeData(): ItDayBarcodeData = ItDayMockData.barcodeData

    fun getReportData(): ItDayReportData = ItDayMockData.reportData

    fun getOnboardingPages(): List<ItDayOnboardingPage> = ItDayMockData.onboardingPages

    fun getPaymentPlans(): List<ItDayPaymentPlan> = ItDayMockData.paymentPlans

    fun getSettingItems(): List<ItDaySettingItem> = ItDayMockData.settingItems
}
