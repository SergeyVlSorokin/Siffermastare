# Story 7.12: Lesson Loop Integration

Status: done

## Story

As a user,
I want my lesson results to actually save to my profile,
so that the app learns what I know.

## Acceptance Criteria

1. **Given** I complete a question in a Lesson
2. **When** I hit Submit
3. **Then** The `LessonSessionManager` uses the `EvaluationStrategy` to grade the input
4. **And** The result is passed to the `KnowledgeEngine` to update the database asynchronously
5. **And** UI feedback ("Rätt" / "Fel") is distinct from the internal atomic updates

## Tasks / Subtasks

- [x] Task 1: Integrate `KnowledgeEngine` tracking with Question timing
  - [x] Update `LessonState` or `LessonSessionManager` to record `questionStartTime` when a question is displayed/audio finishes playing (as per architecture `LessonSession`).
  - [x] Implement `elapsedMs` calculation in `submitAnswer` by comparing submit time against `questionStartTime`.
- [x] Task 2: Connect `KnowledgeEngine` to `LessonSessionManager`
  - [x] Inject `KnowledgeEngine` into `LessonSessionManager` or the `SubmitAnswerUseCase` that drives it.
  - [x] Modify `submitAnswer()` to launch a coroutine (using a provided `CoroutineScope` from the ViewModel) to call `KnowledgeEngine.processEvaluation()` with the `EvaluationResult`, `elapsedMs`, and `targetValue.length`.
  - [x] Ensure the async DB update does not block the main UI thread.
- [x] Task 3: Unit Testing
  - [x] Update `LessonSessionManagerTest.kt` to provide a Fake/Mock `KnowledgeEngine`.
  - [x] Verify `processEvaluation` is called with correctly calculated `elapsedMs`, `targetLength`, and `EvaluationResult` upon submitting an answer.
  - [x] Ensure backward compatibility with existing tests that do not expect `KnowledgeEngine` (use test doubles).

## Dev Notes

### Architecture Compliance

- `LessonSessionManager` should remain in `domain/`.
- Updates to `KnowledgeEngine` must be asynchronous. Coroutines injected into or called by `LessonSessionManager` must use an appropriate `CoroutineScope` (e.g., `viewModelScope` passed down, or a dedicated UseCase that orchestrates updating the DB and the Session state).
- Refer to `docs/learning-model-spec.md` for exact tracking dynamics, but `KnowledgeEngine` (Story 7.11) already handles the calculation logic. Your focus is strictly integration.

### File Structure Requirements

- `app/src/main/java/com/siffermastare/domain/LessonSessionManager.kt` [MODIFY]
- `app/src/test/java/com/siffermastare/domain/LessonSessionManagerTest.kt` [MODIFY]
- UI ViewModels that invoke `submitAnswer` [MODIFY if scope injection is needed]

### Technical Requirements

- Accurate Time Tracking: Measure from audio completion to submit button press. Note: Currently the architecture doc lists `questionStartTime: Long` in `LessonSession` models, implement this logic if missing.
- Async DB calls: Database updates (`KnowledgeEngine.processEvaluation` is suspend) must be non-blocking. Use standard Kotlin coroutine patterns. Let `LessonSessionManager` return the synchronous grading result immediately for UI feedback while triggering the async DB update.

### Previous Story Intelligence

- **From Story 7.11 (Bayesian Math Engine):** `KnowledgeEngine.processEvaluation(result: EvaluationResult, elapsedMs: Long, targetLength: Int)` takes care of computing the non-stationary beta distribution weights. Verify `targetLength` cleanly maps to the string length of the expected target.
- Tests from previous strategies expect deterministic behavior; ensure mocking of `KnowledgeEngine` is solid so `submitAnswer` still returns exactly what UI expects.

### References

- [Learning Model Spec](file:///c:/Users/Serge/source/repos/Siffermastare/docs/learning-model-spec.md) - Section 3.3 for elapsed time definition.
- [Architecture](file:///c:/Users/Serge/source/repos/Siffermastare/docs/architecture.md) - MVVM and Clean Architecture compliance.
- [Bayesian Math Engine](file:///c:/Users/Serge/source/repos/Siffermastare/docs/sprint-artifacts/7-11-bayesian-math-engine.md) - Interface definitions to interact with.

## Dev Agent Record

### Context Reference

Ultimate context engine analysis completed - comprehensive developer guide created

### Agent Model Used

Antigravity (Google Deepmind)

### Debug Log References

### Completion Notes List

### File List

- `app/src/main/java/com/siffermastare/domain/LessonSessionManager.kt` [MODIFIED]
- `app/src/test/java/com/siffermastare/domain/LessonSessionManagerTest.kt` [MODIFIED]
- `app/src/main/java/com/siffermastare/SiffermastareApplication.kt` [MODIFIED]
- `app/src/main/java/com/siffermastare/ui/lesson/LessonViewModel.kt` [MODIFIED]
- `app/src/main/java/com/siffermastare/ui/lesson/LessonViewModelFactory.kt` [MODIFIED]
- `app/src/test/java/com/siffermastare/ui/lesson/LessonViewModelTest.kt` [MODIFIED]
