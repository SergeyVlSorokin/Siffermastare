# Story 6.1: Informal Time Generator (Analogue)

Status: ready-for-dev

## Story

As a User,
I want to practice "analogue" time speaking (e.g., "kvart i tre"),
so that I can understand how Swedes actually tell time.

## Acceptance Criteria

1. **Given** I am in the "Informal Time" module lesson
   **When** a new question is generated
   **Then** the prompt should be a spoken "Analogue" time string (e.g., "fem i halv tre").

2. **When** I hear "fem i halv tre" (2:25)
   **Then** I must enter the digital format (e.g., "0225" or "1425" or with colon if Story 5.3 active).
   **And** the system validates it as correct if it matches the target time.

3. **Given** The `InformalTimeGenerator`
   **Then** It must produce times covering these patterns:
   - "över" (past)
   - "i" (to)
   - "halv" (half past previous hour - distinct Swedish logic)
   - "kvart över" (quarter past)
   - "kvart i" (quarter to)
   - "fem i halv" (25 past)
   - "fem över halv" (35 past)

4. **Given** TTS limitations
   **Then** The spoken string should be natural Swedish text, not relying on system auto-formatting of digits.

## Tasks / Subtasks

- [x] Task 1: Create Generator Logic
  - [x] Create `InformalTimeGenerator` class implementing `NumberGenerator` (or `TimeGenerator` interface).
  - [x] Implement `generate()` to produce a `TimeQuestion` with `answer="0225"` and `displayText` (hidden) / `spokenText="fem i halv tre"`.
  - [x] Implement the complex Swedish minute logic:
    - 0: klockan (current hour)
    - 1-20: över (current hour) ("kvart över" at 15)
    - 21-29: X i halv (next hour)
    - 30: halv (next hour)
    - 31-39: X över halv (next hour)
    - 40-59: X i (next hour) ("kvart i" at 45)

- [x] Task 2: Update Lesson Selector
  - [x] Add "Informal Time" to the module list in `LessonRepository` or `LessonViewModel`.
  - [x] Ensure it loads the new generator.

- [x] Task 3: Verify Validation
  - [x] Ensure `AnswerValidator` (from Story 5.5) handles the comparison correctly (it should, as target is digital).

## Dev Notes

- **Swedish Time Logic**: This is the trickiest part.
  - 14:20 -> "tjugo över två" (Natural Swedish anchor).
  - 14:25 -> "fem i halv tre".
  - 14:29 -> "et i halv tre".
  - 14:30 -> "halv tre".
  - 14:31 -> "et över halv tre".
  - 14:35 -> "fem över halv tre".
  - 14:36 -> "sex över halv tre" (Natural Swedish extends 'halv' reference here).
  - 14:40 -> "tjugo i tre".
- **Interface**: Reuse `TimeGenerator` pattern if possible, or extend it.
- **Testing**: Heavy unit testing needed on the text generation logic.

### Project Structure Notes

- `domain/generators/InformalTimeGenerator.kt`
- `domain/validation/AnswerValidator.kt` (Updated for multiple correct answers)
- `ui/util/TimeFormatter.kt` (Updated for pipe delimited formatting)

### References

- [Source: docs/prd/epic-details.md#Story 6.1](docs/prd/epic-details.md)
