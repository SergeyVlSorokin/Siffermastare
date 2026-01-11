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
   **Then** it should speak in a "Hybrid" format for clarity and naturalness:
    - **Prefix (07x):** Digits ("noll sju noll")
    - **Group 1 (xxx):** Digits ("ett två tre")
    - **Group 2 (xx):** Whole number ("fyrtiofem")
    - **Group 3 (xx):** Whole number ("sextiosju")
    - *Example Spoken Text:* "0 7 0, 1 2 3, 45, 67"

4. **When** answering
   **Then** the user types 10 digits.
   **And** validation ignores whitespace/formatting if the user types just digits.

## Tasks / Subtasks

- [x] Task 1: Create Generator Logic
  - [x] Create `PhoneNumberGenerator` class.
  - [x] Implement `generate()` to produce:
    - Target: "0701234567" (raw digits).
    - Spoken Text: "070, 123, 45, 67" (with pauses/commas to hint TTS).
    - Visual Hint (Optional): "07x-xxx xx xx".

- [x] Task 2: Update UI for Long Input
  - [x] Update `LessonScreen` to handle `maxLength` dynamically.
  - [x] If current limit is 4, increase to 10 for this module.
  - [x] Ensure Numpad interactions work smoothly with 10 digits.

- [x] Task 3: Validation
  - [x] Ensure `AnswerValidator` checks the raw digit sequence.

## Dev Notes

- **Input Handling**: The current `LessonState` might have a hardcoded max length or UI constraints. Check `Numpad` and `LessonViewModel`.
- **TTS Rhythm**: Comma `,` usually adds a short pause in Android TTS. Use this to separate groups: "070, 123, 45, 67".
- **Phone Formats**:
  - Mobiles: 07x-xxx xx xx (10 digits).
  - Landlines (Stockholm): 08-xxx xx xx (9 digits).
  - *Keep it simple: Focus on mobile (10 digits) for MVP.*

### Project Structure Notes
- `domain/generators/PhoneNumberGenerator.kt` [NEW]
- `domain/generators/NumberGeneratorFactory.kt` [MODIFIED]
- `domain/validation/AnswerValidator.kt` [MODIFIED]
- `ui/lesson/LessonViewModel.kt` [MODIFIED]
- `ui/home/HomeScreen.kt` [MODIFIED]
- `res/values/strings.xml` [MODIFIED]

### References

- [Source: docs/prd/epic-details.md#Story 6.3](docs/prd/epic-details.md)
