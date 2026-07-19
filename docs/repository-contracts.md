# Repository contracts

Repository contracts define what data each screen can request without exposing
where that data comes from.

```text
Screen -> ViewModel -> Repository -> DataSource
```

## Current contracts

| Contract | Purpose |
| --- | --- |
| `HomeRepository` | Home summary, next schedule, quick actions, and notices |
| `MapRepository` | Places and map-facing benefit information |
| `BarcodeRepository` | Membership barcode and available benefits |
| `ReportRepository` | Attendance, visit, savings, and highlight summaries |
| `OnboardingRepository` | Onboarding pages shown before main entry |
| `PaymentRepository` | Payment plan options |
| `SettingsRepository` | Settings menu items |

## Rules

- ViewModels depend on repository interfaces, not concrete data sources.
- Repository functions are `suspend` and return `ApiResult<T>` so mock and
  remote implementations can share the same contract.
- Repository implementations hide whether data comes from mock data, a remote
  API, local storage, or a combination of sources.
- Feature code must not read `ItDayMockData` directly except for Compose
  previews.
- Server DTOs should stay outside these contracts. Map DTOs to UI-facing or
  domain models before returning `ApiResult.Success`. Return failures as
  `ApiResult.Failure`.

## Current implementations

Repository implementations currently delegate to `ItDayMockDataSource` and wrap
results with `ApiResult.Success` so feature ViewModels can depend on repository
interfaces before server APIs are ready.

## Follow-up work

- Use `AppConfig.useMockData` when selecting mock or remote implementations.
- Add remote data sources and DTO mappers when server API contracts are ready.
- See `docs/api-result-error-handling.md` for result and error rules.