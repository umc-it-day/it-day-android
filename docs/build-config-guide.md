# BuildConfig Guide

This guide defines the app-level environment values exposed through `BuildConfig`.

## Values

Use these fields from app code when environment-dependent behavior is needed.

| Field | Type | Purpose |
| --- | --- | --- |
| `BuildConfig.BASE_URL` | `String` | Server base URL used by network setup |
| `BuildConfig.USE_MOCK` | `Boolean` | Whether mock data should be used |
| `BuildConfig.APP_ENV` | `String` | Current build environment, such as `debug` or `release` |

## Defaults

`debug` builds default to mock mode.

```kotlin
BuildConfig.USE_MOCK == true
BuildConfig.APP_ENV == "debug"
```

`release` builds default to remote mode.

```kotlin
BuildConfig.USE_MOCK == false
BuildConfig.APP_ENV == "release"
```

`BASE_URL` is empty by default. Set it through a non-committed local value or CI environment value.

## Local Setup

Add local values to `android/local.properties`.

```properties
SERVER_BASE_URL=https://example.com/
USE_MOCK=true
```

`local.properties` is ignored by Git and must not be committed.

Gradle properties and environment variables can also provide the same names:

```text
SERVER_BASE_URL
USE_MOCK
```

## Usage

Network setup should read the base URL from `BuildConfig`.

```kotlin
val baseUrl = BuildConfig.BASE_URL
```

Mock and remote data sources should keep the same UI-facing models so screens do not change when `USE_MOCK` changes.

## Secrets

Do not hardcode API keys, tokens, or production-only secrets in source files.

If a value is sensitive, provide it through `local.properties`, a Gradle property, or a CI secret.
