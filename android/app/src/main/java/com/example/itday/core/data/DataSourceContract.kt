package com.example.itday.core.data

enum class DataSourceType {
    Mock,
    Remote,
    Local,
}

interface DataSourceContract {
    val sourceType: DataSourceType
}

interface MockDataSourceContract : DataSourceContract {
    override val sourceType: DataSourceType
        get() = DataSourceType.Mock
}

interface RemoteDataSourceContract : DataSourceContract {
    override val sourceType: DataSourceType
        get() = DataSourceType.Remote
}

interface LocalDataSourceContract : DataSourceContract {
    override val sourceType: DataSourceType
        get() = DataSourceType.Local
}
