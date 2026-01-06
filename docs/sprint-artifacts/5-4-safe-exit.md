# Story 5.4: Safe Exit

Status: ready-for-review

## Story

As a user on the go (bus/train),
I want to be asked for confirmation if I accidentally try to close the lesson,
So that I don't get stuck losing my progress for the current session.

## Acceptance Criteria

1. **Given** I am in the middle of a lesson (e.g., Question 5/10)
   **When** I press the system Back button OR the app's top-left Back arrow
   **Then** A Dialog should appear asking "Avsluta lektionen?" (Quit lesson?).

2. **When** I confirm "Yes" (Avsluta)
   **Then** The lesson ends and I return to the dashboard (no stats saved).

3. **When** I select "No" (Avbryt)
   **Then** The dialog closes and I remain on the current question.

## Tasks / Subtasks

- [x] Task 1: Implement Interaction Handler
  - [x] Add `BackHandler` in `LessonScreen` composable to intercept system back press.
  - [x] Intercept TopAppBar back navigation action.
- [x] Task 2: Add Dialog UI
  - [x] Create `ExitConfirmationDialog` composable (Standard Alert Dialog).
  - [x] Text: "Vill du avsluta lektionen?" / "Dina framsteg sparas inte."
  - [x] Buttons: "Avsluta" (Destructive/Key), "Avbryt" (Cancel).
- [x] Task 3: Manage State
  - [x] Add `showExitDialog` boolean to UI state (or local `remember` state if purely UI-driven).

## Dev Notes

- **BackHandler:** Compose `BackHandler(enabled = true) { showDialog = true }` is standard.
- **Navigation:** The TopAppBar back button usually calls `navController.popBackStack()`. This needs to be wrapped to show dialog first.

### Project Structure Notes

- `LessonScreen.kt`: Major changes (BackHandler, Dialog).
- `LessonViewModel.kt`: Handle the act of confirming exit (if cleanup needed).

### References

- [Source: docs/epics.md#Story 5.4](docs/epics.md)

## Dev Agent Record

### Implementation Notes
- **State**: Added `isExitDialogVisible` to `LessonUiState` (ViewModel managed).
- **UI**: Added `ExitConfirmationDialog` composable properly separating concern.
- **Logic**: Updated `LessonScreen` signature to support `onNavigateBack`. Used `BackHandler` to intercept back press when dialog is hidden.
- **Tests**: Added ViewModel tests for dialog state logic.

### Debug Log
- Compilation failed initially as expected (Red Phase). Fixed by implementing ViewModel logic.
- UI Integration required updating `LessonScreen` signature to allow callbacks.

## File List
- app/src/main/java/com/siffermastare/ui/lesson/LessonViewModel.kt
- app/src/main/java/com/siffermastare/ui/lesson/LessonScreen.kt
- app/src/main/java/com/siffermastare/ui/components/ExitConfirmationDialog.kt
- app/src/main/java/com/siffermastare/MainActivity.kt
- app/src/test/java/com/siffermastare/ui/lesson/LessonViewModelTest.kt

### Completion Notes
✅ Implemented Safe Exit Mechanism.
- Back press intercepts correctly.
- Dialog appears/dismisses correctly.
- "Avsluta" properly pops back stack without saving stats.
- Tests passing.
