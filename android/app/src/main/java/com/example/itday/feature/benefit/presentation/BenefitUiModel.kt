package com.example.itday.feature.benefit.presentation

enum class CarrierUiModel(
    val label: String,
) {
    SKT("SKT"),
    KT("KT"),
    LGU_PLUS("LG U+"),
}

data class BenefitUiModel(
    val id: String,
    val storeName: String,
    val category: String,
    val carrier: CarrierUiModel,
    val summary: String,
)

internal fun List<BenefitUiModel>.filteredBy(
    carrier: CarrierUiModel,
    category: String,
): List<BenefitUiModel> =
    filter { benefit ->
        benefit.carrier == carrier && (category == ALL_CATEGORY || benefit.category == category)
    }

internal const val ALL_CATEGORY = "전체"

val benefitCategories = listOf(ALL_CATEGORY, "편의점", "카페", "외식", "문화")

val benefitMockItems =
    listOf(
        BenefitUiModel("1", "CU", "편의점", CarrierUiModel.SKT, "VIP 1천 원당 100원 할인"),
        BenefitUiModel("2", "스타벅스", "카페", CarrierUiModel.SKT, "아메리카노 사이즈 업"),
        BenefitUiModel("3", "GS25", "편의점", CarrierUiModel.KT, "일부 상품 10% 할인"),
        BenefitUiModel("4", "파리바게뜨", "외식", CarrierUiModel.KT, "1천 원당 100원 할인"),
        BenefitUiModel("5", "롯데시네마", "문화", CarrierUiModel.LGU_PLUS, "영화 예매 최대 4천 원 할인"),
    )
