# Environment configuration

Use project-local properties for It-Day build-time environment values. Do not
commit API keys, tokens, private server URLs, or `android/itday.properties`.

## BuildConfig fields

The app module exposes these values through `BuildConfig`:

| Field | Type | Debug default | Release default | Purpose |
| --- | --- | --- | --- | --- |
| `API_BASE_URL` | `String` | `http://10.0.2.2:8080/` | empty string | Server API base URL |
| `USE_MOCK_DATA` | `Boolean` | `true` | `false` | Select mock or remote data source |
| `APP_ENV` | `String` | `debug` | `release` | Build environment label |

## Local setup

Copy the example file and fill values for your local environment:

```text
copy android\itday.properties.example android\itday.properties
```

`android/itday.properties` is ignored by Git. It is separate from
`android/local.properties`, so Android Studio can keep managing `sdk.dir`.

```properties
ITDAY_DEBUG_API_BASE_URL=http://10.0.2.2:8080/
ITDAY_DEBUG_USE_MOCK_DATA=true
ITDAY_RELEASE_API_BASE_URL=
ITDAY_RELEASE_USE_MOCK_DATA=false
```

## Property priority

Values are resolved in this order:

1. Gradle `-P` command-line property
2. Environment variable
3. `android/itday.properties`
4. Default value in `app/build.gradle.kts`

Command-line example:

```text
gradlew.bat assembleDebug -PITDAY_DEBUG_USE_MOCK_DATA=false -PITDAY_DEBUG_API_BASE_URL=http://10.0.2.2:8080/
```

## Current policy

- Server APIs are still being prepared, so debug builds use mock data by
  default.
- Release builds default to remote data, but the release API URL must be
  provided before release distribution.
- Secrets must come from `android/itday.properties`, Gradle `-P` properties,
  environment variables, or CI secrets, never from committed source files.