# Story 7.5: Informal Time Evaluation Strategy

Status: done

## Story

As a learner,
I want the system to understand that "Kvart över tre" can be written as "1515" or "0315",
So that I can practice informal time telling with flexible numeric input.

## Concept Atoms

Since "Informal Time" relies on specific vocabulary, we track these atoms:

**Structural Concept Atoms** (graded from InputMinute zone):
*   `#kvart` (15 or 45 min)
*   `#halv` (30 min zone: 21–39)

**Directional Concept Atoms** (require minute-number evidence):
*   `#over` (past)
*   `#i` (to)

**Number Atoms** (standard, no `#` prefix):
*   Hour atoms: `1`–`12`
*   Minute number atoms: `5`, `10`, `20` etc.

## Output Contract

```kotlin
data class EvaluationResult(
    val isCorrect: Boolean,
    val atomUpdates: Map<String, List<Boolean>> = emptyMap()
)
```

- `isCorrect`: True ONLY if User Input semantically matches the Stimulus Time.
- `atomUpdates`: For each **target** atom, a list of booleans (one per occurrence). Atoms with **no evidence** (SKIP) are **omitted** from the map entirely.

---

## Grading Rules

### Rule 1: Input Parsing
- Input is a digit string (no colon). Length 3 or 4.
- Last 2 chars = minute, remaining prefix = hour.
- `"550"` → H=5, M=50. `"0205"` → H=2, M=5.
- Leading zero is optional: `"300"` ≡ `"0300"`.

### Rule 2: `isCorrect` Determination
```
isCorrect = (InputHour % 12 == StimulusHour % 12) AND (InputMinute == StimulusMinute)
```
- "Klockan tolv" (StimulusHour=12): accepts 0000, 1200, 2400 (all ≡ 0 mod 12).

### Rule 3: Hour Atom Grading
The hour atom is the spoken number word (e.g., `3` in "halv **tre**"). Success if the user's input hour is consistent with hearing that number in **any** valid directional context:

```
HourAtomSuccess = InputHour % 12 ∈ { atomValue % 12, (atomValue − 1) % 12 }
```

- `atomValue % 12`: direct match ("klockan X", "X över Y")
- `(atomValue − 1) % 12`: offset match ("halv X", "X i Y", "X i/över halv Y")

### Rule 4: Minute Number Atom Grading
The minute number atom is the spoken number (e.g., `5` in "**fem** over"). Success if the user's input minute implies the same number, even via a different direction.

**Step 1** — Derive minute number from InputMinute:

| InputMinute range | Derived minute number |
|---|---|
| 0 | *none* |
| 1–14, 16–20 | InputMinute |
| 15 | *none* (concept `#kvart`) |
| 21–29 | 30 − InputMinute |
| 30 | *none* |
| 31–39 | InputMinute − 30 |
| 40–44, 46–59 | 60 − InputMinute |
| 45 | *none* (concept `#kvart`) |

**Step 2** — Compare:
- **Semantic match**: InputMinute == target's computed semantic minute value → ✅
- **Cross-direction match**: DerivedMinuteNumber == atom's literal value → ✅
- Neither → ❌

### Rule 5: Structural Concept Atoms (`#kvart`, `#halv`)
Always deterministic from InputMinute:

- `#kvart` → ✅ if InputMinute ∈ {15, 45}
- `#halv` → ✅ if InputMinute ∈ [21..39]

### Rule 6: Directional Concept Atoms (`#over`, `#i`)

**Precondition**: The corresponding minute-side atom (minute number atom, or `#kvart` if no number atom) must be ✅. If precondition fails → **SKIP** (omit from atomUpdates).

When precondition is met, derive direction from InputMinute:

| InputMinute | Direction |
|---|---|
| 1–20 (incl. 15) | `#over` |
| 21–29 | `#i` |
| 31–39 | `#over` |
| 40–59 (incl. 45) | `#i` |
| 0 or 30 | SKIP |

Compare inferred direction vs target direction → ✅ or ❌.

### Rule 7: Duplicate Atoms
Same atom ID in multiple target roles → `atomUpdates[id]` has one boolean per occurrence, graded independently by role (hour vs minute).

### Rule 8: No Extra Atoms
Only target atoms appear in atomUpdates. User-produced atoms not in the target are ignored.

---

## Test Specification (Single Source of Truth)

### 1. Exact Hour — "Klockan X"

**"Klockan två" (0200), Atoms: [`2`]**

| # | Input | isCorrect | `2` | Notes |
|---|-------|-----------|-----|-------|
| 1.1 | 0200 | ✅ | [✅] | |
| 1.2 | 1400 | ✅ | [✅] | 14%12=2 |
| 1.3 | 200 | ✅ | [✅] | Leading zero skipped |
| 1.4 | 0300 | ❌ | [❌] | 3%12≠2 and ≠1 |
| 1.5 | 0205 | ❌ | [✅] | Hour ok, extra minutes |

**"Klockan tolv" (1200), Atoms: [`12`]**

| # | Input | isCorrect | `12` | Notes |
|---|-------|-----------|------|-------|
| 1.6 | 1200 | ✅ | [✅] | |
| 1.7 | 0000 | ✅ | [✅] | 0%12=0=12%12 |
| 1.8 | 000 | ✅ | [✅] | Leading zero |
| 1.9 | 2400 | ✅ | [✅] | 24%12=0 |
| 1.10 | 0100 | ❌ | [❌] | 1≠0 and ≠11 |

### 2. Quarter Past — "Kvart över X"

**"Kvart över fyra" (0415), Atoms: [`#kvart`, `#over`, `4`]**

| # | Input | isCorrect | `#kvart` | `#over` | `4` | Notes |
|---|-------|-----------|----------|---------|-----|-------|
| 2.1 | 0415 | ✅ | ✅ | ✅ | [✅] | |
| 2.2 | 1615 | ✅ | ✅ | ✅ | [✅] | 16%12=4 |
| 2.3 | 0430 | ❌ | ❌ | SKIP | [✅] | #kvart❌→SKIP #over |
| 2.4 | 0445 | ❌ | ✅ | ❌ | [✅] | 45→#kvart✅, dir "i"≠"over" |
| 2.5 | 0515 | ❌ | ✅ | ✅ | [❌] | #kvart✅, dir "over"✅, 5≠4 and 5≠3 |

### 3. Half Past — "Halv X"

**"Halv tre" (0230), Atoms: [`#halv`, `3`]**

| # | Input | isCorrect | `#halv` | `3` | Notes |
|---|-------|-----------|---------|-----|-------|
| 3.1 | 0230 | ✅ | ✅ | [✅] | |
| 3.2 | 1430 | ✅ | ✅ | [✅] | 14%12=2=(3−1) |
| 3.3 | 0330 | ❌ | ✅ | [✅] | 3=literal, 30∈[21..39] |
| 3.4 | 0430 | ❌ | ✅ | [❌] | 4≠3 and 4≠2 |
| 3.5 | 0215 | ❌ | ❌ | [✅] | 15∉[21..39], 2=(3−1) |
| 3.6 | 0200 | ❌ | ❌ | [✅] | 0∉[21..39], 2=(3−1) |

### 4. Quarter To — "Kvart i X"

**"Kvart i fem" (0445), Atoms: [`#kvart`, `#i`, `5`]**

| # | Input | isCorrect | `#kvart` | `#i` | `5` | Notes |
|---|-------|-----------|----------|------|-----|-------|
| 4.1 | 0445 | ✅ | ✅ | ✅ | [✅] | |
| 4.2 | 0515 | ❌ | ✅ | ❌ | [✅] | #kvart✅, dir "over"≠"i", 5=literal |
| 4.3 | 0430 | ❌ | ❌ | SKIP | [✅] | #kvart❌→SKIP, 4=(5−1) |
| 4.4 | 0350 | ❌ | ❌ | SKIP | [❌] | #kvart❌→SKIP, 3≠5 and 3≠4 |

### 5. Minutes Past — "X över Y"

**"Fem över två" (0205), Atoms: [`5`, `#over`, `2`]**

| # | Input | isCorrect | `5` | `#over` | `2` | Notes |
|---|-------|-----------|-----|---------|-----|-------|
| 5.1 | 0205 | ✅ | [✅] | ✅ | [✅] | |
| 5.2 | 1405 | ✅ | [✅] | ✅ | [✅] | 14%12=2 |
| 5.3 | 0155 | ❌ | [✅] | ❌ | [✅] | derive(55)=5✅, dir "i"≠"over", 1=(2−1) |
| 5.4 | 0217 | ❌ | [❌] | SKIP | [✅] | derive(17)=17≠5, SKIP, 2=literal |
| 5.5 | 0455 | ❌ | [✅] | ❌ | [❌] | derive(55)=5✅, dir "i"≠"over", 4≠2 and 4≠1 |
| 5.6 | 0305 | ❌ | [✅] | ✅ | [❌] | 05=semantic✅, dir "over"✅, 3≠2 and 3≠1 |

### 6. Minutes To — "X i Y"

**"Fem i tre" (0255), Atoms: [`5`, `#i`, `3`]**

| # | Input | isCorrect | `5` | `#i` | `3` | Notes |
|---|-------|-----------|-----|------|-----|-------|
| 6.1 | 0255 | ✅ | [✅] | ✅ | [✅] | |
| 6.2 | 1455 | ✅ | [✅] | ✅ | [✅] | |
| 6.3 | 0305 | ❌ | [✅] | ❌ | [✅] | derive(05)=5✅, dir "over"≠"i", 3=literal |
| 6.4 | 0250 | ❌ | [❌] | SKIP | [✅] | derive(50)=10≠5, SKIP, 2=(3−1) |
| 6.5 | 0455 | ❌ | [✅] | ✅ | [❌] | derive(55)=5✅, dir "i"✅, 4≠3 and 4≠2 |

### 7. Minutes to Half — "X i halv Y"

**"Fem i halv tre" (0225), Atoms: [`5`, `#i`, `#halv`, `3`]**

| # | Input | isCorrect | `5` | `#i` | `#halv` | `3` | Notes |
|---|-------|-----------|-----|------|---------|-----|-------|
| 7.1 | 0225 | ✅ | [✅] | ✅ | ✅ | [✅] | |
| 7.2 | 1425 | ✅ | [✅] | ✅ | ✅ | [✅] | |
| 7.3 | 0235 | ❌ | [✅] | ❌ | ✅ | [✅] | derive(35)=5✅, dir "over"≠"i", 35∈halv, 2=(3−1) |
| 7.4 | 0205 | ❌ | [✅] | ❌ | ❌ | [✅] | derive(05)=5✅, dir "over"≠"i", 05∉halv, 2=(3−1) |
| 7.5 | 0325 | ❌ | [✅] | ✅ | ✅ | [✅] | derive(25)=5✅, dir "i"✅, 25∈halv, 3=literal |
| 7.6 | 0217 | ❌ | [❌] | SKIP | ❌ | [✅] | derive(17)≠5, SKIP, 17∉halv, 2=(3−1) |

### 8. Minutes past Half — "X över halv Y"

**"Fem över halv tre" (0235), Atoms: [`5`, `#over`, `#halv`, `3`]**

| # | Input | isCorrect | `5` | `#over` | `#halv` | `3` | Notes |
|---|-------|-----------|-----|---------|---------|-----|-------|
| 8.1 | 0235 | ✅ | [✅] | ✅ | ✅ | [✅] | |
| 8.2 | 0225 | ❌ | [✅] | ❌ | ✅ | [✅] | derive(25)=5✅, dir "i"≠"over", 25∈halv |
| 8.3 | 0205 | ❌ | [✅] | ✅ | ❌ | [✅] | derive(05)=5✅, dir "over"✅, 05∉halv |
| 8.4 | 0535 | ❌ | [✅] | ✅ | ✅ | [❌] | derive(35)=5✅, dir "over"✅, 35∈halv, 5≠3 and 5≠2 |

### 9. Larger Minutes — "Tio över X"

**"Tio över åtta" (0810), Atoms: [`10`, `#over`, `8`]**

| # | Input | isCorrect | `10` | `#over` | `8` | Notes |
|---|-------|-----------|------|---------|-----|-------|
| 9.1 | 0810 | ✅ | [✅] | ✅ | [✅] | |
| 9.2 | 0805 | ❌ | [❌] | SKIP | [✅] | derive(05)=5≠10, SKIP |
| 9.3 | 0850 | ❌ | [✅] | ❌ | [✅] | derive(50)=10✅, dir "i"≠"over" |

### 10. Larger Minutes — "Tjugo i X"

**"Tjugo i tre" (0240), Atoms: [`20`, `#i`, `3`]**

| # | Input | isCorrect | `20` | `#i` | `3` | Notes |
|---|-------|-----------|------|------|-----|-------|
| 10.1 | 0240 | ✅ | [✅] | ✅ | [✅] | |
| 10.2 | 0220 | ❌ | [✅] | ❌ | [✅] | derive(20)=20✅, dir "over"≠"i", 2=(3−1) |
| 10.3 | 0250 | ❌ | [❌] | SKIP | [✅] | derive(50)=10≠20, SKIP, 2=(3−1) |

### 11. Duplicate Atoms — "Tio över tio"

**"Tio över tio" (1010), Atoms: [`10`(min), `#over`, `10`(hr)]**

| # | Input | isCorrect | `10` | `#over` | Notes |
|---|-------|-----------|------|---------|-------|
| 11.1 | 1010 | ✅ | [✅,✅] | ✅ | Both occurrences match |
| 11.2 | 0110 | ❌ | [✅,❌] | ✅ | min 10✅, hr 1≠10 and 1≠9 |
| 11.3 | 1005 | ❌ | [❌,✅] | SKIP | min 05→derive=5≠10, hr 10✅ |

---

## Tasks / Subtasks

- [x] Core Model Update
  - [x] Add `val atoms: List<String> = emptyList()` to `Question` class

- [x] Update `InformalTimeGenerator`
  - [x] Populate `atoms` in `Question` according to Test Specification
  - [x] Implement derived atom logic (e.g. minute 21–29 → `[num, #i, #halv, nextHour]`)

- [x] Update `InformalTimeGeneratorTest`
  - [x] Extend existing tests to verify atoms list
  - [x] Verify ALL patterns from Test Specification

- [x] Implement `InformalTimeEvaluationStrategy`
  - [x] Implement `evaluate` using `question.atoms` as ground truth
  - [x] Input parsing (Rule 1)
  - [x] `isCorrect` via 12h/24h equivalence (Rule 2)
  - [x] Hour atom grading: literal/(−1) matching (Rule 3)
  - [x] Minute number atom grading: semantic/cross-direction (Rule 4)
  - [x] Structural concept atoms: `#kvart`, `#halv` from minute zone (Rule 5)
  - [x] Directional concept atoms: `#over`, `#i` with precondition + SKIP (Rule 6)
  - [x] Duplicate atom handling (Rule 7)
  - [x] No extra atoms (Rule 8)

- [x] Strategy Unit Tests
  - [x] Create `InformalTimeEvaluationStrategyTest`
  - [x] Implement ALL test cases from Test Specification (55 cases)

- [ ] Review Follow-ups
  - [ ] [AI-Review][MEDIUM] Investigate pre-existing flaky test `LessonViewModelTest.incorrectAnswer_incrementsAttempts` (`cardinal_0_20` generator)

## Dev Notes

- **Atom IDs:** `#kvart`, `#halv`, `#over`, `#i` (with `#` prefix). Number atoms without prefix: `1`, `2`, ..., `20`.
- **SKIP semantics:** When a directional atom cannot be graded, it is omitted from `atomUpdates`. The BKT engine treats absent atoms as "no observation" — neither success nor failure update.
- **`targetValue` format:** Pipe-delimited alternatives (e.g., `"0205|1405"`). The strategy uses the first value as the canonical StimulusTime.

### Project Structure Notes

- `app/src/main/java/com/siffermastare/domain/evaluation/InformalTimeEvaluationStrategy.kt`
- `app/src/test/java/com/siffermastare/domain/evaluation/InformalTimeEvaluationStrategyTest.kt`

### References

- [Source: docs/epics.md](file:///c:/Users/Serge/source/repos/Siffermastare/docs/epics.md)
- [Source: docs/learning-model-spec.md](file:///c:/Users/Serge/source/repos/Siffermastare/docs/learning-model-spec.md)

## Dev Agent Record

### Implementation Plan

1. Added `atoms: List<String> = emptyList()` to `Question` data class (backward-compatible default)
2. Added `buildAtoms(hour, minute)` to `InformalTimeGenerator` — derives atom list based on the time pattern
3. Swapped `evaluationStrategy` from `ExactMatchEvaluationStrategy` to `InformalTimeEvaluationStrategy`
4. Implemented `InformalTimeEvaluationStrategy` with all 8 rules using index-based atom processing
5. Key design decision: Use `indexOfLast` for number atoms to distinguish minute vs hour role in duplicates (e.g., "Tio över tio")

### Debug Log

- Initial implementation had a bug in duplicate atom handling (tests 11.2 and 11.3 failed)
- Root cause: `isHourAtom()` used `indexOf` which always returned first occurrence for duplicates
- Fix: Switched to index-based position tracking — pre-compute `lastNumberIndex` and pass `isHourPosition` flag per atom

### Completion Notes

- All 62 `InformalTimeEvaluationStrategyTest` cases pass (55 grading + 7 parseInput edge cases)
- All `InformalTimeGeneratorTest` cases pass (atom generation verified for all 8 patterns)
- No regressions from our changes (1 pre-existing flaky test in `LessonViewModelTest.incorrectAnswer_incrementsAttempts` — `cardinal_0_20` generator unrelated to this story)

## Senior Developer Review (AI)

**Reviewer:** Sergei | **Date:** 2026-02-23

### Findings Fixed (7/7)

| ID | Severity | Description | Resolution |
|----|----------|-------------|------------|
| H1 | HIGH | `parseInput` accepted hour > 24 | Added `if (hour > 24) return null` guard |
| H2 | HIGH | `deriveMinuteNumber` was dead wrapper over `computeSemanticMinuteValue` | Removed dead function, inlined call |
| H3 | HIGH | `minuteSideAtomIsSuccess` used fragile `numberAtoms.size` heuristic | Refactored to positional index check using `targetAtoms.take(dirIndex)` |
| M1 | MEDIUM | No tests for parseInput edge cases | Added 7 tests: empty, non-digit, short, long, hour>24, minute>59, boundary 2400 |
| M2 | MEDIUM | `buildAtoms()` / `formatInformalTime()` were public | Changed to `internal` |
| M3 | MEDIUM | epics.md AC used wrong format ("15:15" instead of "0315") | Fixed AC to digit-string format with `#` prefixed atoms |
| M4 | MEDIUM | Flaky test documented but no action item | Added Review Follow-up task |

### LOW Issues (Accepted)

- L1: Concept atom string constants — not blocking, future cleanup
- L2: Magic range numbers — not blocking, future cleanup
- L3: `2400` valid — confirmed by user

## File List

- `app/src/main/java/com/siffermastare/domain/models/Question.kt` (modified — added `atoms` field)
- `app/src/main/java/com/siffermastare/domain/generators/InformalTimeGenerator.kt` (modified — added `buildAtoms()`, swapped strategy, `internal` visibility)
- `app/src/main/java/com/siffermastare/domain/evaluation/InformalTimeEvaluationStrategy.kt` (new — reviewed: removed dead code, refactored minuteSideAtomIsSuccess, added hour guard)
- `app/src/test/java/com/siffermastare/domain/evaluation/InformalTimeEvaluationStrategyTest.kt` (new — 62 test cases: 55 grading + 7 parseInput)
- `app/src/test/java/com/siffermastare/domain/evaluation/InformalTimeIntegrationTest.kt` (new — 16 generator↔strategy integration tests)
- `app/src/test/java/com/siffermastare/domain/generators/InformalTimeGeneratorTest.kt` (modified — added atom verification tests)
- `docs/epics.md` (modified — fixed Story 7.5 AC)
- `docs/sprint-artifacts/sprint-status.yaml` (modified — status updates)

## Change Log

- 2026-02-13: Implemented Story 7.5 — Informal Time Evaluation Strategy with full atom-level grading (Rules 1-8), 55 test cases, and generator atom population
- 2026-02-23: Code Review — Fixed 3 HIGH + 4 MEDIUM issues: input validation, dead code, fragile heuristic, edge-case tests, visibility, stale AC, flaky test tracking
- 2026-02-23: Added 16 generator↔strategy integration tests verifying end-to-end Question production and evaluation
