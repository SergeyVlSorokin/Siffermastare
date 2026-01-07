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

- [ ] Task 1: Create Generator Logic
  - [ ] Create `InformalTimeGenerator` class implementing `NumberGenerator` (or `TimeGenerator` interface).
  - [ ] Implement `generate()` to produce a `TimeQuestion` with `answer="0225"` and `displayText` (hidden) / `spokenText="fem i halv tre"`.
  - [ ] Implement the complex Swedish minute logic:
    - 0: klockan (current hour)
    - 1-14: över (current hour)
    - 15: kvart över (current hour)
    - 16-19: över (current hour)
    - 20-29: X i halv (next hour), X - time to half hour
    - 30: halv (next hour)
    - 31-35: X över halv (next hour), X - time after half hour
    - 36-44: X i (next hour), X - time to next hour
    - 45: kvart i (next hour)
    - 46-59: X i (next hour), X - time to next hour

- [ ] Task 2: Update Lesson Selector
  - [ ] Add "Informal Time" to the module list in `LessonRepository` or `LessonViewModel`.
  - [ ] Ensure it loads the new generator.

- [ ] Task 3: Verify Validation
  - [ ] Ensure `AnswerValidator` (from Story 5.5) handles the comparison correctly (it should, as target is digital).

## Dev Notes

- **Swedish Time Logic**: This is the trickiest part.
  - 14:25 -> "fem i halv tre" (Literally: 5 minutes to half three).
  - 14:26 -> "fyra i halv tre" (Literally: 4 minutes to half three).
  - 14:27 -> "tre i halv tre" (Literally: 3 minutes to half three).
  - 14:28 -> "två i halv tre" (Literally: 2 minutes to half three).
  - 14:29 -> "et i halv tre" (Literally: 1 minute to half three).
  - 14:30 -> "halv tre".
  - 14:31 -> "et över halv tre" (Literally: 1 minute past half three).
  - 14:32 -> "två över halv tre" (Literally: 2 minutes past half three).
  - 14:33 -> "tre över halv tre" (Literally: 3 minutes past half three).
  - 14:34 -> "fyra över halv tre" (Literally: 4 minutes past half three).
  - 14:35 -> "fem över halv tre".
- **Interface**: Reuse `TimeGenerator` pattern if possible, or extend it.
- **Testing**: Heavy unit testing needed on the text generation logic.

### Project Structure Notes

- `domain/generators/InformalTimeGenerator.kt`
- `domain/validation/` (Reuse existing)

### References

- [Source: docs/prd/epic-details.md#Story 6.1](docs/prd/epic-details.md)
