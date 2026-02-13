# Story 7.9: Ordinal Number Evaluation Strategy

Status: draft

## Story

As a learner,
I want the system to understand that ordinal "25:e" is composed of ordinal atoms `ord:20` and `ord:5`,
So that my mastery of ordinal numbers is tracked separately from cardinal numbers, reflecting that Swedish ordinals sound fundamentally different (e.g., *femte* vs *fem*).

## Context

Story 7-3 implemented `StandardNumberEvaluationStrategy` for cardinal numbers. Since Swedish ordinals have completely different spoken forms (e.g., *första*, *andra*, *tredje*, *tjugonde*), a user's ability to recognize cardinal "fem" does not imply ability to recognize ordinal "femte". The `learning-model-spec.md` §2.1 defines separate ordinal atoms with `ord:` prefix.

This strategy reuses the same **bag-logic** decomposition and comparison approach as `StandardNumberEvaluationStrategy`, but operates on `ord:`-prefixed atom IDs.

## Atom Decomposition Rules (Ordinal)

Decomposition follows the same structural rules as cardinals, but all atom IDs use the `ord:` prefix:

1. **Digits (0–9):** `ord:0, ord:1, ..., ord:9`
2. **Teens (10–19):** `ord:10, ord:11, ..., ord:19`
3. **Tens (20–90):** `ord:20, ord:30, ..., ord:90`

### Examples
- **Target:** "5" → Atoms: `[ord:5]`
- **Target:** "12" → Atoms: `[ord:12]`
- **Target:** "25" → Atoms: `[ord:20, ord:5]`
- **Target:** "123" → Atoms: `[ord:1, ord:20, ord:3]`
- **Target:** "1000" → Atoms: `[ord:1]`

## Output Contract

```kotlin
data class EvaluationResult(
    val isCorrect: Boolean,
    val atomUpdates: Map<String, List<Boolean>> = emptyMap()
)
```

## Acceptance Criteria

### Exact Match
1. **Given** An "Ordinal" Lesson (Target: "25", Atoms: `[ord:20, ord:5]`)
2. **When** I type "25"
3. **Then** `isCorrect = true`
4. **And** Atom `ord:20` is **Success**, Atom `ord:5` is **Success**

### Partial Match
1. **Given** An "Ordinal" Lesson (Target: "25", Atoms: `[ord:20, ord:5]`)
2. **When** I type "24"
3. **Then** `isCorrect = false`
4. **And** Atom `ord:20` is **Success** (correct tens stem)
5. **And** Atom `ord:5` is **Failure** (wrong unit)

### Complete Mismatch
1. **Given** An "Ordinal" Lesson (Target: "25", Atoms: `[ord:20, ord:5]`)
2. **When** I type "99"
3. **Then** `isCorrect = false`
4. **And** Atom `ord:20` is **Failure**, Atom `ord:5` is **Failure**

### 3-Digit Number
1. **Given** An "Ordinal" Lesson (Target: "123", Atoms: `[ord:1, ord:20, ord:3]`)
2. **When** I type "123"
3. **Then** `isCorrect = true`
4. **And** Atoms `ord:1`, `ord:20`, `ord:3` are all **Success**

### Structural Mismatch
1. **Given** An "Ordinal" Lesson (Target: "25", Atoms: `[ord:20, ord:5]`)
2. **When** I type "205"
3. **Then** `isCorrect = false`
4. **And** Atom `ord:5` is **Success** (present in both decompositions)
5. **And** Atom `ord:20` is **Failure** (input decomposes to `ord:2`, `ord:5` — no `ord:20`)

### Edge Cases
1. **Given** Target "0" → Atoms: `[ord:0]`
2. **When** I type "0"
3. **Then** `isCorrect = true`, Atom `ord:0` is **Success**
4. **Given** Target "1000" → Atoms: `[ord:1]`
5. **When** I type "1000"
6. **Then** `isCorrect = true`, Atom `ord:1` is **Success**

### Repeated Atoms (Bag Logic)
1. **Given** Target "555" → Atoms: `[ord:5, ord:50, ord:5]`
2. **When** I type "554"
3. **Then** `isCorrect = false`
4. **And** Atom `ord:50` is **Success**
5. **And** Atom `ord:5` has **Mixed Result** (1 Success, 1 Failure)

## Tasks / Subtasks

- [ ] Implement `OrdinalNumberEvaluationStrategy`
  - [ ] Create class implementing `EvaluationStrategy`
  - [ ] Reuse decomposition logic from `StandardNumberEvaluationStrategy` but produce `ord:`-prefixed atoms for user input
  - [ ] Implement bag-logic comparison between `question.atoms` (target) and decomposed input
- [ ] Unit Tests (`OrdinalNumberEvaluationStrategyTest`)
  - [ ] Exact match (25 vs 25) → ord:20 OK, ord:5 OK, isCorrect=True
  - [ ] Partial match (25 vs 24) → ord:20 OK, ord:5 Fail, isCorrect=False
  - [ ] Complete mismatch (25 vs 99) → ord:20 Fail, ord:5 Fail, isCorrect=False
  - [ ] 3-digit exact match (123 vs 123) → ord:1 OK, ord:20 OK, ord:3 OK, isCorrect=True
  - [ ] 3-digit partial match (123 vs 120) → ord:1 OK, ord:20 OK, ord:3 Fail, isCorrect=False
  - [ ] Structural mismatch (25 vs 205) → ord:5 OK, ord:20 Fail, isCorrect=False
  - [ ] Edge case 0 (0 vs 0) → ord:0 OK, isCorrect=True
  - [ ] Edge case 1000 (1000 vs 1000) → ord:1 OK, isCorrect=True
  - [ ] Repeated atoms (555 vs 554) → ord:50 OK, ord:5 Mixed (1 OK, 1 Fail), isCorrect=False

## Dev Notes

- **Implementation approach:** Consider extracting a shared base class or utility from `StandardNumberEvaluationStrategy` that accepts an atom prefix parameter. Both cardinal (prefix `""`) and ordinal (prefix `"ord:"`) strategies could use the same decomposition and comparison logic with just a different prefix. This avoids code duplication.
- **Alternative:** A simpler approach is to make `StandardNumberEvaluationStrategy` accept an optional `atomPrefix: String = ""` constructor parameter, and have `OrdinalNumberEvaluationStrategy` be a thin subclass or alias. The decomposition output is prefixed accordingly.
- **Input decomposition:** The strategy still needs to decompose **user input** into `ord:` atoms for comparison against the target. The target atoms come from `question.atoms` (per the Generator Owns Atoms rule).

### Project Structure Notes

- `app/src/main/java/com/siffermastare/domain/validation/strategies/OrdinalNumberEvaluationStrategy.kt`
- `app/src/test/java/com/siffermastare/domain/validation/strategies/OrdinalNumberEvaluationStrategyTest.kt`

### References

- [Story 7-3 (Standard Number Strategy)](file:///c:/Users/Serge/source/repos/Siffermastare/docs/sprint-artifacts/7-3-standard-number-strategy.md)
- [Story 7-6 (Atoms Architecture)](file:///c:/Users/Serge/source/repos/Siffermastare/docs/sprint-artifacts/7-6-adapt-strategies-to-atoms-architecture.md)
- [learning-model-spec.md §2.1](file:///c:/Users/Serge/source/repos/Siffermastare/docs/learning-model-spec.md)
