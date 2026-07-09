# AI collaboration prompt guide

Use this guide when asking an AI assistant to create issues, plan work, change
Android code, write commits, or prepare pull requests for It-Day.

## Project context

It-Day is a location-based Android service for carrier membership benefits and
membership barcodes. The confirmed Android stack is Kotlin, Jetpack Compose, and
Material 3.

Before changing code, read `android/AGENTS.md` and the document that matches the
work area:

- Architecture: `docs/architecture.md`
- UI and design system: `docs/design-system.md`
- Kotlin and Compose conventions: `docs/coding-conventions.md`
- API integration: `docs/api-integration.md`
- Testing: `docs/testing.md`
- Git and review workflow: `docs/git-workflow.md`

## Implementation rules

- Follow the feature-first structure described in `docs/architecture.md`.
- Do not create empty `data`, `domain`, or `presentation` layers.
- Prefer existing project patterns over new abstractions.
- Keep Compose UI stateless where possible and pass data with callbacks.
- Use immutable screen state and `StateFlow` when a ViewModel is needed.
- Put shared UI in `core/designsystem` only when it is genuinely reusable.
- Keep feature-specific UI inside the owning feature package.
- Do not assume Retrofit, Hilt, Room, ktlint, detekt, or JaCoCo are configured;
  check Gradle files before using them.
- Do not add libraries unless the task requires them and the reason is clear.

## Security and privacy

- Never commit API keys, tokens, barcodes, location data, or personal
  information.
- Keep base URLs and secrets in local properties or CI secrets.
- Do not log tokens, barcodes, location data, or personal information.
- Do not use real network, map, location, clock, or random dependencies in unit
  tests.

## Issue rules

Use the templates in `.github/ISSUE_TEMPLATE`.

Issue titles should start with one of these prefixes:

- `[Bug]` for bug reports and fixes
- `[Feature]` for new feature work
- `[Refactor]` for code structure improvements
- `[Chore]` for build, configuration, or maintenance work
- `[Design]` for design or technical decisions

An issue should include:

- Summary
- Goal
- Task checklist
- Impact scope
- Definition of Done
- Suggested labels, including a status label when useful

## Branch, commit, and PR rules

Branches use:

```text
<type>/<kebab-case-description>
```

Examples:

```text
feature/home-screen
fix/login-token-expired
chore/gradle-setting
```

Commits and PR titles use:

```text
<type>: <Korean subject>
<type>(<scope>): <Korean subject>
```

Allowed types are:

```text
feat, fix, docs, refactor, test, chore, ci, perf
```

Rules:

- Keep the subject within 72 characters.
- Do not end the subject with a period.
- Use Korean for the subject.
- Link completed issues with `Closes #123` or `Fixes #123`.
- Include behavior changes and verification in the PR body.
- Attach screenshots for UI changes.
- Keep reviewable PR additions under 180 lines when possible and under 200
  lines unless the workflow explicitly skips the limit.

## Verification rules

Run the most relevant available check after making changes.

For Android work on Windows, prefer:

```text
cd android
gradlew.bat testDebugUnitTest
gradlew.bat assembleDebug
```

Only run ktlint, detekt, or JaCoCo tasks after confirming they are configured.
If a check is not configured or cannot run in the current environment, report
that clearly instead of adding tools without a task requirement.

## Reusable prompt

```text
You are working in the It-Day Android repository.

Project context:
- It-Day is a location-based carrier membership benefits Android app.
- The Android stack is Kotlin, Jetpack Compose, and Material 3.
- Before changing code, read android/AGENTS.md and the relevant docs file.

Implementation rules:
- Follow the feature-first architecture.
- Do not create empty layers.
- Prefer existing project patterns.
- Keep Compose UI stateless where possible.
- Use immutable UiState and StateFlow when a ViewModel is needed.
- Do not assume unconfigured libraries or tools are available.
- Do not commit or log API keys, tokens, barcodes, location data, or personal
  information.

Collaboration rules:
- Use issue templates from .github/ISSUE_TEMPLATE.
- Use branch names like <type>/<kebab-case-description>.
- Use commit and PR titles like <type>: <Korean subject>.
- Allowed types are feat, fix, docs, refactor, test, chore, ci, and perf.
- Keep commit and PR titles within 72 characters and do not end them with a
  period.
- Link completed issues with Closes #123 or Fixes #123.
- Keep PRs small, preferably under 180 reviewable added lines.

Workflow:
1. Inspect the current project structure and relevant documents.
2. Make the smallest change that satisfies the task.
3. Run the most relevant configured verification.
4. Summarize changed files, verification results, and remaining risks.
```

## Issue draft for this guide

```text
Title: [Chore] AI 협업 프롬프트 가이드 작성
Labels: chore, status: todo

## 작업 내용
> 프로젝트 지침과 GitHub 협업 규칙을 기반으로 AI 작업 프롬프트 가이드를 작성합니다.

## 목적
- AI에게 Android 작업을 맡길 때 확인해야 할 프로젝트 규칙을 문서화합니다.
- 이슈, 브랜치, 커밋, PR 규칙을 프롬프트에 포함해 협업 흐름을 일관되게 유지합니다.
- 기존 `android/AGENTS.md`, `docs/*`, `.github/*` 템플릿과 CI 규칙을 참고합니다.

## 작업 리스트
- [ ] 기존 프로젝트 지침과 문서 확인
- [ ] AI 협업 프롬프트 가이드 문서 추가
- [ ] 이슈/커밋/PR 규칙 반영
- [ ] 검증 결과 기록

## 영향 범위
- 문서 변경만 포함합니다.
- Android 앱 동작과 빌드 설정에는 영향을 주지 않습니다.

## 완료 조건
- [ ] `docs/ai-collaboration-prompt.md` 문서 추가
- [ ] 기존 지침 파일과 GitHub 규칙 반영
- [ ] PR 본문에 관련 이슈와 검증 결과 포함
```