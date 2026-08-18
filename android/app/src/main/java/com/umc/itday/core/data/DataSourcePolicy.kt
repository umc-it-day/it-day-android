package com.umc.itday.core.data

data class DataSourcePolicy(
    val primary: DataSourceType,
    val fallback: DataSourceType? = null,
) {
    companion object {
        val mockOnly = DataSourcePolicy(primary = DataSourceType.Mock)
        val localOnly = DataSourcePolicy(primary = DataSourceType.Local)
        val remoteOnly = DataSourcePolicy(primary = DataSourceType.Remote)
        val remoteWithMockFallback =
            DataSourcePolicy(
                primary = DataSourceType.Remote,
                fallback = DataSourceType.Mock,
            )
    }
}

enum class FeatureDataArea(
    val defaultPolicy: DataSourcePolicy,
) {
    Auth(DataSourcePolicy.remoteWithMockFallback),
    Onboarding(DataSourcePolicy.localOnly),
    Home(DataSourcePolicy.mockOnly),
    StoreMap(DataSourcePolicy.remoteWithMockFallback),
    Barcode(DataSourcePolicy.mockOnly),
    Report(DataSourcePolicy.mockOnly),
    Settings(DataSourcePolicy.localOnly),
    Payment(DataSourcePolicy.mockOnly),
}
