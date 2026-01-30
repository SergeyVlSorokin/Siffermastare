# Story 7.4: Digital Time Evaluation Strategy

Status: done

## Story

As a learner,
I want the system to strict evaluation for Digital Time questions (e.g. "1400"),
So that I can practice exact 24-hour time notation without ambiguity.

## Acceptance Criteria

### Exact Match
1. **Given** A "Digital Time" Lesson (Target: "1430")
2. **When** I type "1430"
3. **Then** It is marked **Correct**
4. **And** Atoms `14` (Teen) and `30` (Ten) are marked **Success**

### Teens Boundary (10 and 19)
1. **Given** A "Digital Time" Lesson (Target: "1019")
2. **When** I type "1019"
3. **Then** It is marked **Correct**
4. **And** Atoms `10` and `19` are marked **Success**

### 24h Mismatch
1. **Given** A "Digital Time" Lesson (Target: "0230")
2. **When** I type "1430"
3. **Then** It is marked **Incorrect**
4. **And** Atom `30` is **Success** (Minutes match)
5. **And** Atom `0` is **Failure** (Hour mismatch)
6. **And** Atom `2` is **Failure** (Hour mismatch)

### Minute Mismatch
1. **Given** A "Digital Time" Lesson (Target: "1415")
2. **When** I type "1430"
3. **Then** It is marked **Incorrect**
4. **And** Atom `14` is **Success**
5. **And** Atom `15` is **Failure** (Target 15)

### Complex Minute
1. **Given** A "Digital Time" Lesson (Target: "0515")
2. **When** I type "0515"
3. **Then** It is marked **Correct**
4. **And** Atoms `0`, `5`, and `15` are marked **Success**

### Midnight (Double Zero)
1. **Given** A "Digital Time" Lesson (Target: "0030")
2. **When** I type "0030"
3. **Then** It is marked **Correct**
4. **And** Atoms `0`, `0`, and `30` are marked **Success**

### Triple Zero
1. **Given** A "Digital Time" Lesson (Target: "0003")
2. **When** I type "0003"
3. **Then** It is marked **Correct**
4. **And** Atoms `0`, `0`, `0`, and `3` are marked **Success**

### Triple Zero Error
1. **Given** A "Digital Time" Lesson (Target: "0003")
2. **When** I type "0103"
3. **Then** It is marked **Incorrect**
4. **And** Two `0` Atoms are **Success**, One `0` Atom is **Failure**
5. **And** Atom `3` is **Success**

### Leading Zero Optional
1. **Given** A "Digital Time" Lesson (Target: "0513")
2. **When** I type "513"
3. **Then** It is marked **Correct**
4. **And** Atoms `0`, `5`, and `13` are marked **Success** (System infers the leading zero knowledge)

## Atom Decomposition Rules (Spoken Swedish)

The decomposition is strictly based on how the time is **spoken** in Swedish digit-by-digit or group-by-group.

1.  **Hours (HH)**
    *   **Leading Zero (01-09):** Spoken as "noll [number]".
        *   Example "05": Atoms `0`, `5`.
    *   **Double Zero (00):** Spoken as "noll noll".
        *   Example "00": Atoms `0`, `0`.
    *   **Standard (10-23):** Spoken as normal number (decomposed if composite).
        *   Example "14": Atom `14`.
        *   Example "22": Atoms `20`, `2`.

2.  **Minutes (MM)**
    *   **Leading Zero (01-09):** Spoken as "noll [number]".
        *   Example "03": Atoms `0`, `3`.
    *   **Double Zero (00):** Spoken as "noll noll".
        *   Example ":00": Atoms `0`, `0`.
    *   **Standard (10-59):** Spoken as normal number (decomposed if composite).
        *   Example "30": Atom `30`.
        *   Example "45": Atoms `40`, `5`.

### Note

Leading zero is optional for hours. Skipping leading zero is not a failure and will result in a successful evaluation and credit given for recognition of the leading zero atom.

### Examples
*   **Target:** "1445" ("Fjorton fyrtio-fem")
    *   Hour "14": Atom `14`
    *   Minute "45": Atom `40`, Atom `5`
*   **Target:** "0505" ("Noll fem noll fem")
    *   Hour "05": Atom `0`, Atom `5`
    *   Minute "05": Atom `0`, Atom `5`
*   **Target:** "0003" ("Noll noll noll tre")
    *   Hour "00": Atom `0`, Atom `0`
    *   Minute "03": Atom `0`, Atom `3`

## Tasks / Subtasks

- [ ] Implement `DigitalTimeEvaluationStrategy`
  - [ ] Implement `EvaluationStrategy` interface
  - [ ] Implement strict 24h parsing and comparison (expecting only digits)
  - [ ] Implement atom decomposition for Hours and Minutes treating them as standard numbers
- [ ] Unit Tests
  - [ ] Test exact match (1430 vs 1430) -> 14 OK, 30 OK, isCorrect=True
  - [ ] Test Teens Boundary (1019 vs 1019) -> 10 OK, 19 OK, isCorrect=True
  - [ ] Test 24h Mismatch (0230 vs 1430) 
    - [ ] Target Atoms: 0, 2, 30
    - [ ] Input Atoms: 14, 30
    - [ ] Result: 30 OK, 0 Fail, 2 Fail, isCorrect=False
  - [ ] Test Minute Mismatch (1415 vs 1430)
    - [ ] Target Atoms: 14, 15
    - [ ] Input Atoms: 14, 30
    - [ ] Result: 14 OK, 15 Fail, isCorrect=False
  - [ ] Test complex minute (0515 vs 0515) -> 5 OK, 15 OK, isCorrect=True
  - [ ] Test Midnight (0030 vs 0030) -> 0 [OK, OK], 30 OK, isCorrect=True
  - [ ] Test Triple Zero (0003 vs 0003) -> 0 [OK, OK, OK], 3 OK, isCorrect=True
  - [ ] Test Triple Zero Error (0003 vs 0103) -> 0 [OK, OK, Fail], 3 OK, isCorrect=False
  - [ ] Test Leading Zero Optional (0513 vs 513) ->  0 OK, 5 OK, 13 OK, isCorrect=True

## Dev Notes

- **Note:** This strategy is exclusively for the "Tid (Digital)" module where the prompt is read as digits (e.g. "fjorton, noll, noll") or displayed digitally.
- **Input Format:** The strategy receives raw strings of digits (e.g. "1400"). Any visual separators (like :) are handled by the UI or stripped before validation.
- **Atoms:** Reuse standard number atoms (0-9, 10-19, 20-90).

### Project Structure Notes

- `app/src/main/java/com/siffermastare/domain/evaluation/DigitalTimeEvaluationStrategy.kt`
- `app/src/test/java/com/siffermastare/domain/evaluation/DigitalTimeEvaluationStrategyTest.kt`

### References

- [Source: docs/epics.md](file:///c:/Users/Serge/source/repos/Siffermastare/docs/epics.md)
