# Story 5.4: Safe Exit

Status: ready-for-dev

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

- [ ] Task 1: Implement Interaction Handler
  - [ ] Add `BackHandler` in `LessonScreen` composable to intercept system back press.
  - [ ] Intercept TopAppBar back navigation action.
- [ ] Task 2: Add Dialog UI
  - [ ] Create `ExitConfirmationDialog` composable (Standard Alert Dialog).
  - [ ] Text: "Vill du avsluta lektionen?" / "Dina framsteg sparas inte."
  - [ ] Buttons: "Avsluta" (Destructive/Key), "Avbryt" (Cancel).
- [ ] Task 3: Manage State
  - [ ] Add `showExitDialog` boolean to UI state (or local `remember` state if purely UI-driven).

## Dev Notes

- **BackHandler:** Compose `BackHandler(enabled = true) { showDialog = true }` is standard.
- **Navigation:** The TopAppBar back button usually calls `navController.popBackStack()`. This needs to be wrapped to show dialog first.

### Project Structure Notes

- `LessonScreen.kt`: Major changes (BackHandler, Dialog).
- `LessonViewModel.kt`: Handle the act of confirming exit (if cleanup needed).

### References

- [Source: docs/epics.md#Story 5.4](docs/epics.md)
