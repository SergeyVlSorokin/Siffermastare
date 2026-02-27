# Story 7.10: Decimals Strategy

Status: done

## Story

As a learner,
I want to practice decimals using the comma separator,
So that the input correctly identifies my knowledge of integer and decimal parts.

## Acceptance Criteria

### Exact Match
1. **Given** A "Decimals" Lesson (Target: "2,5", Atoms: ["2", "5"])
2. **When** I type "2,5"
3. **Then** It is marked **Correct**
4. **And** Atoms "2" and "5" are marked **Success**

### Partial Match (Wrong decimal part)
1. **Given** A "Decimals" Lesson (Target: "2,5", Atoms: ["2", "5"])
2. **When** I type "2,4"
3. **Then** It is marked **Incorrect**
4. **And** Atom "2" is **Success**, Atom "5" is **Failure**

### Partial Match (Wrong length in decimal part)
1. **Given** A "Decimals" Lesson (Target: "2,25", Atoms: ["2", "20", "5"])
2. **When** I type "2,5"
3. **Then** It is marked **Incorrect**
4. **And** Atom "2" is **Success**, Atom "5" is **Success**, Atom "20" is **Failure**

### Wrong Length
1. **Given** A "Decimals" Lesson (Target: "3,14", Atoms: ["3", "14"])
2. **When** I type "3,1"
3. **Then** It is marked **Incorrect**
4. **And** Atom "3" is **Success**, Atom "14" is **Failure**

### Multiple Commas (Invalid format)
1. **Given** A "Decimals" Lesson (Target: "2,5", Atoms: ["2", "5"])
2. **When** I type "2,,5"
3. **Then** It is marked **Incorrect**
4. **And** Atom "2" is **Success**, Atom "5" is **Failure**

### Missing Comma
1. **Given** A "Decimals" Lesson (Target: "2,5", Atoms: ["2", "5"])
2. **When** I type "25"
3. **Then** It is marked **Incorrect**
4. **And** Atoms "2" and "5" are evaluated separately and both are marked **Failure**.

## Tasks / Subtasks

- [x] Task 1: Create `DecimalsEvaluationStrategy`
  - [x] Create `DecimalsEvaluationStrategy` class implementing `EvaluationStrategy`.
  - [x] Split input around the comma, comparing the integer part and decimal part explicitly against target atoms.
  - [x] Handle error states properly: missing comma, multiple commas, or wrong lengths.
  - [x] Implement bag-logic comparison to determine the success tracking for each atom.
- [x] Task 2: Refactor `DecimalsGenerator` to "Generator Owns Atoms" pattern
  - [x] Switch generator to inject `DecimalsEvaluationStrategy` instead of `ExactMatchEvaluationStrategy`.
  - [x] Populate `question.atoms` with the string representations of the integer and decimal values.
  - [x] Ensure `Question` emits the proper constituent atoms.
- [x] Task 3: Unit tests for strategy and generator
  - [x] Create `DecimalsEvaluationStrategyTest` specifying exact matches, partial matches, wrong length, and missing/multiple commas.
  - [x] Update `DecimalsGeneratorTest` to assert that `question.atoms` are appropriately formed.

## Dev Notes

- **Input Validation:** The UI strictly limits input to digits and `,`. Handle cases where the user inputs multiple commas (e.g., `2,,5`) or omits them (`25`), mapping failures appropriately.
- **Atoms Definition:** `question.atoms` must fully decompose the number (e.g. `2,25` → `["2", "20", "5"]`) based on `StandardNumberEvaluationStrategy`'s internal decomposition utility, since decimals are pronounced as combinations of tens/units. 
- Follow the "Generator Owns Atoms" architecture pattern established iteratively in Epic 7.

### Architecture Compliance

- Use the domain model strategy patterns out-lined in Epic 7 `EvaluationStrategy` interfaces.
- Separate view from strategy logic natively.

### File Structure Requirements

- `app/src/main/java/com/siffermastare/domain/validation/strategies/DecimalsEvaluationStrategy.kt`
- `app/src/test/java/com/siffermastare/domain/validation/strategies/DecimalsEvaluationStrategyTest.kt`
- `app/src/main/java/com/siffermastare/domain/generators/DecimalsGenerator.kt`
- `app/src/test/java/com/siffermastare/domain/generators/DecimalsGeneratorTest.kt`

### Testing Requirements

- Verify structurally incorrect inputs (wrong length, multiple commas, missing commas) are handled without crashing and yield appropriate atomic failures.
- Verify structural decomposition of partial matches identifies proper partial-atomic credits.
- All pre-existing test suites must pass clean.

### Previous Story Intelligence

- **From Story 7.9 (Ordinal Number Evaluation Strategy):** 
  - Ensure decoupled tests! Do not rely arbitrarily on the functionality of `StandardNumberEvaluationStrategy` inside strategy testing.
  - Apply boundary checks inside the generator and validate expected ranges.

### Project Context

- [Epic Breakdown](file:///c:/Users/Serge/source/repos/Siffermastare/docs/epics.md)
- [Architecture Specifications](file:///c:/Users/Serge/source/repos/Siffermastare/docs/architecture.md)

## Dev Agent Record

### Agent Model Used

Antigravity

### Completion Notes List

- All tasks completed. `DecimalsEvaluationStrategy` handles `,` separated evaluation per AC.
- Full test suite passed without regression.

### File List

- [NEW] `app/src/main/java/com/siffermastare/domain/validation/strategies/DecimalsEvaluationStrategy.kt`
- [NEW] `app/src/test/java/com/siffermastare/domain/validation/strategies/DecimalsEvaluationStrategyTest.kt`
- [MODIFY] `app/src/main/java/com/siffermastare/domain/generators/DecimalsGenerator.kt`
- [MODIFY] `app/src/test/java/com/siffermastare/domain/generators/DecimalsGeneratorTest.kt`

## Senior Developer Review (AI)

- **Reviewer:** Sergei (via Antigravity)
- **Date:** 2026-02-27
- **Status:** Approved (Fixes Applied)
- **Findings:**
  - *Discrepancies:* None.
  - *Code Quality:* `DecimalsGeneratorTest.kt` had tautologous tests and missing edge case assertions for leading zeros.
  - *Functionality:* Decimal separator comma parsing logic properly handles numb-pad limited inputs.
- **Actions Taken:** 
  - Refactored `DecimalsGeneratorTest.kt` to define specific hard-coded tests instead of mirroring implementation.
  - Expanded `DecimalsGeneratorTest.kt` string parsing assertions for `0,00` edge cases and specific output formats.
  - Re-verified full test suite passing.
