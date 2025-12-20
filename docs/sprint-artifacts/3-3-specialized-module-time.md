# Story 3.3: Specialized Module (Time)

Status: Done

## Story

As a User,
I want to practice Swedish time telling,
so that I can understand when people say "klockan är ..." (digits or analog style).

## Acceptance Criteria

1.  **Time Logic**: Implement `TimeGenerator` that produces time strings (HH:MM).
2.  **Input Format**: User should input digits corresponding to the time (e.g., "1430").
3.  **TTS Strategy**: Ensure TTS reads the time clearly.
    -   *MVP*: Digital reading ("retio").
    -   *Nice to have*: Natural reading ("halv tre"). *We will stick to digital for MVP unless easy.*
4.  **UI Integration**: Add "Time" button to Home Screen.
5.  **Visual Feedback**: Display format "HH:MM" in the input field as user types? Or just raw digits.
    -   *Decision*: Raw digits is consistent with current UI.

## Tasks / Subtasks

- [x] Task 1: Implement TimeGenerator (AC: 1)
  - [x] Generate random HH (00-23) and MM (00-59).
  - [x] Target: "HHMM" (string of digits).
  - [x] Spoken: "HH:MM" (string with colon).
- [x] Task 2: Verify TTS for Time (AC: 3)
  - [x] formatting "14:30" usually triggers time reading.
- [x] Task 3: Unit Testing (AC: 1)
- [x] Task 4: UI Integration (AC: 4)
  - [x] Update Factory.
  - [x] Update Home Screen.

## Dev Agent Record

### File List
- `app/src/main/java/com/siffermastare/domain/generators/TimeGenerator.kt`
- `app/src/test/java/com/siffermastare/domain/generators/TimeGeneratorTest.kt`
- `app/src/main/java/com/siffermastare/domain/generators/NumberGeneratorFactory.kt`
- `app/src/main/java/com/siffermastare/ui/home/HomeScreen.kt`
