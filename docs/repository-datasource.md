# Repository and data source contracts

This project keeps screen code behind repository contracts so mock, remote, and
local data sources can be changed without rewriting Compose screens.

## Current backend status

Server APIs are not ready yet. The backend database structure is still being
designed, so Android should not treat current mock models as final API models.

## Data flow

```text
Compose screen -> ViewModel -> Repository -> DataSource
                                      |-> MockDataSource
                                      |-> RemoteDataSource
                                      `-> LocalDataSource
```

- Screens and ViewModels depend on repositories, not concrete data sources.
- Repository interfaces describe feature behavior, not transport details.
- Remote data sources own API calls and DTO handling.
- Local data sources own device storage.
- Mock data sources provide screen-oriented sample values until APIs are ready.

## Source types

```text
DataSourceType.Mock    Figma-based sample values and demo behavior
DataSourceType.Remote  Server API backed data
DataSourceType.Local   Device storage backed data
```

Use `DataSourcePolicy` to document the intended default source and optional
fallback for a feature area.

## Feature policies

| Area | Current policy | Notes |
| --- | --- | --- |
| Auth | Remote with Mock fallback | Kakao login is expected to exchange tokens with our server later, but auth details are not confirmed. |
| Onboarding | Local only | Store completion and selected values locally first. Server sync is a later concern. |
| Home | Mock only | Server composition is not defined yet. |
| StoreMap | Remote with Mock fallback | Server should provide store and benefit data. Kakao Map should only display map/location UI. |
| Barcode | Mock only | Server lookup is expected later, but MVP can remain demo/mock. |
| Report | Mock only | Server report data is not defined yet. |
| Settings | Local only | Current settings are app-local. |
| Payment | Mock only | Payment integration is later work. |

## Repository rules

- Add one repository contract per feature area when a screen or use case needs
  data access.
- Keep repository method names feature-oriented, for example `getHomeData()` or
  `getStoreMapPlaces()`, instead of API-path-oriented names.
- Do not expose Retrofit `Response`, DTOs, or data source implementations from a
  repository contract.
- Return UI/domain models that the screen can use safely. Add mappers when
  remote DTOs are introduced.
- Keep local-only behavior, such as onboarding completion or settings, out of
  remote data source classes.

## Model boundaries

```text
Remote DTO       Network response shape. Added after API contracts exist.
Domain model     Stable app behavior model. Add when business rules exist.
UI-facing model  Screen display shape. Current mock models live here for now.
```

Current `core/model/MockModels.kt` models are UI-facing starter models. They can
change while UI work progresses. After server APIs exist, add DTOs and mappers
instead of turning mock models into network response classes.

## Partial mock behavior

The app currently has one global `AppConfig.useMockData` flag. That is enough
while every API is unavailable.

When only some features move to the server, keep the same repository contracts
and change DI wiring per feature area:

```text
AuthRepository       Remote or Mock fallback
OnboardingRepository Local
StoreMapRepository   Remote or Mock fallback
BarcodeRepository    Mock until MVP scope changes
SettingsRepository   Local
```

Do not add many Gradle flags before there is a real need. Prefer feature-level
DI selection once server APIs become available.

## Follow-up work

- `#11` should wire repository implementations with dependency injection.
- `#12` should add Retrofit/OkHttp remote client setup.
- `#15` should define API response and error handling.
- UI issues can add feature repositories only when a screen actually needs data.
- `#58` should remove unused mock fields after UI screens are implemented.