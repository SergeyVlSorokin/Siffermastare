# Story 1.1: Project Initialization & Navigation

Status: ready-for-dev

## Story

As a Developer,
I want to set up the Android project with Jetpack Compose and Navigation,
so that we have a stable foundation.

## Acceptance Criteria

1. New Android Project created using Kotlin & Jetpack Compose.
2. Navigation Host set up with two empty screens: `HomeScreen` and `LessonScreen`.
3. "Start Debug Lesson" button on Home navigates to Lesson.

## Tasks / Subtasks

- [ ] Task 1: Set up Navigation Compose infrastructure (AC: 2, 3)
  - [ ] Add Navigation Compose dependency (already in libs.versions.toml)
  - [ ] Create Navigation Host in MainActivity or App-level composable
  - [ ] Define navigation routes/sealed class for screen destinations
  - [ ] Set up NavController and NavHost

- [ ] Task 2: Create HomeScreen composable (AC: 2, 3)
  - [ ] Create `ui/home/HomeScreen.kt` following architecture structure
  - [ ] Create empty HomeScreen composable with SiffermästareTheme wrapper
  - [ ] Add "Start Debug Lesson" button (Material3 Button component)
  - [ ] Implement navigation action to LessonScreen on button click

- [ ] Task 3: Create LessonScreen composable (AC: 2)
  - [ ] Create `ui/lesson/LessonScreen.kt` following architecture structure
  - [ ] Create empty LessonScreen composable with SiffermästareTheme wrapper
  - [ ] Add placeholder content (will be implemented in Story 1.2)

- [ ] Task 4: Update MainActivity to use Navigation (AC: 2, 3)
  - [ ] Replace current Greeting composable with Navigation Host
  - [ ] Set HomeScreen as start destination
  - [ ] Verify navigation flow works

## Dev Notes

### Architecture Compliance

**Source Tree Structure:** [Source: docs/architecture.md#Source Tree Structure]
- Create files in `app/src/main/java/com/siffermastare/ui/home/` for HomeScreen
- Create files in `app/src/main/java/com/siffermastare/ui/lesson/` for LessonScreen
- Follow Clean Architecture: UI layer only, no business logic in Composables

**MVVM Pattern:** [Source: docs/architecture.md#High-Level Architecture]
- ViewModels will be added in later stories (Story 1.3+)
- For now, screens are simple Composables with navigation only
- No ViewModel needed for this story - just navigation setup

**Navigation Pattern:** [Source: docs/project_context.md#Navigation]
- Use Navigation Compose 2.8.0 (already configured in dependencies)
- Screen structure: `HomeScreen` → `LessonScreen` → `SummaryScreen` (SummaryScreen in later epic)
- Navigation Host set up in MainActivity or App-level composable

### Technical Requirements

**Dependencies Already Configured:**
- Navigation Compose: `androidx.navigation:navigation-compose:2.8.0` [Source: gradle/libs.versions.toml]
- Jetpack Compose BOM: 2024.04.01 [Source: gradle/libs.versions.toml]
- Material Design 3: Via Compose BOM [Source: docs/project_context.md#UI Framework]

**Compose Requirements:** [Source: docs/project_context.md#Framework-Specific Rules]
- All UI must be Jetpack Compose (NO XML layouts)
- Use Material Design 3 components
- Always wrap screens with `SiffermästareTheme` wrapper
- Use `collectAsStateWithLifecycle()` for state observation (not needed yet, but pattern to follow)

**Theme & Styling:** [Source: docs/project_context.md#Theme & Styling]
- Use `SiffermästareTheme` for all screens
- Swedish Minimalist aesthetic: clean lines, ample whitespace
- Color palette: Swedish flag colors (Blue #006AA7, Yellow #FECC00) as accents
- High contrast text for accessibility (WCAG AA)

**Package Structure:** [Source: docs/project_context.md#Code Organization]
- Package: `com.siffermastare.ui.home` for HomeScreen
- Package: `com.siffermastare.ui.lesson` for LessonScreen
- Follow naming: PascalCase for Composables (`HomeScreen`, `LessonScreen`)

### File Structure Requirements

**Files to Create:**
```
app/src/main/java/com/siffermastare/
└── ui/
    ├── home/
    │   └── HomeScreen.kt
    └── lesson/
        └── LessonScreen.kt
```

**Files to Modify:**
```
app/src/main/java/com/siffermastare/
└── MainActivity.kt  (replace Greeting with Navigation Host)
```

### Navigation Implementation Details

**Navigation Routes:**
- Use sealed class or object for route definitions
- Example pattern:
  ```kotlin
  sealed class Screen(val route: String) {
      object Home : Screen("home")
      object Lesson : Screen("lesson")
  }
  ```

**NavHost Setup:**
- Create NavController using `rememberNavController()`
- Set up NavHost with start destination = Home route
- Use `composable()` DSL for each screen
- Pass NavController to screens that need navigation

**Button Implementation:**
- Use Material3 `Button` component
- Text: "Start Debug Lesson"
- OnClick: Navigate to LessonScreen route
- Follow Swedish Minimalist styling (clean, simple)

### Testing Requirements

**No Unit Tests Required for This Story:**
- This is infrastructure setup
- Navigation will be tested manually
- UI tests for navigation can be added in later stories

**Manual Testing Checklist:**
- [ ] App launches and shows HomeScreen
- [ ] "Start Debug Lesson" button is visible
- [ ] Clicking button navigates to LessonScreen
- [ ] LessonScreen displays (empty for now)
- [ ] Back navigation works (system back button)

### Project Structure Notes

**Alignment with Architecture:**
- ✅ Follows source tree structure from architecture.md
- ✅ UI layer only, no domain or data layer needed yet
- ✅ Package structure matches `com.siffermastare.ui.{feature}` pattern

**Dependencies:**
- ✅ Navigation Compose already in dependencies
- ✅ No new dependencies needed for this story

### Critical Don't-Miss Rules

**Anti-Patterns to Avoid:** [Source: docs/project_context.md#Anti-Patterns]
- ❌ NO XML layouts - all UI must be Compose
- ❌ NO business logic in Composables - keep screens simple
- ❌ NO ViewModels yet - just navigation setup

**Edge Cases:**
- Handle back navigation properly (system handles by default with NavController)
- Ensure theme is applied to all screens

### References

- **PRD:** [Source: docs/prd/epic-details.md#Epic 1: Foundation & Core Loop]
- **Architecture:** [Source: docs/architecture.md#Source Tree Structure]
- **Project Context:** [Source: docs/project_context.md]
- **Navigation Compose Docs:** https://developer.android.com/jetpack/compose/navigation

## Dev Agent Record

### Context Reference

- PRD: docs/prd/epic-details.md (Epic 1, Story 1.1)
- Architecture: docs/architecture.md
- Project Context: docs/project_context.md
- Dependencies: gradle/libs.versions.toml, app/build.gradle.kts

### Agent Model Used

_To be filled by dev agent_

### Debug Log References

_To be filled during implementation_

### Completion Notes List

_To be filled when story is complete_

### File List

_To be filled when story is complete_

