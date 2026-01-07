# Story 6.2: Tricky Pairs Generator

Status: ready-for-dev

## Story

As a User,
I want a dedicated mode for easily confused numbers (e.g., 7/20, 13/30),
so that I can fine-tune my listening and distinguish between similar sounding numbers.

## Acceptance Criteria

1. **Given** I am in the "Tricky Pairs" module lesson
   **When** a new question is generated
   **Then** the number should come from a specific set of confused pairs (e.g., 7 vs 20, 6 vs 60, 13 vs 30, 14 vs 40, etc.).

2. **Given** the generator logic
   **Then** it should select from these primary pairs:
   - 7 (sju) vs 20 (tjugo) [or 70, sjuttio - distinct enough? often confused by beginners]
   - 6 (sex) vs 60 (sextio)
   - 13 (tretton) vs 30 (trettio)
   - 14 (fjorton) vs 40 (fyrtio)
   - 15 (femton) vs 50 (femtio)
   - 16 (sexton) vs 60 (sextio)
   - 17 (sjutton) vs 70 (sjuttio)
   - 18 (arton) vs 80 (åttio)
   - 19 (nitton) vs 90 (nittio)

3. **When** the lesson runs
   **Then** it should exclusively serve these numbers, not random integers.

4. **Given** TTS pronunciation
   **Then** the "sje-ljud" (7, 20, 70) and endings ("-on" vs "-io") must be clear (subject to TTS engine quality, but logic must be correct).

## Tasks / Subtasks

- [ ] Task 1: Create Generator Logic
  - [ ] Create `TrickyPairsGenerator` class implementing `NumberGenerator`.
  - [ ] Define the specific list/map of pairs to draw from.
  - [ ] Implement `generate()` to pick randomly from this curated list.

- [ ] Task 2: Update Lesson Selector
  - [ ] Add "Tricky Pairs" to the module list in `LessonRepository` or `LessonViewModel`.
  - [ ] Ensure it loads the new generator.

- [ ] Task 3: Verify Pairs
  - [ ] Manual test: Run the module and verify only tricky numbers appear.

## Dev Notes

- **Data Structure**: A simple `listOf(7, 20, 6, 60, 13, 30, ...)` is sufficient. Or a weighted list if we want to emphasize 13/30 vs 14/40 more.
- **TTS**: No special text processing needed (unlike Time), `CardinalGenerator` logic (Int -> String) applies, just the *selection* of numbers is restricted.
- **Architecture**:
  - `TrickyPairsGenerator` can reuse `CardinalGenerator`'s `toSwedish()` extension function if it's public/internal, or re-implement if needed (prefer reuse).

### Project Structure Notes

- `domain/generators/TrickyPairsGenerator.kt`

### References

- [Source: docs/prd/epic-details.md#Story 6.2](docs/prd/epic-details.md)
