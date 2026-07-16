# App Navigation Policy

## Scope

This document defines the initial navigation structure for the mock-based app flow. Full screen UI implementation is handled by separate feature issues.

## Route Structure

- `SPLASH`: App launch gate. It checks app state and routes to the next destination.
- `LOGIN`: Start and login flow entry.
- `ONBOARDING`: First-use onboarding flow.
- `MAIN`: Container route for the bottom tab area.
- `HOME`: Home tab inside `MAIN`.
- `MAP`: Map tab inside `MAIN`.
- `REPORT`: Report tab inside `MAIN`.
- `SETTINGS`: Settings tab inside `MAIN`.
- `BARCODE`: Non-tab route for barcode-related flow.

`MAIN` is not a standalone visible screen. It hosts `MainTabScaffold`, whose default tab is `HOME`.

## Entry Flow

Current mock flow:

```text
SPLASH -> LOGIN -> ONBOARDING -> MAIN -> HOME
```

Future app-state-based flow:

```text
SPLASH
-> not logged in: LOGIN
-> logged in and onboarding not completed: ONBOARDING
-> logged in and onboarding completed: MAIN
```

When leaving `SPLASH`, the splash route is removed from the back stack so users do not return to it with the back button.

## Bottom Tabs

The bottom tab area contains four tabs:

- `HOME`
- `MAP`
- `REPORT`
- `SETTINGS`

`BARCODE` is intentionally not a bottom tab. It can be opened later from an action such as a home quick action.

## Back Behavior

- `SPLASH`: No user back navigation target. It automatically routes onward.
- `LOGIN`: Back exits the app or returns the user outside the app.
- `ONBOARDING`: Back returns to `LOGIN` during the mock flow.
- `MAIN` / `HOME`: Back follows the system default app-exit behavior.
- `MAIN` / non-home tab: Back moves to the `HOME` tab.
- Detail or flow screens: Back returns to the previous screen in that flow.

The "press back twice to exit" toast is a future UX detail and is not implemented in this issue.

## Tab State Policy

Bottom tab navigation uses `launchSingleTop`, `popUpTo`, `saveState`, and `restoreState` so repeated tab clicks do not create duplicate destinations and tab state can be restored where supported.
