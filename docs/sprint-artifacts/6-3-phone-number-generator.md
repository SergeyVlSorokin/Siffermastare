# Story 6.3: Phone Number Generator

Status: ready-for-dev

## Story

As a User,
I want to practice listening to long sequences of digits (Swedish phone numbers),
so that I can improve my ability to write down phone numbers in real life.

## Acceptance Criteria

1. **Given** I am in the "Phone Numbers" module lesson
   **When** a new question is generated
   **Then** the prompt should be a valid Swedish mobile number format (07x-xxx xx xx).

2. **Given** the visual input field limitations
   **When** a phone number question is active
   **Then** the input field must adapt to allow longer input (10 digits).
   **And** the UI should ideally visually group or allow continuous typing while displaying grouping.

3. **Given** TTS requirements
   **When** speaking the phone number
   **Then** it should speak in natural rhythmic groups: "Noll sju tre... etthundratjugotre... fyrtiofem... sextiosju".
   **Or** simply digit groups "073 - 123 45 67" spoken as "Noll sju tre... ett två tre... fyra fem... sex sju".
   *Dev Note: Standard Swedish practice varies. Let's aim for group-based speaking if possible, or simple digit-by-digit with pauses if grouping logic is too complex for TTS.*
   *Refinement: "Noll sju X" (prefix) ... "XXX" (group 1) ... "XX" (group 2) ... "XX" (group 3) is the standard.*

4. **When** answering
   **Then** the user types 10 digits.
   **And** validation ignores whitespace/formatting if the user types just digits.

## Tasks / Subtasks

- [ ] Task 1: Create Generator Logic
  - [ ] Create `PhoneNumberGenerator` class.
  - [ ] Implement `generate()` to produce:
    - Target: "0701234567" (raw digits).
    - Spoken Text: "070, 123, 45, 67" (with pauses/commas to hint TTS).
    - Visual Hint (Optional): "07x-xxx xx xx".

- [ ] Task 2: Update UI for Long Input
  - [ ] Update `LessonScreen` to handle `maxLength` dynamically.
  - [ ] If current limit is 4, increase to 10 for this module.
  - [ ] Ensure Numpad interactions work smoothly with 10 digits.

- [ ] Task 3: Validation
  - [ ] Ensure `AnswerValidator` checks the raw digit sequence.

## Dev Notes

- **Input Handling**: The current `LessonState` might have a hardcoded max length or UI constraints. Check `Numpad` and `LessonViewModel`.
- **TTS Rhythm**: Comma `,` usually adds a short pause in Android TTS. Use this to separate groups: "070, 123, 45, 67".
- **Phone Formats**:
  - Mobiles: 07x-xxx xx xx (10 digits).
  - Landlines (Stockholm): 08-xxx xx xx (9 digits).
  - *Keep it simple: Focus on mobile (10 digits) for MVP.*

### Project Structure Notes

- `domain/generators/PhoneNumberGenerator.kt`

### References

- [Source: docs/prd/epic-details.md#Story 6.3](docs/prd/epic-details.md)
