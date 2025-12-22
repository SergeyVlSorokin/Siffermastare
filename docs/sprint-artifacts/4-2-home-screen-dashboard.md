# Story 4.2: Home Screen Dashboard

Status: Done

## Story

As a Learner,
I want to see my progress (total lessons, average accuracy) on the home screen,
so that I stay motivated and track my improvement.

## Acceptance Criteria

1.  **Dashboard UI**: Display a summary section at the top of the Home Screen (below header).
    -   Show: **Total Lessons Completed**.
    -   Show: **Current Streak** (Days in a row).
2.  **Data Persistence**: Fetch real data from `LessonRepository`.
    -   *Streak Logic*: Calculated based on `timestamp` of completed lessons. Consecutive days ending today or yesterday.
3.  **Visuals**: Use the new "Swedish Minimalist" theme (Cards/Stats).
4.  **Loading State**: Handle initial loading gracefully.

## Backend/Logic Changes
- Add query to `LessonResultDao`: fetch all timestamps (descending).
- Implement `StreakCalculator` or logic in ViewModel/UseCase.

## Tasks
- [x] Task 1: Data Layer Updates (AC: 2)
    - [x] Add `getLessonCount()` and `getAllTimestamps()` to DAO/Repository.
- [x] Task 2: ViewModel Implementation (AC: 2)
    - [x] Create `HomeViewModel`.
    - [x] Implement Streak Calculation Logic (Kotlin).
    - [x] Expose `HomeUiState` (lessons, streak).
- [x] Task 3: UI Implementation (AC: 1, 3, 4)
    - [x] Create `DashboardStatCard` composable.
    - [x] Integrate into `HomeScreen`.
- [x] Task 4: Code Review Fixes
    - [x] Refactor `HomeScreen` to use `collectAsStateWithLifecycle`
    - [x] Extract hardcoded strings
    - [x] Correct documentation

## Dev Agent Record
### File List
- `app/src/main/java/com/siffermastare/data/database/LessonDao.kt`
- `app/src/main/java/com/siffermastare/data/repository/LessonRepository.kt`
- `app/src/main/java/com/siffermastare/ui/home/HomeViewModel.kt`
- `app/src/main/java/com/siffermastare/ui/home/HomeScreen.kt`
