# Story 7.6: Adapt Existing Strategies & Generators to Atoms-in-Question Architecture

Status: draft

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
3. **`OrdinalGenerator`** — Populate `Question.atoms` using **ordinal-prefixed** atoms (e.g., `ord:20`, `ord:5`). Switch to `OrdinalNumberEvaluationStrategy` (Story 7-9). The decomposition structure is identical to cardinals, but atom IDs use the `ord:` prefix to track mastery separately.
4. **`TrickyPairsGenerator`** — Populate `Question.atoms` using standard number decomposition logic. Switch `evaluationStrategy` from `ExactMatchEvaluationStrategy` to `StandardNumberEvaluationStrategy`.
5. **`DigitalTimeEvaluationStrategy`** — Remove internal `decomposeTimeFull()` / `decomposeHours()` / `decomposeMinutes()` calls for **target**; use `question.atoms` instead. Keep input decomposition for comparison.
6. **`TimeGenerator`** — Populate `Question.atoms` using digital time decomposition logic.
7. **All corresponding tests** — Update to pass atoms in `Question` and verify generators produce correct atoms.

### Out of Scope

- `InformalTimeGenerator` + `InformalTimeEvaluationStrategy` — handled by Story 7-5.
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

### AC6: OrdinalGenerator populates ordinal atoms

1. **Given** An `OrdinalGenerator`
2. **When** It generates a question for target "25"
3. **Then** `question.atoms` == `["ord:20", "ord:5"]`
4. **And** For target "13", `question.atoms` == `["ord:13"]`
5. **And** For target "7", `question.atoms` == `["ord:7"]`

### AC7: All existing tests pass

1. **Given** The refactored code
2. **When** All tests in `StandardNumberEvaluationStrategyTest` and `DigitalTimeEvaluationStrategyTest` are run
3. **Then** All tests pass (existing behavior is preserved)

## Tasks / Subtasks

- [ ] Move decomposition logic to generators
  - [ ] Extract `decompose()` from `StandardNumberEvaluationStrategy` into a shared utility or directly into generators
  - [ ] Extract time decomposition logic from `DigitalTimeEvaluationStrategy` into `TimeGenerator`
- [ ] Update generators to populate `Question.atoms`
  - [ ] `CardinalGenerator` — populate atoms, switch to `StandardNumberEvaluationStrategy`
  - [ ] `OrdinalGenerator` — populate ordinal-prefixed atoms (`ord:` prefix), switch to `OrdinalNumberEvaluationStrategy` (Story 7-9)
  - [ ] `TrickyPairsGenerator` — populate atoms, switch to `StandardNumberEvaluationStrategy`
  - [ ] `TimeGenerator` — populate atoms
- [ ] Refactor strategies to consume `question.atoms`
  - [ ] `StandardNumberEvaluationStrategy.evaluate()` — use `question.atoms` instead of `decompose(target)`
  - [ ] `DigitalTimeEvaluationStrategy.evaluate()` — use `question.atoms` for target atoms
- [ ] Update tests
  - [ ] Update `StandardNumberEvaluationStrategyTest` — pass atoms in Question for all test cases
  - [ ] Update `DigitalTimeEvaluationStrategyTest` — pass atoms in Question for all test cases
  - [ ] Add generator tests for atoms output (CardinalGenerator, OrdinalGenerator, TrickyPairsGenerator, TimeGenerator)
- [ ] Verify all existing tests still pass

## Dev Notes

- **Ordinal atom prefix:** `OrdinalGenerator` uses `ord:` prefix for all atoms (e.g., `ord:20`, `ord:5`). This tracks ordinal mastery separately from cardinal mastery per `learning-model-spec.md` §2.1. Swedish ordinals sound fundamentally different from cardinals (e.g., *femte* vs *fem*, *tjugonde* vs *tjugo*), so a user's ability to recognize one does not imply ability to recognize the other.
- **Strategy reuse:** `StandardNumberEvaluationStrategy` works unchanged for ordinals — the bag-logic comparison is atom-ID agnostic. The strategy compares atoms by string ID, so `ord:20` in the target is matched against `ord:20` in the decomposed input. The strategy's internal `decompose()` for **user input** will need an `ordinal: Boolean` parameter (or the generator passes a prefix) to produce matching `ord:` atoms.
- **Decomposition logic reuse:** The `decompose()` method currently in `StandardNumberEvaluationStrategy` is still needed for decomposing _user input_ during evaluation. Consider keeping a `companion object` or utility for input decomposition, while removing target decomposition from the strategy.
- **Digital Time decomposition reuse:** Similarly, `DigitalTimeEvaluationStrategy` still needs input decomposition. Only remove the _target_ atom derivation path.
- **Strategy wiring:** `CardinalGenerator` and `OrdinalGenerator` currently use `ExactMatchEvaluationStrategy`. This story should switch them to `StandardNumberEvaluationStrategy` since that strategy was built specifically for them (Story 7-3). This was likely deferred during 7-3 implementation.
- **Dependencies:** This story depends on Story 7-5's first task (adding `atoms` to `Question`). If 7-5 is not yet started, the `Question` model change can be done as part of this story or coordinated.

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
