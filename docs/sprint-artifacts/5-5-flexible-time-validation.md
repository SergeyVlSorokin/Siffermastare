# Story 5.5: Flexible Time Validation

Status: Done

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

- [x] Task 1: Implement Time Normalization
  - [x] In `LessonViewModel` (or `CheckAnswerUseCase`), detect if the current module is Time-based.
  - [x] If Time, normalize both input and target:
    - `9:30` -> `09:30`
    - `930` -> `09:30` (if we stripped colons)
  - [x] Compare normalized strings.

## Dev Notes

- **Separation of Concerns:** Story 5.3 handles the UI (VisualTransformation). This story handles the `checkAnswer` logic.
- **Edge Cases:** Ensure `9:3` is not valid for `9:30`. Input must be complete.

### Project Structure Notes

- `LessonViewModel.kt` or `AnswerValidator.kt`

### References

- [Source: docs/epics.md#Story 5.5](docs/epics.md)

## File List

- `app/src/main/java/com/siffermastare/domain/validation/AnswerValidator.kt`
- `app/src/main/java/com/siffermastare/domain/LessonSessionManager.kt`
- `app/src/main/java/com/siffermastare/ui/lesson/LessonViewModel.kt`
- `app/src/test/java/com/siffermastare/domain/validation/AnswerValidatorTest.kt`
- `app/src/test/java/com/siffermastare/domain/LessonSessionManagerTest.kt`
- `app/src/test/java/com/siffermastare/ui/lesson/LessonViewModelTest.kt`

## Dev Agent Record

### Change Log

- **[NEW] AnswerValidator.kt**: Implemented flexible time validation logic (normalization).
- **[MODIFY] LessonSessionManager.kt**: Added optional validator support to `submitAnswer`.
- **[MODIFY] LessonViewModel.kt**: Integrated `AnswerValidator` for `time_digital` lessons.
- **[NEW] AnswerValidatorTest.kt**: Added unit tests for normalization logic.
- **[MODIFY] LessonSessionManagerTest.kt**: Added test for custom validator injection.
- **[MODIFY] LessonViewModelTest.kt**: Added integration test for flexible validation.
