# Story 7.3: Standard Number Strategy

Status: done

## Story

As a learner,
I want the system to understand that "25" is composed of "20" and "5",
so that I can get partial credit if I mistype one part.

## Acceptance Criteria

1. **Given** A "Cardinal" or "Ordinal" Lesson
2. **When** Target is "25" and I type "24"
3. **Then** It generates `EvaluationResult`
4. **And** Atom `20` is **Success** (Correct stem)
5. **And** Atom `5` is **Failure** (Missed the digit)
6. **And** The Strategy handles standard integers (0-1000)
7. **And** `isCorrect` should be **False** (Partial match is not strict equality)
8. **And** Atom naming aligns strictly with `learning-model-spec.md` (e.g. "20" not "tjugo")

### 3-Digit Numbers
1. **Given** Target is "123" (Decomposed per spec: `{1, 20, 3}`)
2. **When** I type "123"
3. **Then** Atoms `1`, `20`, `3` are **Success**
4. **And** `isCorrect` is **True**
5. **When** I type "120" (Decomposed: `{1, 20}`)
6. **Then** Atoms `1`, `20` are **Success**, Atom `3` is **Failure**
7. **And** `isCorrect` is **False**

### Structural Mismatch
1. **Given** Target is "25" (Atoms: `{20, 5}`)
2. **When** I type "205" (Decomposed: `{2, 5}`)
3. **Then** Atom `5` is **Success** (Present in both)
4. **And** Atom `20` is **Failure** (Missing from input)
5. **And** `isCorrect` is **False**
6. **And** The system does not crash or error on length mismatch

### Edge Cases
1. **Given** Target is "0"
2. **When** I type "0"
3. **Then** Atom `0` is **Success**
4. **And** `isCorrect` is **True**
5. **Given** Target is "1000" (Decomposed to: `{1}`)
6. **When** I type "1000"
7. **Then** Atom `1` is **Success**
8. **And** `isCorrect` is **True**

### Repeated Atoms (Bag Logic)
1. **Given** Target is "555" (Decomposed: `{5, 50, 5}`)
2. **When** I type "554" (Decomposed: `{5, 50, 4}`)
3. **Then** Atom `50` is **Success** (Present in both)
4. **And** Atom `5` has **Mixed Result** (1 Success, 1 Failure)
5. **And** `isCorrect` is **False**

## Tasks / Subtasks

- [ ] Implement `StandardNumberEvaluationStrategy`
  - [ ] Create class implementing `EvaluationStrategy`
  - [ ] Implement decomposition logic for numbers 0-1000 (e.g., 25 -> 20 + 5)
  - [ ] Implement comparison logic to identify partial matches
- [ ] Implement `EqualityResult` (if not already existing or part of `EvaluationResult`)
  - [ ] Ensure result structure supports atomic feedback map
- [ ] Unit Tests
  - [ ] Test exact match (25 vs 25) -> 20 OK, 5 OK, isCorrect=True
  - [ ] Test partial match (25 vs 24) -> 20 OK, 5 Fail, isCorrect=False
  - [ ] Test complete mismatch (25 vs 99) -> 20 Fail, 5 Fail, isCorrect=False
  - [ ] Test 3-digit exact match (123 vs 123) -> 1 OK, 20 OK, 3 OK, isCorrect=True
  - [ ] Test 3-digit partial match (123 vs 120) -> 1 OK, 20 OK, 3 Fail, isCorrect=False
  - [ ] Test 3-digit handreds only match (500 vs 500) -> 5 OK, isCorrect=True
  - [ ] Test structural mismatch (25 vs 205) -> 5 OK, 20 Fail, isCorrect=False
  - [ ] Test edge case 0 (0 vs 0) -> 0 OK, isCorrect=True
  - [ ] Test edge case 1000 (1000 vs 1000) -> 1 OK, isCorrect=True
  - [ ] Test repeated atoms (555 vs 554) -> 50 OK, 5 Mixed (1 OK, 1 Fail), isCorrect=False

## Dev Notes

- **Architecture Pattern:** Implementation of the **Strategy Pattern** for `EvaluationStrategy`.
- **Domain Logic:** This logic belongs in `domain/validation` or similar package.
- **Decomposition:** Need a helper or logic to decompose Swedish numbers into "Atoms" (e.g., "tjugofem" -> "tjugo", "fem"). This might overlap with `NumberGenerator` logic or be a reverse operation.
- **EvaluationResult:** Must update to `Map<String, List<Boolean>>` (or similar) to support per-instance grading (e.g. `5` -> `[true, false]`).
- **Atomic IDs:** Must define a consistent ID scheme for atoms (e.g., "atom_20", "atom_5") to match what is stored in the DB.

### Project Structure Notes

- `app/src/main/java/com/siffermastare/domain/validation/strategies/StandardNumberEvaluationStrategy.kt`
- `app/src/test/java/com/siffermastare/domain/validation/strategies/StandardNumberEvaluationStrategyTest.kt`

### References

- [Source: docs/epics.md#Section-7.3](file:///c:/Users/Serge/source/repos/Siffermastare/docs/epics.md)
- [Source: docs/architecture.md#Generator-Strategy-Pattern](file:///c:/Users/Serge/source/repos/Siffermastare/docs/architecture.md)

## Dev Agent Record

### Context Reference

### Agent Model Used

Antigravity (simulated Create Story Workflow)

### Completion Notes List

- Created story based on Epic 7.3 requirements.
- Identified need for decomposition logic.
