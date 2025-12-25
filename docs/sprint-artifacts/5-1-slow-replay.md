# Story 5.1: Slow Replay

Status: done

## Story

As a learner,
I want to hear the number spoken slowly,
So that I can distinguish rapid sounds like "tjugo" vs "tio" when I am confused.

## Acceptance Criteria

1. **Given** I am in an active lesson
   **When** I look at the screen
   **Then** I should see a "Turtle" icon button near the replay button (Secondary Action).

2. **When** I tap the Turtle button
   **Then** The current number should be spoken again via TTS.
   **And** The speech rate should be set to **0.7x** (or an optimal slow speed) for *this single utterance*.

3. **When** The next number is generated (next question)
   **Then** The speech rate should automatically return to **1.0x** (Normal).

## Tasks / Subtasks

- [x] Task 1: Update TTS Manager
  - [x] Extend `TtsManager` interface to support variable speech rate (e.g., `speak(text: String, rate: Float = 1.0f)`).
  - [x] Implement rate control in `AndroidTextToSpeech` (using `textToSpeech.setSpeechRate(rate)`).
- [x] Task 2: Update ViewModel Logic
  - [x] Add `onSlowReplay()` method to `LessonViewModel`.
  - [x] Ensure it triggers the TTS with the slow rate (0.7f).
- [x] Task 3: Update UI
  - [x] Add a `SlowReplayButton` (Icon: Turtle/Snail) to `LessonScreen`.
  - [x] Position it logically near the main Replay button (e.g., smaller, to the side).
  - [x] Connect click event to `viewModel.onSlowReplay()`.

## Dev Notes

- **TTS API:** `TextToSpeech.setSpeechRate(float)` is the standard Android API. 1.0 is normal. 0.5 might be too slow, 0.7-0.75 is usually the "learning sweet spot".
- **State Management:** This is a transient action (Fire-and-forget). The ViewModel doesn't necessarily need a persistent "Slow Mode" state, just an action that calls the TTS service with specific params.
- **Icons:** Material Icons likely has `Speed` or `Slow_Motion_Video`. A generic "Turtle" icon might need a vector asset or use `Speed` with a downward arrow.
- **Accessibility:** Ensure the button has a content description "Play slowly".

### Project Structure Notes

- `TtsManager.kt`: Core logic change here.
- `LessonViewModel.kt`: Action handler.
- `LessonScreen.kt`: UI addition.

### References

- [Source: docs/epics.md#Story 5.1](docs/epics.md)

### Dev Agent Record

#### Implementation Notes
- **TTS Manager:** Updated `speak` method to accept `rate` parameter (default 1.0f).
- **ViewModel:** Added `onSlowReplay` which sets `ttsRate` to 0.7f and triggers replay. Ensures rate resets to 1.0f on next question or normal answer checks.
- **UI:** Added a "Slow" replay button next to the main replay button. Using `PlayArrow` icon with "Sakta" label as a placeholder for a dedicated Turtle icon.
- **Testing:** Added `LessonViewModelTest.slowReplay_setsSlowRate_andTriggersReplay` and `nextQuestion_resetsRate_toNormal` to verify logic.

#### File List
- `app/src/main/java/com/siffermastare/data/tts/TTSManager.kt` (Modified)
- `app/src/main/java/com/siffermastare/ui/lesson/LessonViewModel.kt` (Modified)
- `app/src/main/java/com/siffermastare/ui/lesson/LessonScreen.kt` (Modified)
- `app/src/test/java/com/siffermastare/ui/lesson/LessonViewModelTest.kt` (Modified)

#### Change Log
- **2025-01-27:** Addressed Code Review findings: Fixed race condition in `TTSManager` where pending text replay lost the requested speech rate.
