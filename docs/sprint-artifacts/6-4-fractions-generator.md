# Story 6.4: Fractions Generator

Status: done

## Story

As a User,
I want to practice listening to fractions,
so that I can understand recipes, measurements, and other contexts where "parts of a whole" are spoken.

## Acceptance Criteria

1. **Given** the need for new input keys
   **When** the UI is rendered
   **Then** the "Check" (Submit) button moves to a new location *above* the Numpad (between input fields and the number keys).
   **And** the slot to the right of '0' (previously Check) becomes a dynamic "Special Key".

2. **Given** I am in the "Fractions" module
   **Then** the Special Key (right of 0) should display and type "/".

3. **Given** a fraction like "1/2"
   **When** spoken
   **Then** TTS should say "en halv" (not "ett delat med två").

3. **Given** other common fractions
   **When** generated
   **Then** they should be spoken correctly:
   - 1/3 -> "en tredjedel"
   - 1/4 -> "en fjärdedel"
   - 2/3 -> "två tredjedelar"
   - 3/4 -> "tre fjärdedelar"
   - 1/5 -> "en femtedel" (generic rule starts applying here)

4. **When** answering
   **Then** I must type "1", "/", "2" for 1/2.
   **And** the feedback system checks equality.

## Tasks / Subtasks

- [x] Task 1: Create Generator Logic
  - [x] Create `FractionsGenerator` class.
  - [x] Implement robust logic for "halv", "tredjedel", "fjärdedel" vs "femtedel/sjättedel".
  - [x] Handle plural form ("två tredjedelar").

- [x] Task 2: UI Refactoring (Global Change)
  - [x] Move "Check" button out of the Numpad grid (place between Input and Numpad).
  - [x] Update `Numpad` composable to accept a `specialKeyChar: Char?` argument.
  - [x] If `specialKeyChar` is provided (e.g. '/'), render it in the bottom-right slot.
  - [x] If null, render an empty/invisible slot.

- [x] Task 3: Input Logic
  - [x] Ensure "/" character is handled in `currentInput` state.
  - [x] Validator should handle the string comparison "1/2" == "1/2".

- [x] Task 4: Code Review & Refactoring
  - [x] Refactor `FractionsGenerator` to use constants and maps.
  - [x] Clean up `LessonScreen` (unused code).
  - [x] Standardize Lesson IDs with `NumberGeneratorFactory` constants.


## Dev Notes

- **Grammar**:
  - Denominators: 2=halv, 3=tredjedel, 4=fjärdedel, 5=femtedel, 6=sjättedel, 7=sjundedel, 8=åttondel, 9=niondel, 10=tiondel... 20=tjugondel.
  - Plural: Add "ar" if numerator > 1 (e.g., "två tredjedelar").
  - *Keep it simple:* Focus on denominators 2-10 or 2-4 + 10 first.
- **UI Refactor**:
  - **Current Bottom Row**: `Backspace | 0 | Submit`
  - **New Bottom Row**: `Backspace | 0 | Special` (where Special is dynamic, e.g., `/` or `,` or empty)
  - **Submit Button**: Moves out of the grid. Suggested placement: A full-width or large button *between* the input field and the Numpad.
- **Architecture**: `FractionsGenerator` implements `NumberGenerator`.

### Project Structure Notes

- `domain/generators/FractionsGenerator.kt`

### References

- [Source: docs/prd/epic-details.md#Story 6.4](docs/prd/epic-details.md)

## File List
- `app/src/main/java/com/siffermastare/domain/generators/FractionsGenerator.kt` [NEW]
- `app/src/test/java/com/siffermastare/domain/generators/FractionsGeneratorTest.kt` [NEW]
- `app/src/main/java/com/siffermastare/ui/components/Numpad.kt` [MODIFIED]
- `app/src/main/java/com/siffermastare/ui/lesson/LessonScreen.kt` [MODIFIED]
- `app/src/main/java/com/siffermastare/ui/lesson/LessonViewModel.kt` [MODIFIED]
- `app/src/main/java/com/siffermastare/domain/generators/NumberGeneratorFactory.kt` [MODIFIED]
- `app/src/main/res/values/strings.xml` [MODIFIED]
- `app/src/main/java/com/siffermastare/ui/home/HomeScreen.kt` [MODIFIED]
