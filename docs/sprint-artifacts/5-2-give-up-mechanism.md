# Story 5.2: Give Up Mechanism

Status: ready-for-dev

## Story

As a learner,
I want to be able to "give up" on a question I clearly don't know,
so that I don't get stuck in an infinite loop and can learn from seeing the correct answer.

## Acceptance Criteria

1. **Given** I am in an active lesson
   **When** I have entered an incorrect answer 3 times in a row for the same question
   **Then** A "Ge Upp" (Give Up) button should appear below the input field (Secondary Action).

2. **When** I click "Ge Upp"
   **Then** The app enters "Feedback Mode":
     - The **correct answer (only the digits)** is displayed directly in the **Main Input Field**.
     - The Input Field text/background changes color (e.g., Amber/Orange) to clearly distinguish it from User Input.
     - The **Numpad and Backspace buttons are disabled** (input locked).
     - The **Replay Sound and "Turtle" buttons remains active** (so I can listen again).
     - The Primary Action Button ("Svara") changes to **"Nästa"** (Next).
     - The question is marked as "Failed" (Score: 0) for stats.

3. **When** I click "Nästa"
   **Then** The lesson proceeds to the next question.

## Tasks / Subtasks

- [ ] Task 1: Update ViewModel State logic
  - [ ] Add `isGiveUpMode` to `LessonUiState`.
  - [ ] When `onGiveUp()` is called:
    - Set `userAnswer` to `correctAnswer`.
    - Lock input.
    - Change primary button state.
- [ ] Task 2: Update UI Components
  - [ ] Disable `Numpad` grid when in `isGiveUpMode`.
  - [ ] Update `AnswerDisplay` composable to support a "Revealed" style (color change).
  - [ ] Change `CheckButton` to "Nästa" behavior in `isGiveUpMode`.
- [ ] Task 3: Implement Data Logic
  - [ ] Ensure "Failed" questions (0 score) are recorded correctly in `LessonRepository`.

## Dev Notes

- **UX Refinement:** Reuse the main display. Do NOT show "Svaret var:".
- **Visuals:** Since Green = Correct and Red = Error (User's wrong input), let's use **Amber/Orange** or a muted **Grey** for the "Revealed" state to imply "Here is the info, but you didn't earn it".
- **Input Locking:** Crucial. The user sees the answer in the box but cannot delete it or type more.

### Project Structure Notes

- `LessonViewModel.kt`: Handle the `onGiveUp` state transition.
- `LessonScreen.kt`: map state to UI changes.
- `AnswerDisplay.kt` (if exists) or the Text component: Add the color logic.

### References

- [Source: docs/epics.md#Story 5.1](docs/epics.md)
