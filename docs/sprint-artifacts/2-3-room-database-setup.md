# Story 2.3: Room Database Setup

Status: done

## Story

As a System,
I want to store lesson results locally,
so that data persists after app close.

## Acceptance Criteria

1.  Define `LessonResult` entity (id, timestamp, moduleName, accuracy, avgSpeedMs).
2.  Create Room Database and DAO.
3.  Repository layer to insert results.

## Tasks / Subtasks

- [ ] Task 1: Define Entity (AC: 1)
  - [ ] Create `LessonResult.kt` data class
  - [ ] Annotate with `@Entity`
  - [ ] Fields: `id` (PK, auto), `timestamp`, `moduleName`, `accuracy` (Float), `avgSpeedMs` (Long)

- [ ] Task 2: Create DAO (AC: 2)
  - [ ] Create `LessonResultDao` interface
  - [ ] Method: `insert(result: LessonResult)`
  - [ ] Method: `getAll(): Flow<List<LessonResult>>` (for future history story)

- [ ] Task 3: Create Database (AC: 2)
  - [ ] Create `AppDatabase` abstract class extending `RoomDatabase`
  - [ ] Configure TypeConverters if needed (likely not for basic types)

- [ ] Task 4: Create Repository (AC: 3)
  - [ ] Create `LessonRepository` interface and implementation
  - [ ] Method: `saveResult(result: LessonResult)`

- [ ] Task 5: Dependency Injection Setup
  - [ ] Instantiate Database/Dao/Repo in `App` or DI module
  - [ ] Ensure `LessonViewModel` can access Repository

## Dev Notes

### Architecture Compliance
**Layer Separation:** [Source: docs/architecture.md#Data Layer]
- Entities in `data/local/entities/`
- DAO in `data/local/dao/`
- Database in `data/local/`
- Repository in `data/repository/`

### Technical Requirements
- Use **Room** library (add dependencies if missing in `libs.versions.toml`).
- Use **Coroutines/Flow** for async access.

### File Structure Requirements
```
app/src/main/java/com/siffermastare/
└── data/
    ├── local/
    │   ├── AppDatabase.kt
    │   ├── dao/
    │   │   └── LessonResultDao.kt
    │   └── entities/
    │       └── LessonResult.kt
    └── repository/
        ├── LessonRepository.kt
        └── LessonRepositoryImpl.kt
```
