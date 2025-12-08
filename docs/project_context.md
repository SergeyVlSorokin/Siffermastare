---
project_name: 'Siffermastare'
user_name: 'Sergei'
date: '2025-01-27'
sections_completed:
  ['technology_stack', 'language_rules', 'framework_rules', 'testing_rules', 'quality_rules', 'workflow_rules', 'anti_patterns']
status: 'complete'
optimized_for_llm: true
---

# Project Context for AI Agents

_This file contains critical rules and patterns that AI agents must follow when implementing code in this project. Focus on unobvious details that agents might otherwise miss._

---

## Technology Stack & Versions

### Core Platform
- **Kotlin:** 1.9.0 (strictly enforced - 100% Kotlin requirement per PRD)
- **Android Gradle Plugin:** 8.6.1
- **Target SDK:** 34
- **Min SDK:** 30 (Android 11+)
- **Compile SDK:** 34
- **JVM Target:** 1.8 (for Java interop compatibility)

### UI Framework
- **Jetpack Compose:** BOM 2024.04.01 (all Compose libraries use BOM version)
- **Material Design 3:** Via Compose BOM
- **Kotlin Compiler Extension:** 1.5.1 (must match Compose BOM compatibility)
- **Activity Compose:** 1.9.1

### Architecture & Lifecycle
- **Lifecycle Runtime KTX:** 2.8.4
- **Core KTX:** 1.13.1

### Planned Dependencies (Not Yet Added)
- **Room:** 2.6.1 (for local persistence)
- **Hilt:** 2.50 (for dependency injection)
- **Coroutines & Flow:** 1.7.3 (for async operations)

### Testing
- **JUnit:** 4.13.2
- **AndroidX JUnit:** 1.2.1
- **Espresso Core:** 3.6.1
- **Compose UI Test:** Via Compose BOM

### Critical Version Constraints
- Compose BOM pins all Compose library versions - do not specify individual versions
- Kotlin 1.9.0 must remain compatible with AGP 8.6.1
- Min SDK 30 restricts use of newer Android APIs
- All Compose dependencies must use BOM version reference (no explicit versions)

## Critical Implementation Rules

### Language-Specific Rules

#### Code Style & Configuration
- **100% Kotlin requirement:** No Java code allowed (strictly enforced per PRD)
- **Official Kotlin code style:** Follow `kotlin.code.style=official` from gradle.properties
- **JVM Target 1.8:** Required for Java interop compatibility

#### Null Safety & Type System
- **Strict null safety:** Always use nullable types (`String?`) when values may be absent
- **Safe call operators:** Use `?.` and `?:` consistently for null handling
- **String for numeric answers:** Use `String` type for user input to preserve leading zeros (e.g., "0315" for time format, not `Int`)

#### Coroutines & Async Patterns
- **ViewModel scope:** Always use `viewModelScope` for coroutines in ViewModels
- **StateFlow over LiveData:** Prefer `StateFlow` for state management (architecture requirement)
- **Suspend functions:** Use `suspend` for all async operations (database, TTS)
- **Main thread:** Never block main thread - all I/O operations must be in coroutines

#### Error Handling
- **TTS failures:** Handle Swedish voice missing gracefully - show Snackbar linking to System Settings, never crash
- **Database errors:** Log persistence failures silently without blocking user flow
- **Result types:** Consider `Result<T>` or sealed classes for operation outcomes

#### Data Classes & Immutability
- **Domain models:** Use `data class` for `Question`, `LessonSession`, and other domain objects
- **Immutable collections:** Prefer immutable collections (`List`, `Map`) over mutable variants where possible

#### String Handling
- **Swedish number generation:** Use string templates and conversion logic for Swedish number strings
- **Answer comparison:** Compare user input as strings to handle leading zeros correctly

### Framework-Specific Rules

#### Compose Architecture
- **No XML layouts:** All UI must be built with Jetpack Compose (strictly enforced)
- **Material Design 3:** Use Material3 components and theming throughout
- **Dynamic color:** Support dynamic color scheme on Android 12+ (via `SiffermästareTheme`)

#### State Management Pattern
- **Single UiState:** ViewModels must expose ONE `UiState` data class via `StateFlow` (architecture requirement)
- **State observation:** Use `collectAsStateWithLifecycle()` in Composables to observe ViewModel state
- **MVVM strict adherence:** ViewModels hold state and business logic, Composables only display

#### Component Organization
- **Reusable components:** Place in `ui/components/` (e.g., `Numpad`, `FeedbackText`)
- **Screen-level Composables:** Organize by feature: `ui/home/`, `ui/lesson/`, `ui/summary/`
- **Theme components:** Color, Type, Theme in `ui/theme/`

#### Custom Numpad Component
- **Grid layout:** 1-3 (top row), 4-6 (middle row), 7-9 (third row), 0 (centered bottom)
- **Required buttons:** Digits 0-9, "Backspace", "Check" (primary action)
- **State-driven:** Update based on ViewModel state, not local state
- **Visual feedback:** Neutral (typing), Green (correct), Red (error) states

#### Theme & Styling
- **Theme wrapper:** Always use `SiffermästareTheme` for all screens
- **Swedish Minimalist aesthetic:** Clean lines, ample whitespace, distraction-free
- **Color palette:** Swedish flag colors (Blue #006AA7, Yellow #FECC00) as accents
- **Accessibility:** High contrast text (WCAG AA compliance)

#### Navigation
- **Navigation Compose:** Use when implemented (currently planned)
- **Screen structure:** `HomeScreen` → `LessonScreen` → `SummaryScreen`
- **Navigation Host:** Set up in MainActivity or App-level composable

#### Performance Requirements
- **Responsive UI:** All interactions must feel instantaneous (P95 < 200ms per PRD)
- **Lightweight:** Avoid heavy computations in Compose recomposition
- **State updates:** Minimize unnecessary recompositions through proper state hoisting

### Testing Rules

#### Test Structure & Organization
- **Unit tests:** Focus on `domain/` layer logic (generators, use cases, session manager)
- **UI tests:** Compose UI tests for Numpad interactions and screen navigation flows
- **Integration tests:** Use `FakeTTSManager` implementation to test lesson loop without physical speaker
- **Test location:** Unit tests in `app/src/test/`, UI tests in `app/src/androidTest/`
- **Package mirroring:** Mirror main source package structure in test directories

#### Mandatory Test Requirements
- **Lesson Logic:** Unit tests are MANDATORY for number generation and answer validation (per PRD)
- **Custom Numpad:** UI tests required for Numpad button interactions and state updates
- **TTS validation:** Manual "ear-testing" required for TTS output quality (cannot be automated)

#### Test Coverage Priorities
- **Number generators:** Verify `CardinalGenerator` produces correct Swedish strings (e.g., "105" → "hundrafem")
- **Session manager:** Verify `LessonSessionManager` calculates `avgTimePerQuestionMs` correctly
- **Answer validation:** Test with leading zeros (e.g., "0315" vs "315")
- **Visual feedback:** Test correct/incorrect state transitions in UI

#### Mocking Patterns
- **Mockk:** Use Mockk for mocking (when added - currently JUnit 4 only)
- **FakeTTSManager:** Create fake TTS implementation for integration tests without device dependency
- **Room database:** Mock Room database and DAOs for repository tests
- **ViewModel testing:** Use `TestDispatcher` for coroutine testing in ViewModels

#### Test Boundaries
- **Unit tests:** Pure Kotlin logic only - no Android dependencies
- **UI tests:** Compose UI interactions and state observation
- **Integration tests:** Full lesson flow with fake TTS, real ViewModel, mocked repository

### Code Quality & Style Rules

#### Code Organization
- **Clean Architecture layers:** Strict separation: `ui/` (presentation), `domain/` (business logic), `data/` (infrastructure)
- **Package structure:** `com.siffermastare.{layer}.{feature}` (e.g., `com.siffermastare.ui.lesson`)
- **Feature organization:** Group by feature within layers (e.g., `ui/home/`, `ui/lesson/`, `ui/summary/`)
- **Source tree:** Follow architecture document structure exactly

#### Naming Conventions
- **Files:** PascalCase for classes (e.g., `MainActivity.kt`, `LessonViewModel.kt`, `Numpad.kt`)
- **Packages:** Lowercase with dots (e.g., `com.siffermastare.domain.generators`)
- **Composables:** PascalCase (e.g., `Numpad`, `HomeScreen`, `LessonScreen`)
- **ViewModels:** `{Feature}ViewModel` suffix (e.g., `LessonViewModel`, `HomeViewModel`)
- **Use cases:** `{Action}UseCase` suffix (e.g., `GenerateLessonUseCase`, `SubmitAnswerUseCase`)
- **Repositories:** `{Entity}Repository` suffix (e.g., `LessonRepository`)

#### Documentation Standards
- **KDoc:** Required for public classes, functions, and complex logic
- **Inline comments:** Use for unobvious logic (e.g., Swedish number conversion algorithms)
- **Architecture docs:** Major decisions documented in `docs/architecture.md`

#### Architecture Boundaries
- **Domain layer:** Pure Kotlin only - NO Android dependencies allowed
- **UI layer:** Jetpack Compose only - NO business logic, only presentation
- **Data layer:** Android dependencies allowed (Room, TTS, System APIs)
- **Layer dependencies:** UI → Domain ← Data (Domain has no dependencies on other layers)

#### Code Style
- **Kotlin official style:** Enforced via `kotlin.code.style=official` in gradle.properties
- **Immutability:** Prefer `val` over `var`, immutable collections
- **Null safety:** Explicit nullable types, no `!!` operator unless absolutely necessary

### Development Workflow Rules

#### Repository Structure
- **Monorepo:** Single Git repository containing entire Android project
- **No backend:** 100% on-device MVP - no external API dependencies
- **Offline-first:** All functionality works without network connection

#### Dependency Management
- **Version catalogs:** Use `gradle/libs.versions.toml` for all dependency declarations
- **Compose BOM:** Always reference BOM for Compose libraries - never specify individual versions
- **New dependencies:** Add to version catalog first, then reference via `libs.` alias
- **Version consistency:** Keep versions in sync across all modules

#### Build System
- **Gradle:** Use Gradle with Kotlin DSL (`.gradle.kts` files)
- **AGP version:** 8.6.1 (must remain compatible with Kotlin 1.9.0)
- **Build configuration:** All build settings in `app/build.gradle.kts`
- **Settings:** Project-wide settings in `settings.gradle.kts` and `gradle.properties`

#### Deployment Considerations
- **No backend required:** MVP is completely offline - no cloud services needed
- **Local storage only:** All user data persisted via Room database on device
- **No authentication:** No user accounts or login required for MVP
- **TTS dependency:** Uses device's built-in TextToSpeech engine (no external audio files)

### Critical Don't-Miss Rules

#### Anti-Patterns to Avoid
- **NO XML layouts:** All UI must be Jetpack Compose - never create XML layout files
- **NO Java code:** 100% Kotlin requirement - any Java code violates PRD
- **NO business logic in UI:** Composables must only display state, never contain business logic
- **NO blocking main thread:** All I/O operations (database, TTS) must be in coroutines
- **NO LiveData:** Use `StateFlow` for state management - LiveData is not used in this project
- **NO individual Compose versions:** Always use BOM version reference, never specify individual library versions

#### Edge Cases to Handle
- **Leading zeros in answers:** Use `String` type for numeric answers to preserve leading zeros (e.g., "0315" for time, not `Int` 315)
- **Swedish TTS voice missing:** Show Snackbar with link to System Settings, never crash the app
- **Database write failures:** Log persistence errors silently without blocking user flow
- **Timer calculation:** Start timer when audio finishes speaking, not when question is generated
- **Answer validation:** Compare strings, not integers, to handle "0315" vs "315" correctly

#### Architecture Violations to Prevent
- **Android dependencies in domain layer:** Domain layer must be pure Kotlin - no Android imports
- **Business logic in Composables:** All logic belongs in ViewModels or Use Cases
- **ViewModels without UiState:** Every ViewModel must expose single `UiState` data class via `StateFlow`
- **Layer mixing:** UI layer cannot directly access Data layer - must go through Domain layer
- **Missing layer separation:** Respect Clean Architecture boundaries strictly

#### Performance Gotchas
- **Heavy computation in recomposition:** Avoid expensive operations in Compose functions
- **Unnecessary StateFlow emissions:** Only emit new state when actual changes occur
- **Missing lifecycle-aware collection:** Always use `collectAsStateWithLifecycle()` not `collectAsState()`
- **Blocking operations:** Never use `runBlocking` in production code

#### Security & Data Rules
- **No network calls:** MVP is 100% offline - any network code is a violation
- **No user data collection:** All data stays on device - no analytics or tracking
- **No external dependencies for TTS:** Use only device's built-in TextToSpeech API

---

## Usage Guidelines

**For AI Agents:**

- Read this file before implementing any code
- Follow ALL rules exactly as documented
- When in doubt, prefer the more restrictive option
- Update this file if new patterns emerge during implementation

**For Humans:**

- Keep this file lean and focused on agent needs
- Update when technology stack changes
- Review quarterly for outdated rules
- Remove rules that become obvious over time

**Last Updated:** 2025-01-27

