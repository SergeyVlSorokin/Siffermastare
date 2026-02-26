# Story 7.7: Phone Number Strategy

Status: Ready for Review

## Story

As a learner,
I want to practice digit sequences where "070" means "0, 7, 0" and not "Seventy",
So that my practice reflects how phone numbers are actually spoken and my per-atom mastery is tracked correctly.

## Context

The `PhoneNumberGenerator` was implemented in Epic 6 (Story 6.3). It currently uses `ExactMatchEvaluationStrategy`, which provides no granular atom feedback. Story 7.6 established the "Generator Owns Atoms" rule: generators populate `question.atoms` and strategies consume them as ground truth.

**Critical: Phone numbers use HYBRID atom decomposition.** The spoken structure determines the atoms:

| Segment | Example | Spoken As | Atom Rule |
|:---|:---|:---|:---|
| Prefix (3 digits) | `070` | "noll sju noll" | Individual digit atoms: `["0", "7", "0"]` |
| Group 1 (3 digits) | `123` | "ett två tre" | Individual digit atoms: `["1", "2", "3"]` |
| Pair 1 (2 digits) | `45` | "fyrtiofem" | Standard number decomposition: `["40", "5"]` |
| Pair 2 (2 digits) | `67` | "sextiosju" | Standard number decomposition: `["60", "7"]` |

**Full example:** `"0701234567"` → atoms `["0", "7", "0", "1", "2", "3", "40", "5", "60", "7"]`

**Pair edge cases (from `formatPair()`):**
- `05` → spoken "noll fem" → atoms `["0", "5"]` (individual digits since < 10)
- `00` → spoken "noll noll" → atoms `["0", "0"]`
- `12` → spoken "tolv" → atoms `["12"]` (teen — single atom)
- `10` → spoken "tio" → atoms `["10"]` (teen — single atom)
- `20` → spoken "tjugo" → atoms `["20"]` (tens — single atom)

## Acceptance Criteria

### AC1: PhoneNumberGenerator populates question.atoms with hybrid decomposition

1. **Given** A `PhoneNumberGenerator`
2. **When** It generates a question for target `"0701234567"`
3. **Then** `question.atoms` == `["0", "7", "0", "1", "2", "3", "40", "5", "60", "7"]`
4. **And** Prefix + Group1 use individual digit atoms; Pair1 + Pair2 use standard number decomposition
5. **And** `question.evaluationStrategy` is `PhoneNumberEvaluationStrategy` (not `ExactMatchEvaluationStrategy`)

### AC2: Correct input marks all atoms Success

1. **Given** A question with `targetValue = "0701234567"` and atoms as per AC1
2. **When** I evaluate input `"0701234567"`
3. **Then** `isCorrect = true`
4. **And** All atom occurrences are marked **Success** via bag-logic

### AC3: Partial digit errors provide granular feedback

1. **Given** A question with `targetValue = "0701234567"` (atoms include two `"7"` entries — one from prefix, one from pair2 decomposition of 67)
2. **When** I evaluate input `"0701234569"` (last digit 9 instead of 7)
3. **Then** `isCorrect = false`
4. **And** Bag-logic: target has `"7"` ×2, input has `"7"` ×1 → 1 Success, 1 Failure for atom `"7"`
5. **And** Atom `"9"`: not in target → ignored (No Extra Atoms rule)

### AC4: Pair with leading zero uses individual digit atoms

1. **Given** A question with pair `05` (spoken "noll fem")
2. **Then** The pair contributes atoms `["0", "5"]` (individual digits, NOT standard decomposition of 5)
3. **And** Evaluation grades these digit atoms correctly

### AC5: Pair with teen uses standard decomposition

1. **Given** A question with pair `12` (spoken "tolv")
2. **Then** The pair contributes atom `["12"]` (single teen atom)
3. **And** Evaluation grades the teen atom correctly

### AC6: Pair with double zero uses individual digit atoms

1. **Given** A question with target `"0701230067"` where pair1 is `00` (spoken "noll noll")
2. **Then** The pair contributes atoms `["0", "0"]` (two individual zero atoms)
3. **And** These zeros are bag-counted together with any other `"0"` atoms from prefix/group1
4. **And** Evaluation grades the zero atoms correctly

### AC7: Non-numeric and length-mismatch inputs handled

1. **Given** A phone number question
2. **When** I evaluate input `"abc"` or `"070123456"` (too short)
3. **Then** `isCorrect = false`
4. **And** All target atoms receive **Failure** (for non-numeric) or partial bag-logic grades (for length mismatch)

### AC8: Existing tests continue to pass

1. **Given** The refactored `PhoneNumberGenerator`
2. **When** All tests in `PhoneNumberGeneratorTest` are run
3. **Then** All existing tests pass (format, leading zeros, etc.)

## Tasks / Subtasks

- [x] Create `PhoneNumberEvaluationStrategy` (AC: 2, 3, 4, 5, 6, 7)
  - [x] Create `app/src/main/java/com/siffermastare/domain/evaluation/PhoneNumberEvaluationStrategy.kt`
  - [x] Implement hybrid input decomposition:
    - Parse input into segments: prefix (chars 0-2), group1 (chars 3-5), pair1 (chars 6-7), pair2 (chars 8-9)
    - Prefix + group1: individual digit atoms
    - Pair1 + pair2: use `StandardNumberEvaluationStrategy.decompose()` for standard number decomposition
    - Handle pairs < 10 (e.g., "05" → atoms `["0", "5"]`)
  - [x] Use bag-logic comparison between `question.atoms` and decomposed input atoms
  - [x] Handle non-numeric input (fail all target atoms)
  - [x] Handle length mismatches
- [x] Update `PhoneNumberGenerator` to populate atoms (AC: 1)
  - [x] Implement `decomposePhoneNumber(target)` method to produce hybrid atoms
  - [x] Populate `question.atoms` in `generateLesson()`
  - [x] Switch `evaluationStrategy` from `ExactMatchEvaluationStrategy` to `PhoneNumberEvaluationStrategy`
- [x] Create `PhoneNumberEvaluationStrategyTest` (AC: 2, 3, 4, 5, 6, 7)
  - [x] Test correct input → all atoms Success
  - [x] Test partial error → granular bag-logic feedback
  - [x] Test pair with leading zero (e.g., 05 → digit atoms `["0", "5"]`)
  - [x] Test pair with teen (e.g., 12 → teen atom `["12"]`)
  - [x] Test pair with tens only (e.g., 20 → atom `["20"]`)
  - [x] Test pair with double zero (e.g., 00 → atoms `["0", "0"]`)
  - [x] Test completely wrong input → all atoms Failure
  - [x] Test non-numeric input, length mismatch
- [x] Update `PhoneNumberGeneratorTest` (AC: 1, 8)
  - [x] Add test verifying atoms for known target (hybrid decomposition)
  - [x] Add test verifying `evaluationStrategy` is `PhoneNumberEvaluationStrategy`
  - [x] Ensure existing format and leading zero tests still pass
- [x] Run full test suite and verify no regressions (AC: 8)

## Dev Notes

### Hybrid Atom Decomposition Rule
Phone numbers use **position-dependent** atom decomposition based on how they are spoken:
- **Prefix (07x)** + **Group 1 (xxx)**: individual digit atoms — each character is a separate atom
- **Pair 1 (XX)** + **Pair 2 (YY)**: standard number decomposition via `StandardNumberEvaluationStrategy.decompose()`
  - Exception: pairs < 10 (spoken "noll X") → individual digit atoms `["0", "X"]`

### Input Decomposition for Evaluation
The strategy must decompose user input using the **same hybrid rules** as the generator uses for target atoms:
1. If input length ≠ 10: grade available digits with bag-logic, set `isCorrect = false`
2. If input length == 10: split into prefix (0-2), group1 (3-5), pair1 (6-7), pair2 (8-9)
3. Decompose each segment per the rules above, then bag-compare against `question.atoms`

### Pair Decomposition Logic (mirrors `formatPair()`)
The `formatPair(n)` method in `PhoneNumberGenerator` determines how a pair is spoken:
- `n < 10`: "noll X" → atoms `["0", "digit"]` (two individual digit atoms)
- `n >= 10`: standard Swedish compound number → use `StandardNumberEvaluationStrategy.decompose(n)`

### Reuse Existing Decomposition
Use `StandardNumberEvaluationStrategy.decompose(number)` for pair atom decomposition. Do NOT duplicate the decomposition logic. [Source: StandardNumberEvaluationStrategy.kt companion](file:///c:/Users/Serge/source/repos/Siffermastare/app/src/main/java/com/siffermastare/domain/validation/strategies/StandardNumberEvaluationStrategy.kt)

### Project Structure Notes

**New files:**
- `app/src/main/java/com/siffermastare/domain/evaluation/PhoneNumberEvaluationStrategy.kt`
- `app/src/test/java/com/siffermastare/domain/evaluation/PhoneNumberEvaluationStrategyTest.kt`

**Modified files:**
- `app/src/main/java/com/siffermastare/domain/generators/PhoneNumberGenerator.kt` — populate atoms, switch strategy
- `app/src/test/java/com/siffermastare/domain/generators/PhoneNumberGeneratorTest.kt` — add atom verification tests

### Code Patterns to Follow

From Story 7.6:
- Use `question.atoms` as ground truth (never derive target atoms internally in strategy)
- Decomposition logic in companion objects or shared utilities
- Test helpers: use `createQuestion()` pattern
- Bag-logic: see `StandardNumberEvaluationStrategy.evaluate()` for reference

### References

- [Source: docs/learning-model-spec.md §4.1](file:///c:/Users/Serge/source/repos/Siffermastare/docs/learning-model-spec.md) — Atom source and grading rules
- [Source: docs/epics.md — Story 7.7](file:///c:/Users/Serge/source/repos/Siffermastare/docs/epics.md) — Epic requirements
- [Source: Story 7-6](file:///c:/Users/Serge/source/repos/Siffermastare/docs/sprint-artifacts/7-6-adapt-strategies-to-atoms-architecture.md) — Atoms architecture pattern
- [Source: PhoneNumberGenerator.kt](file:///c:/Users/Serge/source/repos/Siffermastare/app/src/main/java/com/siffermastare/domain/generators/PhoneNumberGenerator.kt) — Current generator with `formatPair()` logic
- [Source: StandardNumberEvaluationStrategy.kt](file:///c:/Users/Serge/source/repos/Siffermastare/app/src/main/java/com/siffermastare/domain/validation/strategies/StandardNumberEvaluationStrategy.kt) — `decompose()` companion for reuse

## Dev Agent Record

### Context Reference

<!-- Path(s) to story context XML will be added here by context workflow -->

### Agent Model Used

Antigravity (Claude)

### Debug Log References

No issues encountered. BUILD SUCCESSFUL on first run.

### Completion Notes List

- Created `PhoneNumberEvaluationStrategy` implementing `EvaluationStrategy` with hybrid input decomposition and bag-logic comparison
- Decomposition logic placed in `companion object` (`decomposeInput`, `decomposePhonePair`) for reuse by both strategy and generator
- `decomposePhonePair` delegates to `StandardNumberEvaluationStrategy.decompose()` for pairs ≥ 10, avoiding logic duplication
- Pairs < 10 decomposed as individual digit atoms `["0", "X"]` matching `formatPair()` spoken behavior
- Updated `PhoneNumberGenerator`: switched from `ExactMatchEvaluationStrategy` to `PhoneNumberEvaluationStrategy`, populates `question.atoms` via `decomposePhoneNumber()` in `generateLesson()`
- 20 new tests in `PhoneNumberEvaluationStrategyTest` covering AC2-AC7
- 2 new tests in `PhoneNumberGeneratorTest` verifying hybrid atom population (AC1) and strategy type
- All existing tests preserved and passing (AC8)
- Full test suite: BUILD SUCCESSFUL, 0 failures, 0 errors

### File List

- `app/src/main/java/com/siffermastare/domain/evaluation/PhoneNumberEvaluationStrategy.kt` [NEW]
- `app/src/test/java/com/siffermastare/domain/evaluation/PhoneNumberEvaluationStrategyTest.kt` [NEW]
- `app/src/main/java/com/siffermastare/domain/generators/PhoneNumberGenerator.kt` [MODIFIED]
- `app/src/test/java/com/siffermastare/domain/generators/PhoneNumberGeneratorTest.kt` [MODIFIED]
- `docs/sprint-artifacts/sprint-status.yaml` [MODIFIED]
- `docs/sprint-artifacts/7-7-phone-number-strategy.md` [MODIFIED]
