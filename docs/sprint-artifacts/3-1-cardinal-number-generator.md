# Story 3.1: Cardinal Number Generator

Status: Done

## Story

As a User,
I want to practice Swedish numbers 0-1000,
so that I can learn to recognize spoken cardinal numbers.

## Acceptance Criteria

1.  **Generator Interface**: Create a `NumberGenerator` interface to standardize lesson generation.
2.  **Cardinal Logic**: Implement `CardinalGenerator` that produces numbers within a configurable range (min, max).
3.  **TTS Strategy**: Use Android TTS to read digits directly (e.g., "21" -> "tjugoett") as a simplified initial approach.
4.  **Verification**: Unit tests must verify that the generator produces questions within bounds and with correct text properties.

## Tasks / Subtasks

- [x] Task 1: Create Domain Models (AC: 1)
  - [x] Create `Question` data class (`targetValue`, `spokenText`, `visualHint`)
  - [x] Create `NumberGenerator` interface

- [x] Task 2: Implement Cardinal Generator (AC: 2, 3)
  - [x] Create `CardinalGenerator(min, max)` implementation
  - [x] Implement logic to generate random numbers in range
  - [x] Set `spokenText` to digit string (e.g. "123") for TTS

- [x] Task 3: Unit Testing (AC: 4)
  - [x] Create `NumberGeneratorTest`
  - [x] Verify range bounds
  - [x] Verify list size
- [x] Task 4: Implement LessonSessionManager (AC: 1b)
  - [x] Move game loop logic from ViewModel to `LessonSessionManager`
  - [x] Integrate `NumberGenerator` into Manager

- [x] Task 5: Refactor LessonViewModel (AC: 1b)
  - [x] Inject `LessonSessionManager`
  - [x] Remove hardcoded random logic

- [x] Task 6: UI Integration (AC: 4)
  - [x] Update Navigation to support arguments (e.g., `lesson/{mode}`)
  - [x] Update HomeScreen with distinct buttons (0-10, 0-20, 0-100)
  - [x] Factory to pick `CardinalGenerator` based on mode

### Architecture Compliance
- **Domain Layer**: All logic resides in `domain/generators` and `domain/models`. Pure Kotlin, no Android dependencies (except `main` source set structure).
- **Strategy Pattern**: `NumberGenerator` interface allows swapping implementations (Cardinal, Ordinal, Time) easily.

### Decisions
- **TTS Simplified**: Per user decision, we are NOT mapping Int -> Swedish Words manually yet. We rely on the TTS engine's ability to read "123" as "hundratjugotre". This simplifies the logic significantly.

## Dev Agent Record

### Completion Notes List
- Implemented `Question` and `NumberGenerator` in `domain` package.
- Implemented `CardinalGenerator` using standard `Random`.
- Validated via `NumberGeneratorTest` (All Passed).

### File List
- `app/src/main/java/com/siffermastare/domain/models/Question.kt`
- `app/src/main/java/com/siffermastare/domain/generators/NumberGenerator.kt`
- `app/src/main/java/com/siffermastare/domain/generators/CardinalGenerator.kt`
- `app/src/test/java/com/siffermastare/domain/generators/NumberGeneratorTest.kt`
- `app/src/main/java/com/siffermastare/domain/LessonSessionManager.kt`
- `app/src/main/java/com/siffermastare/domain/generators/NumberGeneratorFactory.kt`
- `app/src/main/java/com/siffermastare/ui/lesson/LessonViewModel.kt`
- `app/src/main/java/com/siffermastare/ui/lesson/LessonScreen.kt`
- `app/src/main/java/com/siffermastare/ui/home/HomeScreen.kt`
- `app/src/main/java/com/siffermastare/ui/navigation/Screen.kt`
- `app/src/main/java/com/siffermastare/MainActivity.kt`
- `app/src/main/java/com/siffermastare/data/tts/TTSManager.kt`
