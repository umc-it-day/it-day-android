# API result and error handling

Repositories return `ApiResult<T>` so screens can handle success and failure in a
consistent way regardless of whether the data came from mock, remote, or local
sources.

## Result type

```kotlin
ApiResult.Success(data)
ApiResult.Failure(error)
```

Use `ApiResult` at repository boundaries. Do not expose Retrofit `Response`, raw
exceptions, or server DTO wrappers to ViewModels or composables.

## Error model

```text
AppError.Network     Device/network connectivity or timeout failure
AppError.Server      Server responded with an error status or API error code
AppError.Auth        Login, token, or permission failure
AppError.Validation  Client-side validation failure
AppError.Unknown     Unexpected failure that does not fit another category
```

`AppError.Server` can carry `statusCode`, `errorCode`, and server `message` when
those fields become available. Server response bodies are not defined yet, so do
not lock Android code to a final API wrapper shape before the backend contract
exists.

## UI mapping

Use `ApiResult<T>.toUiState()` from `ui/state/UiStateMapper.kt` when a ViewModel
needs to expose `UiState<T>`.

```kotlin
val state = repository.getHomeData().toUiState()
```

For list-like screens, pass an empty predicate:

```kotlin
val state = repository.getMapPlaces().toUiState { places -> places.isEmpty() }
```

UI-facing messages are produced through `AppError.toUserMessage()`.

## Repository rules

- Repository functions return `ApiResult<T>`.
- Mock implementations return `ApiResult.Success(...)` unless the scenario is
  intentionally testing an error state.
- Remote implementations map transport exceptions and server errors to
  `AppError` at the repository or remote data source boundary.
- ViewModels should not inspect HTTP status codes directly.
- Composables should receive `UiState`, not `ApiResult`, unless there is a clear
  feature-specific reason.

## Future server wrapper

When the backend API contract is ready, add DTOs such as `ApiResponseDto<T>` in
remote data packages and map them into `ApiResult<T>`. Keep that wrapper out of
Repository interfaces.