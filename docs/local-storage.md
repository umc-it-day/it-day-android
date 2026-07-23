# Local storage

The app uses Preferences DataStore for small local key-value state.

## Stored in issue #17

`LocalPreferencesDataSource` owns app-local preferences that are not tied to a
server response:

- onboarding completed flag
- guest mode flag

These values are exposed as `Flow` so ViewModels and startup routing can observe
changes without blocking the main thread.

## Session clearing policy

`clearUserSessionPreferences()` clears only user-session state. It sets guest
mode to `false` and keeps the onboarding completed flag.

This matches the current product decision that onboarding should not restart on
logout or account switching. A full app-data reset can be added later as a
separate behavior if the settings screen needs it.

## Not stored here

Do not store a separate `isLoggedIn` boolean. Login state should be derived from
valid auth token state.

Access token, refresh token, authorization headers, and token refresh policy are
reserved for issue #18. `AuthTokenStorage` can use DataStore internally, but it
should stay separate from general local preferences.

## DI

`AppContainer` exposes `LocalPreferencesDataSource`, and
`DefaultAppContainer` wires it to the app-wide Preferences DataStore instance.
Feature ViewModels should depend on the interface instead of constructing
DataStore directly.