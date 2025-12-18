# Retrospective: Epic 2 - Core Lesson Gameplay

**Status**: Completed
**Date**: 2025-12-18

## Summary
Epic 2 focused on transforming the basic prototype into a structured learning experience. We successfully implemented the 10-question lesson format, visual feedback systems, local data persistence, and performance tracking.

## Completed Stories
- **2.1: The 10-Question Lesson Structure**: Implemented the core loop, question counter, and navigation to a summary screen.
- **2.2: Visual Feedback States**: Replaced simple text with a robust state machine (`AnswerState`) handling Neutral, Correct (Green/Delay), and Incorrect (Red/Shake/Replay) states.
- **2.3: Room Database Setup**: Established the offline data layer using Room, ensuring robust data storage for lesson results.
- **2.4: Metric Calculation & Saving**: Implemented logic to track accuracy and speed, save these metrics to the DB, and display them to the user.

## What Went Well
- **Architecture**: The MVVM pattern with a Repository layer has proven robust. Isolate ViewModel logic made unit testing straightforward.
- **State Management**: Using `LessonUiState` data class with `StateFlow` simplified the UI code in `LessonScreen`.
- **Testing**: We established a good pattern of verifying ViewModel logic with Unit Tests (using `MainDispatcherRule` and `FakeRepository`).

## Challenges & Solutions
- **Build Configuration (Room/JDK)**: We encountered significant build issues due to incompatibility between JDK 22 and the `kapt` plugin (used for Room).
    - *Solution*: Migrated to **KSP (Kotlin Symbol Processing)** and updated `build.gradle.kts` to explicitly target **Java 17**. This modernized the build stack and resolved the errors.
- **Animation Timing**: Coordinating the shake animation, color change, and TTS replay required careful management of coroutine delays in the ViewModel to ensure a smooth user experience.

## Action Items for Next Epic
- **Maintain KSP**: Ensure future libraries (like Hilt if we add it) are configured with KSP.
- **Emulator**: We still lack a configured emulator for Instrumentation tests. As logic gets more complex, setting this up would be beneficial.

## Conclusion
The application now has a fully functional "Game Loop" with persistence. The foundation is solid for adding generated content (Epic 3).
