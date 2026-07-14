# Mock data

Mock data is based on the current Figma screens and exists to unblock UI
implementation before the server API is ready.

## Figma references

- https://www.figma.com/design/RUPsQ73vDE8Mat3Wn69xfn?node-id=396-4433
- https://www.figma.com/design/sJDUlqBoBqaOKtNe54hQON?node-id=

Use these references as the source for screen-oriented sample data. If either
Figma file changes, update the mock values and UI-facing models together.

## Source files

```text
core/model/MockModels.kt
core/data/mock/ItDayMockData.kt
core/data/mock/ItDayMockDataSource.kt
```

`ItDayMockData` exposes reusable sample values for Compose previews and local UI
checks. `ItDayMockDataSource` wraps those values behind functions that can later
be replaced by repository and remote data source implementations.

## Current coverage

- Home
- Map
- Barcode
- Report
- Onboarding
- Payment plans
- Settings

## Rules

- Treat these models as UI-facing sample models, not final server DTOs.
- Keep feature screens from reading `ItDayMockData` directly unless it is only
  for a Compose preview.
- Runtime code should go through `ItDayMockDataSource` until repository
  contracts are added.
- When the server API is ready, add DTOs and mappers instead of reshaping these
  files into network models.

## Follow-up work

- `#16` defines repository and data source contracts. See `docs/repository-datasource.md`.
- `#11` should inject mock or remote implementations using
  `AppConfig.useMockData`.
- Feature UI issues can import these samples for previews while the API is not
  available.