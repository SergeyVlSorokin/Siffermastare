# Story 7.8: Fractions Strategy

Status: Done

## Story

As a learner,
I want to practice fractions like "1/2",
So that the system grades the numerator and denominator independently, tracking cardinal mastery for the numerator and ordinal mastery for the denominator (since denominators are spoken with ordinal stems in Swedish).

## Context

The `FractionsGenerator` was implemented in Epic 6 (Story 6.4). It currently uses `ExactMatchEvaluationStrategy`, which provides no granular atom feedback. Story 7.6 established the "Generator Owns Atoms" rule: generators populate `question.atoms` and strategies consume them as ground truth.

**Fractions use two-atom decomposition with mixed atom types.** Each fraction `n/d` produces exactly two atoms: the numerator as a **cardinal** atom and the denominator as an **ordinal** atom. This reflects the spoken structure: in Swedish, the numerator is spoken as a cardinal number (*"tre"*) while the denominator uses an ordinal-derived form (*"fjärdedel"* from *fjärde*).

| Component | Example | Spoken As | Atom Rule |
|:---|:---|:---|:---|
| Numerator | `3` in `3/4` | *"tre"* (cardinal) | Cardinal atom: `"3"` |
| Denominator | `4` in `3/4` | *"fjärdedel"* (ordinal stem) | Ordinal atom: `"ord:4"` |

**Full example:** `"3/4"` → atoms `["3", "ord:4"]`

**Edge cases:**
- `1/2` → atoms `["1", "ord:2"]` (denominator *"halv"* — unique form, mapped to `ord:2`)
- `5/10` → atoms `["5", "ord:10"]` (denominator *"tiondel"* from ordinal *tionde*)
- `2/3` → atoms `["2", "ord:3"]`
- Numerator and denominator atoms never collide (different prefixes), so bag-logic is straightforward

## Acceptance Criteria

### AC1: FractionsGenerator populates question.atoms with numerator and denominator

1. **Given** A `FractionsGenerator`
2. **When** It generates a question for target `"3/4"`
3. **Then** `question.atoms` == `["3", "ord:4"]` (cardinal numerator + ordinal denominator)
4. **And** `question.evaluationStrategy` is `FractionsEvaluationStrategy` (not `ExactMatchEvaluationStrategy`)

### AC2: Correct input marks all atoms Success

1. **Given** A question with `targetValue = "3/4"` and atoms `["3", "ord:4"]`
2. **When** I evaluate input `"3/4"`
3. **Then** `isCorrect = true`
4. **And** All atoms are marked **Success** via bag-logic

### AC3: Wrong denominator provides granular feedback

1. **Given** A question with `targetValue = "1/2"` and atoms `["1", "ord:2"]`
2. **When** I evaluate input `"1/4"`
3. **Then** `isCorrect = false`
4. **And** Atom `"1"` is **Success** (correct numerator)
5. **And** Atom `"ord:2"` is **Failure** (wrong denominator)
6. **And** Extra atom `"ord:4"` is ignored (No Extra Atoms rule)

### AC4: Wrong numerator provides granular feedback

1. **Given** A question with `targetValue = "3/4"` and atoms `["3", "ord:4"]`
2. **When** I evaluate input `"5/4"`
3. **Then** `isCorrect = false`
4. **And** Atom `"3"` is **Failure** (wrong numerator)
5. **And** Atom `"ord:4"` is **Success** (correct denominator)

### AC5: Completely wrong input fails all atoms

1. **Given** A question with `targetValue = "1/2"` and atoms `["1", "ord:2"]`
2. **When** I evaluate input `"7/9"`
3. **Then** `isCorrect = false`
4. **And** Atom `"1"` is **Failure**
5. **And** Atom `"ord:2"` is **Failure**

### AC6: Invalid format handled gracefully

1. **Given** A fraction question
2. **When** I evaluate input `"abc"` or `"12"` (no slash) or `""` (empty)
3. **Then** `isCorrect = false`
4. **And** All target atoms are marked **Failure**

### AC7: Denominator 10 uses single atom

1. **Given** A question with `targetValue = "5/10"` and atoms `["5", "ord:10"]`
2. **When** I evaluate input `"5/10"`
3. **Then** `isCorrect = true`
4. **And** Atom `"5"` is **Success**
5. **And** Atom `"ord:10"` is **Success**

### AC8: Existing tests continue to pass

1. **Given** The refactored `FractionsGenerator`
2. **When** All tests in `FractionsGeneratorTest` are run
3. **Then** All existing tests pass (format, spoken text, etc.)

## Tasks / Subtasks

- [x] Create `FractionsEvaluationStrategy` (AC: 2, 3, 4, 5, 6, 7)
  - [x] Create `app/src/main/java/com/siffermastare/domain/evaluation/FractionsEvaluationStrategy.kt`
  - [x] Parse input by splitting on `/` — extract numerator and denominator strings
  - [x] Handle invalid input: no `/`, non-numeric parts, empty string → fail all target atoms
  - [x] Decompose valid input into atoms: `[numStr, "ord:" + denStr]` (cardinal numerator + ordinal denominator)
  - [x] Use `BagLogicHelper.compare(question.atoms, inputAtoms)` for grading
  - [x] `isCorrect` = `input.trim() == question.targetValue` (exact string match)
- [x] Update `FractionsGenerator` to populate atoms (AC: 1)
  - [x] Extract numerator and denominator as strings in `generateLesson()`
  - [x] Set `question.atoms = listOf(numerator.toString(), "ord:$denominator")`
  - [x] Switch `evaluationStrategy` from `ExactMatchEvaluationStrategy` to `FractionsEvaluationStrategy`
- [x] Create `FractionsEvaluationStrategyTest` (AC: 2, 3, 4, 5, 6, 7)
  - [x] Test correct input → all atoms Success
  - [x] Test wrong denominator → numerator Success, denominator Failure
  - [x] Test wrong numerator → numerator Failure, denominator Success
  - [x] Test completely wrong input → all atoms Failure
  - [x] Test invalid format (no slash, non-numeric, empty) → all atoms Failure
  - [x] Test denominator 10 (ordinal teen atom `"ord:10"`) → correct grading
  - [x] Test input with extra whitespace → trimmed and graded correctly
- [x] Update `FractionsGeneratorTest` (AC: 1, 8)
  - [x] Add test verifying atoms for known fractions (cardinal numerator + ordinal denominator)
  - [x] Add test verifying `evaluationStrategy` is `FractionsEvaluationStrategy`
  - [x] Ensure existing format and spoken text tests still pass
- [x] Run full test suite and verify no regressions (AC: 8)

## Dev Notes

### Fraction Atom Decomposition Rule
Fractions use **mixed-type two-component decomposition** reflecting Swedish spoken structure:
- **Numerator**: the integer before the `/` → **cardinal** atom (e.g., `"3"`)
- **Denominator**: the integer after the `/` → **ordinal** atom with `ord:` prefix (e.g., `"ord:4"`)

**Rationale:** Swedish fraction denominators use ordinal-derived stems (*tredjedel* from *tredje*, *fjärdedel* from *fjärde*). Using `ord:` prefixed atoms means fractions contribute to ordinal mastery tracking, increasing sample counts for ordinals while correctly modeling the linguistic skill being tested. The special case *"halv"* (1/2) is mapped to `ord:2` for consistency.

The generator constrains numerator ∈ [1, denominator-1] and denominator ∈ [2, 10]. All numerators are cardinal atoms (1–9) per §2.1. Denominators use ordinal atoms (`ord:2`–`ord:10`) per §2.1 (Ordinal Digits + Ordinal Teens).

### Input Decomposition for Evaluation
The strategy must decompose user input using these rules:
1. Trim whitespace
2. Split on `/` — must produce exactly 2 parts
3. Both parts must be valid integers
4. If any validation fails: `isCorrect = false`, fail all target atoms
5. If valid: input atoms = `[numeratorStr, "ord:" + denominatorStr]`, then bag-compare against `question.atoms`

### Reuse BagLogicHelper
Use `BagLogicHelper.compare()` and `BagLogicHelper.failAll()` from the shared utility. Do NOT duplicate bag logic. [Source: BagLogicHelper.kt](file:///c:/Users/Serge/source/repos/Siffermastare/app/src/main/java/com/siffermastare/domain/evaluation/BagLogicHelper.kt)

### Project Structure Notes

**New files:**
- `app/src/main/java/com/siffermastare/domain/evaluation/FractionsEvaluationStrategy.kt`
- `app/src/test/java/com/siffermastare/domain/evaluation/FractionsEvaluationStrategyTest.kt`

**Modified files:**
- `app/src/main/java/com/siffermastare/domain/generators/FractionsGenerator.kt` — populate atoms, switch strategy
- `app/src/test/java/com/siffermastare/domain/generators/FractionsGeneratorTest.kt` — add atom verification tests

### Code Patterns to Follow

From Stories 7.6 and 7.7:
- Use `question.atoms` as ground truth (never derive target atoms internally in strategy)
- Test helpers: use `createQuestion()` pattern with explicit atoms
- Bag-logic: see `StandardNumberEvaluationStrategy.evaluate()` and `PhoneNumberEvaluationStrategy` for reference
- `isCorrect` is based on exact string match, independent of atom grading

### References

- [Source: docs/learning-model-spec.md §4.1](file:///c:/Users/Serge/source/repos/Siffermastare/docs/learning-model-spec.md) — Atom source and grading rules
- [Source: docs/epics.md — Story 7.8](file:///c:/Users/Serge/source/repos/Siffermastare/docs/epics.md) — Epic requirements
- [Source: Story 7-6](file:///c:/Users/Serge/source/repos/Siffermastare/docs/sprint-artifacts/7-6-adapt-strategies-to-atoms-architecture.md) — Atoms architecture pattern
- [Source: Story 7-7](file:///c:/Users/Serge/source/repos/Siffermastare/docs/sprint-artifacts/7-7-phone-number-strategy.md) — Phone number strategy (similar pattern)
- [Source: FractionsGenerator.kt](file:///c:/Users/Serge/source/repos/Siffermastare/app/src/main/java/com/siffermastare/domain/generators/FractionsGenerator.kt) — Current generator
- [Source: BagLogicHelper.kt](file:///c:/Users/Serge/source/repos/Siffermastare/app/src/main/java/com/siffermastare/domain/evaluation/BagLogicHelper.kt) — Shared bag-logic utility
- [Source: StandardNumberEvaluationStrategy.kt](file:///c:/Users/Serge/source/repos/Siffermastare/app/src/main/java/com/siffermastare/domain/validation/strategies/StandardNumberEvaluationStrategy.kt) — Reference strategy pattern

## Dev Agent Record

### Agent Model Used

Claude (Antigravity)

### Completion Notes List

- ✅ Created `FractionsEvaluationStrategy` — splits on `/`, validates integers, decomposes to `[numStr, "ord:denStr"]`, uses `BagLogicHelper.compare()`. Invalid input → `failAll()`.
- ✅ Updated `FractionsGenerator` — populates `atoms = listOf(numerator, "ord:$denominator")`, switched from `ExactMatchEvaluationStrategy` to `FractionsEvaluationStrategy`.
- ✅ Created 13 tests in `FractionsEvaluationStrategyTest` covering AC2–AC7 + whitespace + edge cases.
- ✅ Added 2 tests to `FractionsGeneratorTest`: atom pattern verification + strategy type assertion.
- ✅ Full suite: 234 tests, 0 failures.

### File List

- [NEW] `app/src/main/java/com/siffermastare/domain/evaluation/FractionsEvaluationStrategy.kt`
- [MOD] `app/src/main/java/com/siffermastare/domain/generators/FractionsGenerator.kt`
- [NEW] `app/src/test/java/com/siffermastare/domain/evaluation/FractionsEvaluationStrategyTest.kt`
- [MOD] `app/src/test/java/com/siffermastare/domain/generators/FractionsGeneratorTest.kt`
- [MOD] `docs/sprint-artifacts/sprint-status.yaml`

## Senior Developer Review (AI)

- [x] Fixed High severity issue: Refactored `FractionsGeneratorTest.kt` to remove flaky non-deterministic tests by making `formatSpokenText` internal and verifying it predictably.
- [x] Fixed Medium severity issue: Removed dead code in `FractionsGenerator.kt` for impossible `numerator > 1` when `denominator == 2`.
- [x] Waived Medium severity issue (Strict Whitespace Parsing): Waived based on user input that the UI securely restricts characters.
- [x] Fixed Low severity issue: Removed left-over AI comments in test file.
- **Outcome:** Fixes applied. Status updated to Done.
