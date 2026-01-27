# Story 7.1: Knowledge Data Model

Status: done

## Story

As a system,
I need to persist the probability state for each "Atomic Number",
So that I can remember what the user knows between sessions.

## Acceptance Criteria

### AC1: Database Table
**Given** The app starts up
**Then** A Room database table `atom_states` should exist
**And** It should store `atom_id` (string), `alpha` (float), `beta` (float), and `last_updated` (timestamp)

### AC2: Default Priors
**Given** A fresh install
**When** I query the state for Atom "5"
**Then** It should return the default prior ($\alpha=1.0, \beta=1.0$)

## Developer Context

### Technical Requirements

- **Database**: Use Room Database (SQLite).
- **Entities**: Create `AtomState` entity annotated with `@Entity`.
- **DAO**: Create `AtomStateDao` with methods to `insert`, `update`, and `query` states.
- **Repository**: Implement `KnowledgeRepository` to abstract the data source.
- **Dependency Injection**: Use Hilt to inject the Repository and Database.
- **Concurrency**: Use Coroutines/Flow for DB access.

### Architecture Compliance

- **Layer**: Data Layer (Persistence).
- **Pattern**: Repository Pattern.
- **Constraint**: Offline-first (Local DB only).
- **Constraint**: Immutable Domain Models where possible (map Entity to Domain model if needed, or use Entity if simple). *Note: Architecture mentions Domain Models vs Persistence Models. Ensure separation if complexity warrants it, but for simple atomic states, direct mapping might be acceptable if justifiable. However, architecture prescribes `LessonResultEntity` vs `LessonSession`. Likely `AtomState` (Entity) and `AtomKnowledge` (Domain)? For this story, just persistence is requested, but check defined patterns.*

### File Structure Requirements

- `app/src/main/java/com/siffermastare/data/database/AtomState.kt`
- `app/src/main/java/com/siffermastare/data/database/AtomStateDao.kt`
- `app/src/main/java/com/siffermastare/data/repository/KnowledgeRepository.kt`
- Update `app/src/main/java/com/siffermastare/data/database/AppDatabase.kt` to include the new entity.

### Testing Requirements

- **Unit Tests**:
  - Test `AtomStateDao` (using `Room.inMemoryDatabaseBuilder` for instrumentation test or Robolectric for unit test).
  - Test `KnowledgeRepository` (mocking the DAO).
- **Coverage**: Ensure default priors logic is tested.

## Dev Agent Record

### Context Reference

- Architecture: `docs/architecture.md`
- Epics: `docs/epics.md`

### File List

- [x] com/siffermastare/data/database/AtomState.kt
- [x] com/siffermastare/data/database/AtomStateDao.kt
- [x] com/siffermastare/data/database/AppDatabase.kt
- [x] com/siffermastare/data/repository/KnowledgeRepository.kt
