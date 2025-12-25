# Story 5.5: Flexible Time Validation

Status: ready-for-dev

## Story

As a user,
I want the app to accept "9:30" as a correct answer for "09:30",
So that I am not penalized for mathematical correctness vs strict formatting.

## Acceptance Criteria

1. **Given** The target answer is "09:30"
   **When** I submit "9:30" (or "930" if raw)
   **Then** It should be marked as **Correct**.

2. **Given** The target answer is "09:30"
   **When** I submit "09:30" (or "0930" if raw)
   **Then** It should be marked as **Correct**.

3. **Given** The target answer is "14:15"
   **When** I submit "2:15" (12-hour format mistakenly)
   **Then** It should be marked as **Incorrect** (unless we support 12h, which we don't yet - but strict checking prevents false positives).

## Tasks / Subtasks

- [ ] Task 1: Implement Time Normalization
  - [ ] In `LessonViewModel` (or `CheckAnswerUseCase`), detect if the current module is Time-based.
  - [ ] If Time, normalize both input and target:
    - `9:30` -> `09:30`
    - `930` -> `09:30` (if we stripped colons)
  - [ ] Compare normalized strings.

## Dev Notes

- **Separation of Concerns:** Story 5.3 handles the UI (VisualTransformation). This story handles the `checkAnswer` logic.
- **Edge Cases:** Ensure `9:3` is not valid for `9:30`. Input must be complete.

### Project Structure Notes

- `LessonViewModel.kt` or `AnswerValidator.kt`

### References

- [Source: docs/epics.md#Story 5.5](docs/epics.md)
