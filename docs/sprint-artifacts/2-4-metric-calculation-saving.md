# Story 2.4: Metric Calculation & Saving

Status: done

## Story

As a User,
I want my speed and accuracy saved when I finish,
so that I can track my progress.

## Acceptance Criteria

1.  Timer logic: Start timer when audio finishes speaking. Stop when "Check" is pressed.
2.  Calculate `Accuracy` (Correct / (Correct + Incorrect attempts)).
3.  Calculate `AvgSpeed` (Total time / 10).
4.  On Lesson Complete, save `LessonResult` to DB.
5.  Display these stats on `SummaryScreen`.

## Tasks / Subtasks

- [ ] Task 1: Implement Timer Logic (AC: 1, 3)
  - [ ] Add timing tracking in `LessonViewModel`
  - [ ] Capture start time (when TTS finishes? or roughly when question shown)
  - [ ] Capture end time (when Check pressed and correct)
  - [ ] Accumulate total time

- [ ] Task 2: Implement Accuracy Logic (AC: 2)
  - [ ] Track total attempts vs correct answers
  - [ ] Calculate percentage

- [ ] Task 3: Save to Database (AC: 4)
  - [ ] Inject `LessonRepository` into `LessonViewModel`
  - [ ] Call `repository.saveResult()` on lesson completion

- [ ] Task 4: Display Stats on Summary (AC: 5)
  - [ ] Pass stats to `SummaryScreen` (via Navigation arguments or shared ViewModel)
  - [ ] Display Accuracy and Avg Speed nicely

## Dev Notes

### Architecture Compliance
- All calculation logic in `LessonViewModel`.
- `LessonScreen` remains dumb.

### Technical Requirements
- **Timing:** `System.currentTimeMillis()` or `System.nanoTime()` is sufficient.
- **Accuracy:** `attempts` count might be > 10 if user makes mistakes. `questions` = 10. Accuracy = `10 / attempts`? Or `correct / (correct + errors)`. PRD says `Correct / (Correct + Incorrect attempts)`. If I fail once on Q1, then get it right, that's 1 correct, 1 incorrect. Total attempts = 2. Accuracy = 1/2 = 50%.

### File Structure Requirements
- Modify `LessonViewModel.kt`
- Modify `SummaryScreen.kt`
