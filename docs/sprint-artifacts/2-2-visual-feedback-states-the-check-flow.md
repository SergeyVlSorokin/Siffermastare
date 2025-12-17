# Story 2.2: Visual Feedback States (The "Check" Flow)

Status: done

## Story

As a User,
I want clear visual cues when I answer,
so that I don't have to read toast messages.

## Acceptance Criteria

1.  Implement specific Input Field colors: Neutral (typing), Green (Correct), Red (Error).
2.  On Error: Input turns red, shakes, plays sound again, clears input, user stays on question.
3.  On Correct: Input turns green, 500ms delay, then next question.

## Tasks / Subtasks

- [ ] Task 1: Add UI State for Feedback (AC: 1)
  - [ ] Add `inputColor` state (Neutral, Green, Red) to ViewModel/Screen
  - [ ] Update `LessonScreen` to respect this color
  - [ ] Remove old "Toast" logic for Correct/Incorrect

- [ ] Task 2: Implement Error Flow (Shake, Clear, Replay) (AC: 2)
  - [ ] Create simple shake animation
  - [ ] Trigger shake on Incorrect answer
  - [ ] Clear input field
  - [ ] Trigger TTS replay for current number

- [ ] Task 3: Implement Success Delay (AC: 3)
  - [ ] On Correct answer: Set state to Green
  - [ ] Wait 500ms (using `delay`)
  - [ ] Proceed to next question (reset state, new number)

## Dev Notes

### Architecture Compliance
- Logic for state changes (Neutral -> Green/Red) should reside in `LessonViewModel`.
- `LessonScreen` observes the state and triggers animations.

### Technical Requirements
- **Colors:**
  - Neutral: `MaterialTheme.colorScheme.onSurface` (or primary)
  - Green: `Color(0xFF4CAF50)` (or Swedish logic if applicable, but standard Success green)
  - Red: `Color(0xFFF44336)` (Error)
- **Animation:** simple offset animation for Shake.

### File Structure Requirements
- Modify `LessonScreen.kt`
- Modify `LessonViewModel.kt`
