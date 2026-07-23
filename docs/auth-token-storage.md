# Authentication token storage

Issue #18 provides the app-side foundation for ITDAY server tokens.

## Token ownership

The Kakao SDK token is used only to authenticate with the ITDAY server. After
that exchange, the app stores the ITDAY access token and refresh token in the
existing Preferences DataStore.

Kakao login -> Kakao token -> ITDAY login API -> ITDAY access/refresh tokens

Kakao SDK integration and the login API belong to issue #29.

## Request and refresh flow

- AuthHeaderInterceptor adds an Authorization Bearer header to normal API
  requests when an access token exists.
- AuthTokenAuthenticator handles one 401 retry.
- TokenRefresher serializes concurrent refresh attempts, calls
  AuthRemoteDataSource, saves rotated access and refresh tokens, and returns
  the new access token.
- Authentication rejection clears both tokens. Transient network/server failures
  keep the current tokens so an outage does not sign the user out.
- A request that still receives 401 after refresh is not retried again.

The server endpoint is not defined yet. PendingAuthRemoteDataSource keeps this
boundary explicit. Replace it with a Retrofit implementation that uses a client
without AuthHeaderInterceptor or AuthTokenAuthenticator; otherwise refresh
requests can recursively trigger token refresh.

## Storage and security

Preferences DataStore stores both tokens atomically in the app-private
itday_preferences file. The file is excluded from cloud backup and
device-to-device transfer. Tokens must never be logged.

Preferences DataStore is not encrypted storage. If the production threat model
requires encryption at rest, add Android Keystore-backed encryption behind
AuthTokenStorage without changing callers.
