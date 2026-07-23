# Dependency injection

The project currently uses a manual DI container instead of Hilt or Koin.

## Why manual DI now

The app is still in the foundation phase. Server APIs and most feature
ViewModels are not finalized, so adding a DI framework now would add Gradle and
annotation-processing complexity before it provides enough value.

Manual DI gives us a stable composition point while keeping the implementation
small. Hilt or Koin can be introduced later when constructor graphs, scopes, and
feature modules become harder to manage manually.

## Entry point

`ItDayApplication` owns the app-wide container:

```kotlin
class ItDayApplication : Application() {
    lateinit var appContainer: AppContainer
        private set
}
```

`AndroidManifest.xml` registers this Application class with
`android:name=".ItDayApplication"`.

## Container

`AppContainer` exposes shared dependencies that screens and ViewModels can use:

```text
core/di/AppContainer.kt
core/di/DefaultAppContainer.kt
core/di/AppContainerProvider.kt
```

The default container currently provides:

- `OkHttpClient`
- `Retrofit`
- `ItDayMockDataSource`
- `LocalPreferencesDataSource`
- `AuthTokenStorage`, `AuthRemoteDataSource`, and `TokenRefresher`
- `HomeRepository`
- `MapRepository`
- `BarcodeRepository`
- `ReportRepository`
- `OnboardingRepository`
- `PaymentRepository`
- `SettingsRepository`

## Usage

Use `Context.appContainer` when Android code needs access to the container:

```kotlin
val repository = context.appContainer.homeRepository
```

Feature ViewModels should receive repository interfaces through constructors.
Do not instantiate repository implementations directly inside screens.

## Mock and remote switching

Current repository implementations use `ItDayMockDataSource` because server APIs
are not ready. When remote implementations are added, keep the public repository
interfaces stable and change `DefaultAppContainer` wiring based on
`AppConfig.useMockData` or feature-level data source policy.

## Testing

Tests can provide their own `AppContainer` implementation with fake repositories
or fake data sources. Keep `AppContainer` as an interface so test code does not
need to depend on `DefaultAppContainer`.

## Framework adoption criteria

Consider Hilt or Koin when at least one of these becomes true:

- Multiple feature ViewModels need scoped dependency graphs.
- Remote, local, and mock implementations require repeated manual wiring.
- Test replacement setup becomes difficult to maintain manually.
- Constructor dependency graphs become large enough that manual construction
  starts hiding bugs instead of making wiring explicit.
