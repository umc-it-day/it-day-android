package com.umc.itday.feature.barcode.data.model

import kotlinx.serialization.Serializable

@Serializable
data class BarcodeDataDto(
    val barcodeNum: String,
)

@Serializable
data class LotteryDataDto(
    val lotteryNum: String,
)

@Serializable
data class BarcodeNumberRequestDto(
    val barcodeNum: String,
)


@Serializable
data class BarcodeUsageRequestDto(
    val storeId: Long,
)
