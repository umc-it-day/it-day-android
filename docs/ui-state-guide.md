# UI State Guide

This guide defines how screens represent loading, content, empty, error, and one-time UI effects.

## Common State

Use `UiState<T>` for screens that load data asynchronously.

```kotlin
UiState<HomeUiModel>
UiState<ReportUiModel>
UiState<List<StoreUiModel>>
```

- `Loading`: data is being loaded or refreshed before content is available.
- `Success`: the screen has renderable data.
- `Empty`: the request succeeded, but there is no meaningful content to show.
- `Error`: the screen cannot show content because loading failed.

Do not represent the same screen with separate flags such as `isLoading`, `isError`, and nullable content when `UiState<T>` can express it clearly.

## Screen UiModel

Each screen should define a presentation model when the content has more than one value.

```kotlin
data class HomeUiModel(
    val barcodeEnabled: Boolean,
    val benefits: List<BenefitUiModel>,
    val brandDays: List<BrandDayUiModel>,
)
```

- Keep screen rendering data in `FeatureUiModel` or `FeatureUiState`.
- Do not pass Retrofit DTOs directly to Compose screens.
- Convert remote or domain models before they reach the UI layer.
- Keep the model immutable with `val` and read-only collections.
- Add only values needed to render the screen.

## Empty State

Use `Empty` when loading succeeds but the user has nothing to act on.

Examples from the Figma order page:

- Home: no registered membership or benefit data.
- Report: no report or challenge history.
- Map: no nearby partner stores or no search results.
- Barcode registration: no available barcode data after scanning or lookup.
- Notification-like lists: no items to display.

Use `Success(emptyList())` only when an empty list is still a valid content state and the screen does not need a separate empty UI.

## Screen Examples

Use the Figma order page as the initial screen list. Exact fields should be finalized in each feature PR.

| Screen | Recommended state |
| --- | --- |
| Start | local state or `UiState<StartUiModel>` if remote config is needed |
| Onboarding | `UiState<OnboardingUiModel>` when loading selectable data |
| Home | `UiState<HomeUiModel>` |
| Barcode registration | `UiState<BarcodeRegistrationUiModel>` |
| Report | `UiState<ReportUiModel>` |
| Map | `UiState<MapUiModel>` or `UiState<List<StoreUiModel>>` |
| Setting | local state unless account or app settings are loaded |
| Payment | `UiState<PaymentUiModel>` for payment product or membership data |

## One-Time Events

Do not put one-time effects in `UiState`.

One-time effects include:

- navigation
- toast or snackbar
- permission request
- payment sheet launch
- barcode registration completion
- dialog that should appear only once

Represent these with a separate event type owned by each feature.

```kotlin
sealed interface HomeUiEvent {
    data class ShowSnackbar(val message: String) : HomeUiEvent
    data object NavigateToBarcodeRegistration : HomeUiEvent
}
```

In Compose, collect events from the ViewModel and handle them in `LaunchedEffect`.

Persistent screen information stays in `UiState`; actions that should happen once stay in `UiEvent`.
