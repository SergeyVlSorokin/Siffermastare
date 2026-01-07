# Story 6.5: Decimals Generator

Status: ready-for-dev

## Story

As a User,
I want to practice listening to decimal numbers,
so that I can handle prices ("3,50") and precise measurements ("3,5") in real Swedish life.

## Acceptance Criteria

1. **Given** I am in the "Decimals" module
   **When** the UI is rendered
   **Then** the "Special Key" (right of '0') should display and type a comma ",".
   **And** the standard Numpad functionality works as usual.

2. **Given** a generated decimal number like "3,5"
   **When** spoken
   **Then** TTS should say "tre komma fem".

3. **Given** a generated "Price-like" decimal (Two decimal places, e.g., "10,50")
   **When** spoken
   **Then** TTS might say "tio och femtio" OR "tio komma femtio" (Stick to "komma" for consistency unless "Price Mode" is specifically enabled, but "och" is common for prices. Let's aim for generic decimals "komma" first as the baseline).

4. **When** answering
   **Then** I must type "3", ",", "5" for 3,5.
   **And** the validation logic must respect the comma separator.

## Tasks / Subtasks

- [ ] Task 1: Create Generator Logic
  - [ ] Create `DecimalsGenerator` class.
  - [ ] Generate numbers with 1 or 2 decimal places.
  - [ ] Convert string to Swedish spoken format (use "komma").
  - [ ] *Optional/Stretch:* specialized "Price" logic "kronor och öre" (Start simple with "komma").

- [ ] Task 2: UI integration
  - [ ] Reuse the Numpad refactor from Story 6.4.
  - [ ] set `specialKeyChar = ','` when this module is active.

- [ ] Task 3: Input Logic
  - [ ] Ensure "," is handled in state.
  - [ ] Validator compares "3,5" vs "3,5".

## Dev Notes

- **Separator**: Sweden uses comma `,` not dot `.`.
- **Pronunciation**: "Komma" is the safest standard way.
  - 3,5 => "tre komma fem"
  - 3,14 => "tre komma fjorton"
- **Architecture**: `DecimalsGenerator` implements `NumberGenerator`.
- **Dependencies**: Relies on the Numpad refactor (Check button moved, Special key available) from Story 6.4.

### Project Structure Notes

- `domain/generators/DecimalsGenerator.kt`

### References

- [Source: docs/prd/epic-details.md#Story 6.5](docs/prd/epic-details.md)
