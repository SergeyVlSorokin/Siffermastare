# Story 7.2: Evaluation Strategy Framework

Status: ready-for-dev

## Story

As a developer,
I need a flexible way to evaluate user answers against complex rules (e.g. "14:00" == "2:00"),
So that the system can accurately attribute knowledge even when there are multiple correct ways to answer.

## Acceptance Criteria

### AC1: Strategy Interface
**Given** A `NumberGenerator`
**Then** It should define an `EvaluationStrategy`
**And** The Strategy should accept `UserInput` (String) and `Question`
**And** It should return an `EvaluationResult`

### AC2: ExactMatchEvaluationStrategy
**Given** I use the `ExactMatchEvaluationStrategy`
**When** Input matches Target exactly (ignoring strict whitespace if needed)
**Then** It returns `isCorrect = true`
**When** Input differs
**Then** It returns `isCorrect = false`
**And** This strategy is used as the default for current generators to maintain existing behavior

### AC3: Evaluation Result
**Given** An evaluation is performed
**Then** The `EvaluationResult` should contain:
  - `isCorrect`: Boolean
  - `atomUpdates`: Map<String, Boolean> (AtomID -> Success/Failure)

## Developer Context

### Technical Requirements

- **Interface**: Create `EvaluationStrategy` in `domain` layer.
- **Models**: Create `EvaluationResult` data class.
- **Implementation**: Create `ExactMatchEvaluationStrategy` that implements strict string equality.
- **Integration**: Update `NumberGenerator` interface to include `val evaluationStrategy: EvaluationStrategy`.
- **Refactoring**: Update all existing Generators (`Cardinal`, `Ordinal`, `Time`, `Phone`, `Fraction`, `Decimal`) to return `ExactMatchEvaluationStrategy` temporarily (until specific strategies are built in later stories).

### Architecture Compliance

- **Layer**: Domain Layer.
- **Pattern**: Strategy Pattern.
- **Location**: `com.siffermastare.domain.evaluation`.

### File Structure Requirements

- `app/src/main/java/com/siffermastare/domain/evaluation/EvaluationStrategy.kt`
- `app/src/main/java/com/siffermastare/domain/evaluation/EvaluationResult.kt`
- `app/src/main/java/com/siffermastare/domain/evaluation/ExactMatchEvaluationStrategy.kt`
- Update `app/src/main/java/com/siffermastare/domain/generators/NumberGenerator.kt`

### Testing Requirements

- **Unit Tests**:
  - `ExactMatchEvaluationStrategyTest`: Verify "10"=="10", "10"!="11", etc.
  - `EvaluationResultTest`: Verify data holding.

## Dev Agent Record

### Context Reference

- Architecture: `docs/architecture.md`
- Epics: `docs/epics.md`

### Architectural Decisions
- **Hybrid Validation Strategy**: Instead of a strict cutover or Adapter pattern, `LessonSessionManager` now supports a Hybrid approach. It prioritizes the legacy `validator` function (if injected by ViewModel for Time/Phone lessons) to preserve complex behavior. If no validator is provided, it uses the new `EvaluationStrategy` from the Generator. This ensures zero regression for legacy types while allowing new types to use the Strategy pattern immediately.

### File List

- [x] com/siffermastare/domain/evaluation/EvaluationStrategy.kt
- [x] com/siffermastare/domain/evaluation/EvaluationResult.kt
- [x] com/siffermastare/domain/evaluation/ExactMatchEvaluationStrategy.kt
- [x] com/siffermastare/domain/generators/NumberGenerator.kt
#### Modified Generators (migrated to Strategy interface)
- [x] com/siffermastare/domain/generators/CardinalGenerator.kt
- [x] com/siffermastare/domain/generators/OrdinalGenerator.kt
- [x] com/siffermastare/domain/generators/DecimalsGenerator.kt
- [x] com/siffermastare/domain/generators/FractionsGenerator.kt
- [x] com/siffermastare/domain/generators/TimeGenerator.kt
- [x] com/siffermastare/domain/generators/InformalTimeGenerator.kt
- [x] com/siffermastare/domain/generators/PhoneNumberGenerator.kt
- [x] com/siffermastare/domain/generators/TrickyPairsGenerator.kt
#### Logic Updates
- [x] com/siffermastare/domain/LessonSessionManager.kt
