package com.umc.itday.feature.barcode.presentation

enum class BarcodeStep {
    Intro,
    Form,
    Success,
    Duplicate,
}

enum class Carrier(val displayName: String) {
    SKT("SKT"),
    KT("KT"),
    LGU_PLUS("LGU+"),
}

enum class MembershipGrade(val displayName: String) {
    VIP("VIP"),
    GOLD("골드"),
    SILVER("실버"),
    BASIC("베이직"),
}

data class BarcodeRegistrationUiState(
    val step: BarcodeStep = BarcodeStep.Intro,
    val selectedCarrier: Carrier = Carrier.SKT,
    val selectedGrade: MembershipGrade = MembershipGrade.GOLD,
    val barcodeNumber: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
) {
    val isValidLength: Boolean
        get() = barcodeNumber.length == 16

    val formattedBarcodeNumber: String
        get() {
            if (barcodeNumber.isEmpty()) return ""
            return barcodeNumber.chunked(4).joinToString(" ")
        }

    val maskedBarcodeNumber: String
        get() {
            if (barcodeNumber.length < 16) return formattedBarcodeNumber
            val prefix = barcodeNumber.take(12).chunked(4).joinToString(" ")
            return "$prefix ****"
        }

    val registeredBadgeText: String
        get() = "${selectedCarrier.displayName} ${selectedGrade.displayName}"
}
