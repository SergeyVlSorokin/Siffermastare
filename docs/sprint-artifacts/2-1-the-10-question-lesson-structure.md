# Story 2.1: The 10-Question Lesson Structure

Status: done

## Story

As a User,
I want the lesson to end after 10 questions,
so that I have a defined session length.

## Acceptance Criteria

1.  Track `questionCount` (1/10).
2.  UI shows progress "Question X of 10".
3.  After 10th correct answer, navigate to a new `SummaryScreen`.

## Tasks / Subtasks

- [ ] Task 1: Refactor Logic to ViewModel (Arch Requirement)
  - [ ] Create `LessonViewModel`
  - [ ] Move state `targetNumber`, `currentInput` to ViewModel
  - [ ] Move game loop logic (`checkAnswer`, `generateNumber`) to ViewModel
  - [ ] Expose UI state via `StateFlow` or `Compose State`

- [ ] Task 2: Implement Progress Tracking (AC: 1)
  - [ ] Add `questionCount` state (starts at 1)
  - [ ] Increment count on correct answer
  - [ ] Reset count when lesson starts/restarts

- [ ] Task 3: UI Progress Indicator (AC: 2)
  - [ ] Display "Question X / 10" in LessonScreen
  - [ ] Style using Swedish Minimalist header style
  - [ ] Optional: Simple valid CSS/Material progress bar if time permits

- [ ] Task 4: Summary Screen Creation (AC: 3)
  - [ ] Create `SummaryScreen` composable (placeholder for now)
  - [ ] Add navigation route `Screen.Summary`
  - [ ] Implement "Back to Home" button on Summary

- [ ] Task 5: Navigation Logic (AC: 3)
  - [ ] Detect when `questionCount` > 10 (or == 10 and answered correctly)
  - [ ] Navigate to `SummaryScreen`
  - [ ] Clear/Reset Lesson state

## Dev Notes

### Architecture Compliance

**ViewModel Introduction:** [Source: docs/architecture.md#MVVM Pattern]
- This story introduces the ViewModel layer.
- `LessonViewModel` must handle all business logic.
- `LessonScreen` becomes a passive view, observing state.

**Navigation:**
- Use `NavController` to move between `Lesson` and `Summary`.
- Pass necessary data (score, etc.) if needed (Story 2.4 will add metrics, this story just navigates).

### Technical Requirements

**State Properties:**
- `currentQuestion: Int` (1..10)
- `totalQuestions: Int` (constant 10)
- `isLessonComplete: Boolean` (derived or explicit state)

**Progress Display:**
- Top of screen
- Text: "Fråga 1 av 10" (Swedish)

### File Structure Requirements

**Files to Create:**
```
app/src/main/java/com/siffermastare/
├── ui/
│   └── lesson/
│       └── LessonViewModel.kt
└── ui/
    └── summary/
        └── SummaryScreen.kt
```

**Files to Modify:**
```
app/src/main/java/com/siffermastare/
├── ui/
│   └── lesson/
│       └── LessonScreen.kt (Refactor to use ViewModel)
└── ui/
    └── navigation/
        └── Screen.kt (Add Summary route)
```
