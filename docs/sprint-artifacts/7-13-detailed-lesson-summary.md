# Story 7.13: Detailed Lesson Summary

Status: done

## Story

As a learner,
I want to see exactly which numbers I struggled with after a lesson,
so that I know what to focus on next time.

## Acceptance Criteria

1. **Given** I finish a lesson
   **Then** the summary screen shows two lists: "Improved Knowledge" and "Needs Practice"
2. **And** the "Improved Knowledge" list contains atoms where all updates were Success during this session (e.g., "You mastered '7'")
3. **And** the "Needs Practice" list contains atoms where at least one update was Failure during this session (e.g., "You struggled with '20'")
4. **And** it derives these lists from the session's accumulated `EvaluationResult`s (not from the database)
5. **And** the existing accuracy % and average speed metrics are still displayed
6. **And** if atom update lists are empty, only accuracy and speed are shown (no empty sections)

## Tasks / Subtasks

- [x] Task 1: Accumulate session `EvaluationResult`s in `LessonSessionManager` (AC: #4)
  - [x] 1.1 Add a `sessionResults: List<EvaluationResult>` accumulator field to `LessonSessionManager`
  - [x] 1.2 Append each `EvaluationResult` in `submitAnswer()` after evaluation (both strategy and fallback paths)
  - [x] 1.3 Clear the accumulator in `startLesson()`
  - [x] 1.4 Expose a public `getSessionResults(): List<EvaluationResult>` method
- [x] Task 2: Create `AtomSummary` domain model (AC: #1, #2, #3)
  - [x] 2.1 Create `domain/models/AtomSummary.kt` with data class `AtomSummary(val atomId: String, val successes: Int, val failures: Int)`
  - [x] 2.2 Add a companion/top-level function `buildAtomSummaries(results: List<EvaluationResult>): Pair<List<AtomSummary>, List<AtomSummary>>` that returns `(improved, needsPractice)` lists
  - [x] 2.3 Logic: merge all `atomUpdates` across all results by atomId, count total successes and failures. An atom goes to "improved" if failures == 0, to "needsPractice" if failures > 0
  - [x] 2.4 Smart Sorting: sort output lists by numeric value extracting digits from `atomId` (stripping `ord:`, `#`), falling back to alphabetical for text like `kvart` or `halv`.
- [x] Task 3: Surface atom summary data to the UI layer (AC: #1, #4, #5, #6)
  - [x] 3.1 Update `LessonViewModel.calculateAndSaveResults()` to call `sessionManager.getSessionResults()` and `buildAtomSummaries()`, storing the two lists
  - [x] 3.2 Update `getFinalStats()` or add a new `getAtomSummary()` method that returns `Pair<List<AtomSummary>, List<AtomSummary>>?` (nullable for legacy fallback)
  - [x] 3.3 Update `LessonScreen.kt` callback `onLessonComplete` to pass the atom summary data alongside accuracy and speed
- [x] Task 4: Update Navigation to pass summary data (AC: #1, #5, #6)
  - [x] 4.1 Rather than encoding complex lists into nav args, use a shared in-memory approach: store atom summaries in a `SavedStateHandle` or a shared `SummaryDataHolder` singleton cleared after consumption
  - [x] 4.2 Update `Screen.Summary` route — atom data does NOT go through URL encoding; only accuracy/avgSpeed remain as nav args
  - [x] 4.3 Create `SummaryViewModel` (or use `SummaryDataHolder`) to retrieve atom data on the summary screen side
- [x] Task 5: Update `SummaryScreen.kt` UI (AC: #1, #2, #3, #5, #6)
  - [x] 5.1 Add parameter `improvedAtoms: List<AtomSummary>` and `needsPracticeAtoms: List<AtomSummary>` (both default empty)
  - [x] 5.2 Display "Improved Knowledge" section with atom IDs formatted for readability (strip `ord:` prefix → show "20 (ordinal)" etc.)
  - [x] 5.3 Display "Needs Practice" section similarly
  - [x] 5.4 Conditionally hide both sections when atom lists are empty (AC: #6)
  - [x] 5.5 Keep existing accuracy and speed display intact
- [x] Task 6: Unit Testing (AC: all)
  - [x] 6.1 Test `LessonSessionManager` accumulates results correctly across multiple submits
  - [x] 6.2 Test `LessonSessionManager.getSessionResults()` returns empty list after `startLesson()`
  - [x] 6.3 Test `buildAtomSummaries()` correctly partitions atoms into improved vs needsPractice
  - [x] 6.4 Test edge case: empty `atomUpdates` maps produce empty summary lists
  - [x] 6.5 Test edge case: atom appearing in multiple questions aggregates correctly (e.g., atom "5" succeeds in Q1, fails in Q3 → needsPractice)
  - [x] 6.6 Test `LessonViewModel` exposes correct atom summary after lesson completion

## Dev Notes

### Architecture Compliance

- `AtomSummary` and `buildAtomSummaries()` belong in `domain/models/` — pure Kotlin, no Android dependencies
- `LessonSessionManager` stays in `domain/` — accumulating `EvaluationResult`s is domain logic
- `SummaryScreen.kt` remains a stateless Composable receiving data via parameters
- Navigation data passing: avoid putting complex objects in nav args. Use a lightweight in-memory holder or `SavedStateHandle` pattern. The holder should be cleared after the `SummaryScreen` reads it to avoid stale data

### Project Structure Notes

- `app/src/main/java/com/siffermastare/domain/models/AtomSummary.kt` [NEW]
- `app/src/main/java/com/siffermastare/domain/LessonSessionManager.kt` [MODIFY] — add result accumulation
- `app/src/main/java/com/siffermastare/ui/lesson/LessonViewModel.kt` [MODIFY] — compute and surface atom summaries
- `app/src/main/java/com/siffermastare/ui/lesson/LessonScreen.kt` [MODIFY] — pass atom data through callback
- `app/src/main/java/com/siffermastare/ui/summary/SummaryScreen.kt` [MODIFY] — render atom lists
- `app/src/main/java/com/siffermastare/ui/summary/SummaryDataHolder.kt` [NEW] — shared in-memory data for navigation
- `app/src/main/java/com/siffermastare/MainActivity.kt` [MODIFY] — read from holder in summary composable
- `app/src/test/java/com/siffermastare/domain/LessonSessionManagerTest.kt` [MODIFY] — test result accumulation
- `app/src/test/java/com/siffermastare/domain/models/AtomSummaryTest.kt` [NEW] — test buildAtomSummaries

### Technical Requirements

- **No Database Reads:** The summary derives entirely from the in-session `EvaluationResult` list. Do NOT query `atom_states` — this is a session-level view, not a lifetime view (that's Story 7.14's Knowledge Dashboard)
- **Thread Safety:** `sessionResults` list is only mutated from `submitAnswer()` which runs on the caller's thread. No concurrent modifications expected, but use a synchronized list or copy-on-read if concerned
- **Atom Display Formatting:** Strip known prefixes for readability:
  - `ord:20` → display as "20 (ordinal)"
  - `#kvart` → display as "kvart" (remove `#`)
  - Plain atoms like `5` → display as "5"
- **Robustness:** If `getSessionResults()` returns an empty list or all results have empty `atomUpdates`, the summary screen gracefully falls back to showing only accuracy and speed — no empty headers

### Previous Story Intelligence

- **From Story 7.12 (Lesson Loop Integration):** `submitAnswer()` already obtains the `EvaluationResult` from the strategy. The result is passed to `processAsync()` for `KnowledgeEngine` updates. You need to ALSO accumulate this result in a session-level list before/after that call
- **From Story 7.11 (Bayesian Math Engine):** `KnowledgeEngine.processEvaluation()` updates the DB asynchronously. That flow is independent of session accumulation — do not conflate them
- **Validator path:** When `validator` is provided, a synthetic `EvaluationResult(isCorrect, emptyMap())` is created with empty `atomUpdates`. Code should handle this gracefully — empty maps simply don't contribute atoms to the summary

### Git Intelligence

- Recent commits show pattern: story implementation modifies `LessonSessionManager`, `LessonViewModel`, and corresponding tests
- Test patterns use `FakeKnowledgeEngine`, `FakeTimeProvider` — continue this pattern for new tests
- No Mockk usage detected in tests — project uses hand-written fakes

### References

- [Epics: Story 7.13](file:///c:/Users/Serge/source/repos/Siffermastare/docs/epics.md) — Lines 390-399
- [Learning Model Spec](file:///c:/Users/Serge/source/repos/Siffermastare/docs/learning-model-spec.md) — §4.1 for atom decomposition rules
- [Architecture](file:///c:/Users/Serge/source/repos/Siffermastare/docs/architecture.md) — MVVM, source tree, SummaryScreen placement
- [EvaluationResult](file:///c:/Users/Serge/source/repos/Siffermastare/app/src/main/java/com/siffermastare/domain/evaluation/EvaluationResult.kt) — `atomUpdates: Map<String, List<Boolean>>`
- [LessonSessionManager](file:///c:/Users/Serge/source/repos/Siffermastare/app/src/main/java/com/siffermastare/domain/LessonSessionManager.kt) — current submit flow
- [LessonViewModel](file:///c:/Users/Serge/source/repos/Siffermastare/app/src/main/java/com/siffermastare/ui/lesson/LessonViewModel.kt) — `calculateAndSaveResults()`, `getFinalStats()`
- [SummaryScreen](file:///c:/Users/Serge/source/repos/Siffermastare/app/src/main/java/com/siffermastare/ui/summary/SummaryScreen.kt) — current minimal UI
- [Previous Story 7.12](file:///c:/Users/Serge/source/repos/Siffermastare/docs/sprint-artifacts/7-12-lesson-loop-integration.md) — integration patterns

## Dev Agent Record

### Context Reference

Ultimate context engine analysis completed - comprehensive developer guide created

### Agent Model Used

Antigravity (Google DeepMind)

### Debug Log References

### Completion Notes List

- ✅ Task 1: Added `sessionResults` accumulator to `LessonSessionManager` — appends in all 3 submit paths, clears on `startLesson()`, exposes via `getSessionResults()`
- ✅ Task 2: Created `AtomSummary.kt` with `buildAtomSummaries()` — pure Kotlin domain function that merges atomUpdates and partitions into improved/needsPractice
- ✅ Task 3: Updated `LessonViewModel` — computes atom summaries in `calculateAndSaveResults()`, exposes via `getAtomSummary()` (null when both lists empty)
- ✅ Task 4: Created `SummaryDataHolder` singleton for in-memory navigation data passing; wired in `LessonScreen` (store) and `MainActivity` (consume)
- ✅ Task 5: Rewrote `SummaryScreen.kt` — renders "Förbättrad kunskap" and "Behöver övning" sections conditionally with atom ID formatting (ord: prefix, # prefix, plain)
- ✅ Task 6: 10 new tests total — 6 in `AtomSummaryTest`, 2 in `LessonSessionManagerTest`, 2 in `LessonViewModelTest`
- ℹ️ Pre-existing `KnowledgeRepositoryTest` failures (3) present — android.util.Log not mocked — unrelated to this story

### File List

- app/src/main/java/com/siffermastare/domain/models/AtomSummary.kt [NEW]
- app/src/main/java/com/siffermastare/ui/summary/SummaryDataHolder.kt [NEW]
- app/src/main/java/com/siffermastare/domain/LessonSessionManager.kt [MODIFIED]
- app/src/main/java/com/siffermastare/ui/lesson/LessonViewModel.kt [MODIFIED]
- app/src/main/java/com/siffermastare/ui/lesson/LessonScreen.kt [MODIFIED]
- app/src/main/java/com/siffermastare/ui/summary/SummaryScreen.kt [MODIFIED]
- app/src/main/java/com/siffermastare/MainActivity.kt [MODIFIED]
- app/src/main/res/values/strings.xml [MODIFIED]
- app/src/test/java/com/siffermastare/domain/models/AtomSummaryTest.kt [NEW]
- app/src/test/java/com/siffermastare/domain/LessonSessionManagerTest.kt [MODIFIED]
- app/src/test/java/com/siffermastare/ui/lesson/LessonViewModelTest.kt [MODIFIED]
- app/src/main/java/com/siffermastare/SiffermastareApplication.kt [MODIFIED]
- app/src/main/java/com/siffermastare/data/repository/RoomKnowledgeRepository.kt [MODIFIED]
- app/src/main/java/com/siffermastare/util/Logger.kt [NEW]

### Review Follow-ups (AI)
- [x] [HIGH] Fixed race condition where `LessonScreen` reads `getFinalStats` before `calculateAndSaveResults` completes
- [x] [MEDIUM] Extracted hardcoded UI strings (`"Give Up"`, `"Replay"`, etc.) in `LessonScreen`
- [x] [MEDIUM] Added missing tracked files and changes to File List (`SiffermastareApplication.kt`, `RoomKnowledgeRepository.kt`, `Logger.kt`)
- [x] [FEATURE] Sorted atom summary lists algebraically by atomId so they appear grouped in the UI
- [x] [FEATURE] Implemented smart sorting that recognizes underlying numbers (so `1, 2, 10` instead of `1, 10, 2`) and handles text/prefixes correctly.
