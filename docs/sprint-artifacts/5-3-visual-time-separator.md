# Story 5.3: Visual Time Separator

Status: ready-for-dev

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

- [ ] Task 1: Update AnswerDisplay UI
  - [ ] Add `visualTransformation` or logic to `AnswerDisplay` to support a static separator.
  - [ ] Or, use a `BasicTextField` with a custom decoration box that draws the colon at the correct index.
- [ ] Task 2: Handle Input Logic
  - [ ] Ensure the underlying value remains a simple string of digits (or handles the colon if inserted).
  - [ ] (Decision): Keep state as raw digits ("0930") and format purely visually ("09:30").

## Dev Notes

- **VisualTransformation:** Jetpack Compose `VisualTransformation` is the cleanest way to handle "Type digits, see formatted text".
- **OffsetMapping:** Will be needed if we use VisualTransformation so the cursor behaves correctly.
- **Scope:** This story is purely about the *display* and *input experience*. Determining if "930" equals "09:30" is Story 5.5.

### Project Structure Notes

- `AnswerDisplay.kt`: Primary change.

### References

- [Source: docs/epics.md#Story 5.3](docs/epics.md)
