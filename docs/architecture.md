# Architecture

Start with a single `app` Gradle module. Split modules only when build time,
ownership, or dependency boundaries justify it.

The current base package is `com.example.itday`.

## Current package layout

```text
com.example.itday/
|-- MainActivity.kt
|-- core/
|   |-- config/        BuildConfig-backed app configuration
|   |-- data/          shared data source contracts and starter repositories
|   |   |-- mock/      Figma-based mock data source
|   |   `-- repository/feature repository contracts
|   |-- model/         UI-facing starter models used by mock data
|   `-- network/       network client setup
`-- ui/
    |-- state/         shared UI state model
    `-- theme/         Compose theme, colors, dimensions, radius, typography
```

Do not create empty package layers just to match a template. Add packages when a
real screen, contract, or implementation needs them.

## Planned feature layout

Create `feature/*` packages when each screen is implemented. Feature packages
should be feature-first and only contain the layers they actually need.

```text
feature/home/
|-- data/
|   |-- remote/
|   |-- model/
|   |-- mapper/
|   `-- repository/
|-- domain/
|   |-- model/
|   |-- repository/
|   `-- usecase/
`-- presentation/
    |-- component/
    |-- HomeScreen.kt
    |-- HomeViewModel.kt
    |-- HomeUiState.kt
    `-- HomeUiEvent.kt
```

If a feature only needs UI at first, create only `feature/<name>/presentation`.
Add `data` or `domain` later when real behavior requires them.

## Placement rules

- `core` contains shared, feature-neutral code only.
- `ui/theme` and `ui/state` contain app-wide Compose styling and shared UI
  state helpers.
- `presentation` depends on repository/domain contracts, never Retrofit DTOs.
- `data` implements repository contracts and owns DTO mapping. See
  `docs/repository-datasource.md` for Mock/Remote/Local boundaries.
- `domain` contains business rules and no Android dependencies. Add it only
  when the feature has meaningful business logic.
- A component stays inside its feature unless another feature genuinely shares
  it.
- Pass stable IDs or small immutable values through navigation.

## Data flow

```text
User action -> Screen callback -> ViewModel -> UseCase/Repository
                                      |
                                      v
                                 StateFlow<UiState>
                                      |
                                      v
                                   Compose UI
```

Use one immutable state per screen. Keep one-time navigation or snackbar effects
separate from persistent state.

## Current follow-up packages

The following packages are expected but should be added by their owning issues:

- `navigation/`: `#7` app navigation structure
- `di/`: `#11` dependency injection setup
- `feature/*`: `#19` to `#28` screen UI work
- `core/designsystem` or shared UI components: `#8` common Compose components