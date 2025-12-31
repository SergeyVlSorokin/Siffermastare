# Story 5.3: Visual Time Separator

Status: done

## Story

As a user practicing Time modules,
I want to see a visual separator (:) in the input field,
So that I understand the format expected and can read my input clearly.

## Acceptance Criteria

1. **Given** I am in a "Time" module lesson
   **Then** The input field should display a colon (:) separator between hours and minutes.

2. **Given** The input field
   **When** I type numbers
   **Then** They should fill in naturally around the separator (e.g., typing "0930" results in visual "09:30").

3. **Given** I am in a non-Time module
   **Then** The colon separator should NOT be visible.

## Tasks / Subtasks

- [x] Task 1: Update AnswerDisplay UI
  - [x] Add `visualTransformation` or logic to `AnswerDisplay` to support a static separator.
  - [x] Or, use a `BasicTextField` with a custom decoration box that draws the colon at the correct index.
- [x] Task 2: Handle Input Logic
  - [x] Ensure the underlying value remains a simple string of digits (or handles the colon if inserted).
  - [x] (Decision): Keep state as raw digits ("0930") and format purely visually ("09:30").

## Dev Notes

- **VisualTransformation:** Jetpack Compose `VisualTransformation` is the cleanest way to handle "Type digits, see formatted text".
- **OffsetMapping:** Will be needed if we use VisualTransformation so the cursor behaves correctly.
- **Scope:** This story is purely about the *display* and *input experience*. Determining if "930" equals "09:30" is Story 5.5.

### Project Structure Notes

- `AnswerDisplay.kt`: Primary change.

### References

- [Source: docs/epics.md#Story 5.3](docs/epics.md)

## Dev Agent Record

### Implementation Notes
- **Task 1**: Implemented `TimeFormatter` util and `AnswerDisplay` component.
- Used `lessonId.contains("time")` in `LessonScreen` to toggle visual formatting.
- `TimeFormatter` logic: Inserts `:` before last 2 digits. "123" -> "1:23", "0930" -> "09:30".
- **Task 2**: Verified `TimeGenerator` produces raw digits ("0930"), ensuring consistent data model. No logic changes needed for input handling.

### Debug Log
- N/A - Straightforward implementation.

## File List
- app/src/main/java/com/siffermastare/ui/util/TimeFormatter.kt
- app/src/main/java/com/siffermastare/ui/components/AnswerDisplay.kt
- app/src/main/java/com/siffermastare/ui/lesson/LessonScreen.kt
- app/src/main/java/com/siffermastare/ui/lesson/LessonViewModel.kt
- app/src/main/java/com/siffermastare/domain/generators/TimeGenerator.kt
- app/src/test/java/com/siffermastare/ui/util/TimeFormatterTest.kt

## Senior Developer Review (AI)
- **Review Date**: 2026-01-01
- **Outcome**: Approve (Fixed 2 High/Med issues)
- **Issues Found**: 1 High (Empty State), 1 Medium (Input Limit), 2 Low
- **Fixes Applied**:
    - Fixed `AnswerDisplay` empty state logic.
    - Added input length limit (8 chars) to `LessonViewModel`.
    - Cleaned up `TimeFormatterTest` and added `@Preview`.

## Change Log
- 2026-01-01: Applied code review fixes (Empty State, Input Limit).
- 2026-01-01: Implementation complete.

### Completion Notes
✅ Implemented Visual Separator for Time modules.
- Created `AnswerDisplay` component in `ui/components/`.
- Created `TimeFormatter` in `ui/util/` (handles "0930" -> "09:30").
- Updated `LessonScreen` to use `AnswerDisplay` with `isTimeFormat` logic.
- Updated `LessonViewModel` to expose `lessonId` in UI state.
- Verified input logic remains raw digits (consistent with `TimeGenerator`).
- All unit tests passing.
