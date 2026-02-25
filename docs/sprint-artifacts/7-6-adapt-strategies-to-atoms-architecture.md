# Story 7.6: Adapt Existing Strategies & Generators to Atoms-in-Question Architecture

Status: done

## Story

As a developer,
I want to refactor existing evaluation strategies and their corresponding generators to consume `question.atoms` instead of deriving atoms internally,
So that the system follows the single-source-of-truth rule ("Generator Owns Atoms") established in `learning-model-spec.md` §4.1.

## Context

Story 7-5 introduced the architectural rule: **generators produce atoms and attach them to the `Question` object; strategies use `question.atoms` as ground truth and never derive their own target atom list.** Story 7-5 also tasks adding `val atoms: List<String>` to the `Question` data class.

Stories 7-3 (Standard Number Strategy) and 7-4 (Digital Time Strategy) were implemented **before** this rule existed. Both strategies contain internal `decompose()` methods that derive atoms from the target value at evaluation time. This story brings them into alignment.

## Scope

### In Scope

1. **`StandardNumberEvaluationStrategy`** — Remove internal `decompose(target)` call; use `question.atoms` instead.
2. **`CardinalGenerator`** — Populate `Question.atoms` using standard number decomposition logic. Switch `evaluationStrategy` from `ExactMatchEvaluationStrategy` to `StandardNumberEvaluationStrategy`.
3. **`TrickyPairsGenerator`** — Populate `Question.atoms` using standard number decomposition logic. Switch `evaluationStrategy` from `ExactMatchEvaluationStrategy` to `StandardNumberEvaluationStrategy`.
4. **`DigitalTimeEvaluationStrategy`** — Remove internal `decomposeTimeFull()` / `decomposeHours()` / `decomposeMinutes()` calls for **target**; use `question.atoms` instead. Keep input decomposition for comparison.
5. **`TimeGenerator`** — Populate `Question.atoms` using digital time decomposition logic.
6. **All corresponding tests** — Update to pass atoms in `Question` and verify generators produce correct atoms.

### Out of Scope

- `InformalTimeGenerator` + `InformalTimeEvaluationStrategy` — handled by Story 7-5.
- `OrdinalGenerator` + `OrdinalNumberEvaluationStrategy` — deferred to Story 7-9 (ordinals need a dedicated strategy with `ord:` prefix).
- Future strategies (Phone, Fractions, Decimals) — Stories 7-7, 7-8, 7-10 will be designed with the new architecture natively.

## Acceptance Criteria

### AC1: Question.atoms is populated by generators

1. **Given** A `CardinalGenerator` with range 0–1000
2. **When** It generates a question for target "25"
3. **Then** `question.atoms` == `["20", "5"]`
4. **And** `question.evaluationStrategy` is `StandardNumberEvaluationStrategy` (not `ExactMatchEvaluationStrategy`)

### AC2: StandardNumberEvaluationStrategy uses question.atoms

1. **Given** A question with `targetValue = "25"` and `atoms = ["20", "5"]`
2. **When** I evaluate input "24"
3. **Then** Atom `20` is **Success**, Atom `5` is **Failure**, `isCorrect = false`
4. **And** The strategy does NOT internally decompose the target — it uses `question.atoms` directly

### AC3: TimeGenerator populates atoms

1. **Given** A `TimeGenerator`
2. **When** It generates a question for target "0515"
3. **Then** `question.atoms` == `["0", "5", "15"]`

### AC4: DigitalTimeEvaluationStrategy uses question.atoms

1. **Given** A question with `targetValue = "0515"` and `atoms = ["0", "5", "15"]`
2. **When** I evaluate input "0515"
3. **Then** All atoms are **Success**, `isCorrect = true`
4. **And** The strategy does NOT internally decompose the target

### AC5: TrickyPairsGenerator populates atoms

1. **Given** A `TrickyPairsGenerator`
2. **When** It generates a question for target "30"
3. **Then** `question.atoms` == `["30"]`
4. **And** `question.evaluationStrategy` is `StandardNumberEvaluationStrategy`

### AC6: All existing tests pass

1. **Given** The refactored code
2. **When** All tests in `StandardNumberEvaluationStrategyTest` and `DigitalTimeEvaluationStrategyTest` are run
3. **Then** All tests pass (existing behavior is preserved)

## Tasks / Subtasks

- [x] Move decomposition logic to generators
  - [x] Extract `decompose()` from `StandardNumberEvaluationStrategy` into a shared utility or directly into generators
  - [x] Extract time decomposition logic from `DigitalTimeEvaluationStrategy` into `TimeGenerator`
- [x] Update generators to populate `Question.atoms`
  - [x] `CardinalGenerator` — populate atoms, switch to `StandardNumberEvaluationStrategy`
  - [x] `TrickyPairsGenerator` — populate atoms, switch to `StandardNumberEvaluationStrategy`
  - [x] `TimeGenerator` — populate atoms
- [x] Refactor strategies to consume `question.atoms`
  - [x] `StandardNumberEvaluationStrategy.evaluate()` — use `question.atoms` instead of `decompose(target)`
  - [x] `DigitalTimeEvaluationStrategy.evaluate()` — use `question.atoms` for target atoms
- [x] Update tests
  - [x] Update `StandardNumberEvaluationStrategyTest` — pass atoms in Question for all test cases
  - [x] Update `DigitalTimeEvaluationStrategyTest` — pass atoms in Question for all test cases
  - [x] Add generator tests for atoms output (CardinalGenerator, TrickyPairsGenerator, TimeGenerator)
- [x] Verify all existing tests still pass

## Dev Notes

- **Decomposition logic reuse:** The `decompose()` method in `StandardNumberEvaluationStrategy.companion` is used by generators for atom population AND by the strategy for decomposing _user input_ during evaluation.
- **Digital Time decomposition reuse:** `DigitalTimeEvaluationStrategy` uses `TimeGenerator.decomposeTwoDigitPart()` for input decomposition. Target atoms come from `question.atoms`.
- **Strategy wiring:** `CardinalGenerator` and `TrickyPairsGenerator` now use `StandardNumberEvaluationStrategy` instead of the original `ExactMatchEvaluationStrategy`.
- **Dependencies:** This story depends on Story 7-5's first task (adding `atoms` to `Question`). If 7-5 is not yet started, the `Question` model change can be done as part of this story or coordinated.
- **Ordinals:** `OrdinalGenerator` atom population and strategy wiring is deferred to Story 7-9, which introduces a dedicated `OrdinalNumberEvaluationStrategy`.

### Project Structure Notes

Files to modify:
- `app/src/main/java/com/siffermastare/domain/models/Question.kt`
- `app/src/main/java/com/siffermastare/domain/generators/CardinalGenerator.kt`
- `app/src/main/java/com/siffermastare/domain/generators/OrdinalGenerator.kt`
- `app/src/main/java/com/siffermastare/domain/generators/TrickyPairsGenerator.kt`
- `app/src/main/java/com/siffermastare/domain/generators/TimeGenerator.kt`
- `app/src/main/java/com/siffermastare/domain/validation/strategies/StandardNumberEvaluationStrategy.kt`
- `app/src/main/java/com/siffermastare/domain/evaluation/DigitalTimeEvaluationStrategy.kt`
- `app/src/test/java/com/siffermastare/domain/validation/strategies/StandardNumberEvaluationStrategyTest.kt`
- `app/src/test/java/com/siffermastare/domain/evaluation/DigitalTimeEvaluationStrategyTest.kt`

### References

- [Source: docs/learning-model-spec.md §4.1](file:///c:/Users/Serge/source/repos/Siffermastare/docs/learning-model-spec.md)
- [Story 7-3](file:///c:/Users/Serge/source/repos/Siffermastare/docs/sprint-artifacts/7-3-standard-number-strategy.md)
- [Story 7-4](file:///c:/Users/Serge/source/repos/Siffermastare/docs/sprint-artifacts/7-4-time-evaluation-strategy.md)
- [Story 7-5](file:///c:/Users/Serge/source/repos/Siffermastare/docs/sprint-artifacts/7-5-informal-time-evaluation-strategy.md)

## Dev Agent Record

### Implementation Plan
- Moved `decompose()` to `StandardNumberEvaluationStrategy.companion` with `prefix` parameter for ordinal support
- Extracted time decomposition into `TimeGenerator.companion` (`decomposeTime()`, `decomposeTwoDigitPart()`)
- All strategies now read `question.atoms` as ground truth — no internal target decomposition
- Input decomposition preserved: strategies still decompose user input for comparison

### Completion Notes
- ✅ `StandardNumberEvaluationStrategy` — companion `decompose(number, prefix)`, reads `question.atoms`
- ✅ `DigitalTimeEvaluationStrategy` — uses `question.atoms` for target, delegates input decomp to `TimeGenerator.decomposeTwoDigitPart()`
- ✅ `CardinalGenerator` — populates atoms via `decompose()`, uses `StandardNumberEvaluationStrategy`
- ✅ `TrickyPairsGenerator` — populates atoms, uses `StandardNumberEvaluationStrategy`
- ✅ `TimeGenerator` — populates atoms via `decomposeTime()`
- ✅ All existing tests updated with atom-aware `createQuestion()` helpers
- ✅ New generator atom tests added for CardinalGenerator, TrickyPairsGenerator, TimeGenerator
- ✅ Full regression suite passes

### Debug Log
- Initial build failure was transient Gradle daemon issue; resolved on retry

## File List

### Modified
- `app/src/main/java/com/siffermastare/domain/validation/strategies/StandardNumberEvaluationStrategy.kt`
- `app/src/main/java/com/siffermastare/domain/evaluation/DigitalTimeEvaluationStrategy.kt`
- `app/src/main/java/com/siffermastare/domain/generators/CardinalGenerator.kt`
- `app/src/main/java/com/siffermastare/domain/generators/TrickyPairsGenerator.kt`
- `app/src/main/java/com/siffermastare/domain/generators/TimeGenerator.kt`
- `app/src/test/java/com/siffermastare/domain/validation/strategies/StandardNumberEvaluationStrategyTest.kt`
- `app/src/test/java/com/siffermastare/domain/evaluation/DigitalTimeEvaluationStrategyTest.kt`
- `app/src/test/java/com/siffermastare/domain/generators/NumberGeneratorTest.kt`
- `app/src/test/java/com/siffermastare/domain/generators/TrickyPairsGeneratorTest.kt`
- `app/src/test/java/com/siffermastare/domain/generators/TimeGeneratorTest.kt`

## Change Log

- 2026-02-25: Adapted strategies to atoms architecture — strategies now consume `question.atoms`, generators populate atoms, decomposition logic extracted to companion objects
- 2026-02-25: Code review fixes — Fixed `DigitalTimeEvaluationStrategy` to truly use `question.atoms` (was re-deriving from target string); removed ordinal references (deferred to Story 7-9); updated `epics.md` Story 7.3 AC; improved `TrickyPairsGeneratorTest`
