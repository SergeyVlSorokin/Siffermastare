# Story 7.11: Bayesian Math Engine

Status: Ready for Review

## Story

As a system,
I need to continually update knowledge estimates based on the results from the Evaluation Strategy,
so that the user's proficiency model tracks their real-time performance.

## Acceptance Criteria

### AC1: Success Update with Speed Weight
1. **Given** An `EvaluationResult` with `atomUpdates = { "5" -> [true] }`
2. **And** An `elapsedMs` of 1600ms and a `targetValue` with 2 characters (MPE = 800)
3. **Then** Weight $W = \text{clamp}(800 / 800, 0.2, 1.3) = 1.0$
4. **And** The DB state for Atom `"5"` updates: $\alpha_{new} = 0.9 \cdot \alpha_{old} + 1.0$, $\beta_{new} = 0.9 \cdot \beta_{old}$

### AC2: Failure Update (Standard Weight)
1. **Given** An `EvaluationResult` with `atomUpdates = { "20" -> [false] }`
2. **And** Any timing values
3. **Then** The DB state for Atom `"20"` updates: $\alpha_{new} = 0.9 \cdot \alpha_{old}$, $\beta_{new} = 0.9 \cdot \beta_{old} + 1.0$

### AC3: Fast Answer Bonus
1. **Given** A correct atom with `elapsedMs` = 600ms and `targetValue` length = 2 (MPE = 300)
2. **Then** Weight $W = \text{clamp}(800 / 300, 0.2, 1.3) = 1.3$ (capped at bonus maximum)

### AC4: Slow Answer Penalty
1. **Given** A correct atom with `elapsedMs` = 8000ms and `targetValue` length = 2 (MPE = 4000)
2. **Then** Weight $W = \text{clamp}(800 / 4000, 0.2, 1.3) = 0.2$ (floored at minimum)

### AC5: Multiple Atoms in Single Evaluation
1. **Given** An `EvaluationResult` with `atomUpdates = { "20" -> [true], "5" -> [false] }`
2. **Then** Atom `"20"` receives a **Success** update (with computed W) and Atom `"5"` receives a **Failure** update (weight 1.0)
3. **And** Both updates use the **same** elapsed time and target length for MPE/W calculation

### AC6: Duplicate Atom Entries
1. **Given** An `EvaluationResult` with `atomUpdates = { "5" -> [true, false] }` (atom appears twice in target)
2. **Then** The engine applies **two sequential** Bayesian updates to Atom `"5"`: first a Success update, then a Failure update (both with decay applied before each)

### AC7: Default Prior for New Atoms
1. **Given** Atom `"X"` has never been seen before (no DB row)
2. **When** A Success update arrives for `"X"`
3. **Then** The repository returns the default prior ($\alpha = 1.0, \beta = 1.0$) per `RoomKnowledgeRepository`
4. **And** The update applies: $\alpha_{new} = 0.9 \cdot 1.0 + W$, $\beta_{new} = 0.9 \cdot 1.0$

### AC8: Absent Atoms Mean No Observation
1. **Given** An `EvaluationResult` where Atom `"Z"` is **not** present in `atomUpdates`
2. **Then** No α/β update occurs for Atom `"Z"` (SKIP per learning-model-spec §4.1)

## Tasks / Subtasks

- [x] Task 1: Create `KnowledgeEngine` domain class (AC: 1–8)
  - [x] Create `KnowledgeEngine.kt` in `domain/engine/`
  - [x] Constructor takes `KnowledgeRepository` and `TimeProvider`
  - [x] Implement `suspend fun processEvaluation(result: EvaluationResult, elapsedMs: Long, targetLength: Int)`
  - [x] MPE calculation: `elapsedMs / targetLength`
  - [x] Weight calculation: `clamp(800.0 / MPE, 0.2, 1.3)` (on **Float**, not Int)
  - [x] For each entry in `result.atomUpdates`, iterate the `List<Boolean>` (handles duplicate atoms)
  - [x] Success: `α_new = λ * α_old + W`, `β_new = λ * β_old`
  - [x] Failure: `α_new = λ * α_old`, `β_new = λ * β_old + 1.0`
  - [x] `λ` constant = `0.9f` (expose as companion-object constant for testability)
  - [x] `REFERENCE_MPE` constant = `800f`
  - [x] `MIN_WEIGHT` constant = `0.2f`, `MAX_WEIGHT` constant = `1.3f`
  - [x] Use `repository.getAtomState(atomId)` → apply update → `repository.updateAtomState(newState)`
- [x] Task 2: Comprehensive unit tests for `KnowledgeEngine` (AC: 1–8)
  - [x] Create `KnowledgeEngineTest.kt` in `test/domain/engine/`
  - [x] Reuse `FakeAtomStateDao` and `FakeTimeProvider` from `KnowledgeRepositoryTest.kt` (or extract to shared test-doubles package)
  - [x] Test success update with W=1.0 (AC1)
  - [x] Test failure update with standard weight (AC2)
  - [x] Test fast answer bonus W=1.3 (AC3)
  - [x] Test slow answer penalty W=0.2 (AC4)
  - [x] Test mixed success/failure atoms in single evaluation (AC5)
  - [x] Test duplicate atom entries `[true, false]` sequential update (AC6)
  - [x] Test default prior for new atom (AC7)
  - [x] Test absent atom receives no update (AC8)
  - [x] Test edge: `targetLength = 0` → guard against division by zero (treat MPE as very large → W = 0.2)
  - [x] Test edge: `elapsedMs = 0` → W should be 1.3 (fastest possible → bonus)
- [x] Task 3: Verify all existing tests still pass cleanly

## Dev Notes

### Architecture Compliance

- `KnowledgeEngine` lives in `domain/engine/` — **pure Kotlin**, no Android imports
- It depends on the `KnowledgeRepository` **interface** (not `RoomKnowledgeRepository`) to respect Clean Architecture layer boundaries: Domain → Interface ← Data
- All methods are `suspend` (async DB I/O via repository)
- This class does **not** own or manage coroutine scopes — the caller (ViewModel or UseCase in Story 7.12) will provide scope

### Key Constants & Formulas (from [learning-model-spec.md §3](file:///c:/Users/Serge/source/repos/Siffermastare/docs/learning-model-spec.md))

| Constant | Value | Purpose |
|---|---|---|
| λ (DECAY_FACTOR) | 0.9f | Forgetting factor — effective memory ≈ last 10 interactions |
| REFERENCE_MPE | 800f ms | Baseline "standard speed" per character |
| MIN_WEIGHT | 0.2f | Floor for slow answers — minimal α progress |
| MAX_WEIGHT | 1.3f | Cap for fast answers — bonus fluency credit |

**MPE** = `elapsedMs.toFloat() / targetLength.toFloat()`
**W** = `(REFERENCE_MPE / MPE).coerceIn(MIN_WEIGHT, MAX_WEIGHT)` (only applies to **Success**)
**Failure** always uses weight `1.0f` (speed does not mitigate a mistake)

### Existing Infrastructure (DO NOT recreate)

| Component | Path | Purpose |
|---|---|---|
| `KnowledgeRepository` | `data/repository/KnowledgeRepository.kt` | Interface: `getAtomState(atomId)`, `updateAtomState(state)` |
| `RoomKnowledgeRepository` | `data/repository/RoomKnowledgeRepository.kt` | Impl: returns default prior (α=1, β=1) for missing atoms |
| `AtomState` | `data/database/AtomState.kt` | Entity: `atomId`, `alpha`, `beta`, `lastUpdated` |
| `AtomStateDao` | `data/database/AtomStateDao.kt` | DAO: `getAtomState`, `insertOrUpdate` |
| `EvaluationResult` | `domain/evaluation/EvaluationResult.kt` | Input: `isCorrect`, `atomUpdates: Map<String, List<Boolean>>` |
| `TimeProvider` | `util/TimeProvider.kt` | Interface + `SystemTimeProvider` for timestamps |
| `FakeAtomStateDao` | `test/.../KnowledgeRepositoryTest.kt` | Test double for `AtomStateDao` |
| `FakeTimeProvider` | `test/.../KnowledgeRepositoryTest.kt` | Test double for `TimeProvider` |

### File Structure Requirements

- `app/src/main/java/com/siffermastare/domain/engine/KnowledgeEngine.kt` [NEW]
- `app/src/test/java/com/siffermastare/domain/engine/KnowledgeEngineTest.kt` [NEW]

### Testing Requirements

- All tests must be pure JUnit 4 (no Android dependencies)
- Use `runBlocking` for suspend function testing (existing pattern from `KnowledgeRepositoryTest`)
- Create `RoomKnowledgeRepository` with `FakeAtomStateDao` + `FakeTimeProvider` as the injected `KnowledgeRepository`
- Float comparisons with `assertEquals(expected, actual, delta)` using `delta = 0.001f`
- Each AC maps to at least one test method
- Guard against division-by-zero when `targetLength = 0`

### Previous Story Intelligence

- **From Story 7.10 (Decimals Strategy):** Tests must be deterministic with hard-coded values; avoid mirroring implementation logic in test assertions. Do not rely on other strategies functioning correctly.
- **From Story 7.1 (Knowledge Data Model):** `RoomKnowledgeRepository` already handles the default flat prior (α=1.0, β=1.0). Do NOT duplicate this logic in `KnowledgeEngine`.

### Project Context

- [Learning Model Spec](file:///c:/Users/Serge/source/repos/Siffermastare/docs/learning-model-spec.md) — Sections 3.1–3.3 define all update formulas
- [Epic Breakdown](file:///c:/Users/Serge/source/repos/Siffermastare/docs/epics.md) — Story 7.11 definition
- [Architecture](file:///c:/Users/Serge/source/repos/Siffermastare/docs/architecture.md) — MVVM + Clean Architecture boundaries
- [Project Context](file:///c:/Users/Serge/source/repos/Siffermastare/docs/project_context.md) — Testing rules, running tests

## Dev Agent Record

### Agent Model Used

Antigravity (Google Deepmind)

### Completion Notes List

- Implemented `KnowledgeEngine` in `domain/engine/` as pure Kotlin class with no Android dependencies
- Constructor takes `KnowledgeRepository` interface + `TimeProvider` (Clean Architecture compliant)
- All 4 constants exposed as companion-object constants: `DECAY_FACTOR`, `REFERENCE_MPE`, `MIN_WEIGHT`, `MAX_WEIGHT`
- Division-by-zero guard: `targetLength <= 0` → `MPE = Float.MAX_VALUE` → `W = MIN_WEIGHT (0.2)`
- `elapsedMs = 0` → `MPE = 0` → `REFERENCE_MPE / 0 = Inf` → clamped to `MAX_WEIGHT (1.3)`
- Failure weight always 1.0 — speed does not mitigate mistakes
- Sequential duplicate atom updates via `List<Boolean>` iteration with re-read from repository between updates
- 10 unit tests: 8 AC-mapped + 2 edge cases, all deterministic with hard-coded values
- Full test suite passes (0 regressions)

### File List

- `app/src/main/java/com/siffermastare/domain/engine/KnowledgeEngine.kt` [MODIFIED]
- `app/src/test/java/com/siffermastare/domain/engine/KnowledgeEngineTest.kt` [MODIFIED]
- `app/src/test/java/com/siffermastare/data/repository/KnowledgeRepositoryTest.kt` [MODIFIED]
- `app/src/test/java/com/siffermastare/testdoubles/FakeAtomStateDao.kt` [NEW]
- `app/src/test/java/com/siffermastare/testdoubles/FakeTimeProvider.kt` [NEW]
- `app/src/test/java/com/siffermastare/testdoubles/FakeKnowledgeRepository.kt` [NEW]
