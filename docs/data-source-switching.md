# Data source switching

Use `AppConfig.useMockData` as the single application-level flag for selecting
mock or remote data sources.

## Current source

`AppConfig` reads values generated from `BuildConfig`:

```kotlin
AppConfig.apiBaseUrl
AppConfig.useMockData
AppConfig.appEnv
```

The underlying `BuildConfig` values are configured in `app/build.gradle.kts` and
can be overridden with Gradle `-P`, environment variables, or
`android/itday.properties`. See `docs/environment.md` for setup details.

## Selection rule

Repository or DI setup should select implementations using this rule:

```text
AppConfig.useMockData == true  -> MockDataSource
AppConfig.useMockData == false -> RemoteDataSource
```

Do not read `BuildConfig.USE_MOCK_DATA` directly from feature code. Use
`AppConfig` so the switching rule stays centralized.

## Follow-up work

- `#14` defines mock data and MockDataSource implementations. See `docs/mock-data.md`.
- `#16` defines repository and DataSource contracts.
- `#11` can wire concrete implementations through dependency injection.
