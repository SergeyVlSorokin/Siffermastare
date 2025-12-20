# Story 3.2: Ordinal Number Generator

Status: Done

## Story

As a User,
I want to practice Swedish ordinal numbers (1st, 2nd, 3rd...),
so that I can learn to recognize spoken ordinal numbers.

## Acceptance Criteria

1.  **Ordinal Logic**: Implement `OrdinalGenerator` that produces numbers/strings representing ordinals (e.g., 1-20).
2.  **TTS Strategy**: Verify if Android TTS supports "1:a", "2:a" format for Swedish ordinals ("första", "andra"). If not, implement a simple mapping or mapping strategy.
3.  **Visual Representation**: Display the ordinal format (e.g., "1:a") or merely the digit depending on pedagogical preference. (Assuming "1:a", "2:a" is standard Swedish notation).
4.  **UI Integration**: Add a dedicated button on Homescreen for "Ordinals".

## Tasks / Subtasks

- [x] Task 1: Research/Spike TTS for Ordinals (AC: 2)
  - [x] Verify if "1:a", "2:a" etc is read correctly by TTS.
- [x] Task 2: Implement OrdinalGenerator (AC: 1)
  - [x] Create `OrdinalGenerator` class.
  - [x] Generate questions with `targetValue`="1" (for checking) but `spokenText`="1:a" (confim strategy).
  - *Refinement*: If user inputs "1", we check against "1". But TTS reads "första".
- [x] Task 3: Unit Testing (AC: 3)
  - [x] Test generator output formats.
- [x] Task 4: UI Integration (AC: 4)
  - [x] Add to `NumberGeneratorFactory`.
  - [x] Add "Ordinals" button to Home.

## Dev Agent Record

### File List
- `app/src/main/java/com/siffermastare/domain/generators/OrdinalGenerator.kt`
- `app/src/test/java/com/siffermastare/domain/generators/OrdinalGeneratorTest.kt`
- `app/src/main/java/com/siffermastare/domain/generators/NumberGeneratorFactory.kt`
- `app/src/main/java/com/siffermastare/ui/home/HomeScreen.kt`
