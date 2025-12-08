# Siffermästare Product Requirements Document (PRD)

## Goals and Background Context

### Goals
* **User Engagement:** Achieve a high rate of user engagement (average 3+ sessions/week).
* **Educational Effectiveness:** Demonstrate measurable improvement in user proficiency (e.g., 50% increase in accuracy/speed in 2 weeks).
* **Module Completion:** Drive users to master specialized modules (e.g., 60% of users complete one specialized module).

### Background Context
This PRD defines the requirements for "Siffermästare," a native Android application for learning Swedish numbers. Existing language apps lack deep, practical, and trackable practice for numerical comprehension. Learners struggle with auditory comprehension of spoken numbers, especially specialized formats like dates, times, and the *personnummer*.

This product solves this by providing a focused "listen-and-type" learning loop, progressive complexity, and dedicated modules for real-world number formats. The MVP will be an on-device app validating the core mechanic and tracking user accuracy and speed.

### Change Log
| Date | Version | Description | Author |
| :--- | :--- | :--- | :--- |
| 2025-11-07 | 1.0 | Initial PRD draft based on Project Brief. | John (PM) |

## Requirements

### Functional
* **FR1:** The system shall provide a "listen-and-type" learning mode where a Swedish number is spoken via TTS, and the user must input the numerical digits.
* **FR2:** The system shall provide immediate visual feedback to the user, indicating if their submitted answer is correct or incorrect.
* **FR3:** The system shall generate and play audio for Swedish numbers using a high-quality Text-to-Speech (TTS) engine.
* **FR4:** The system shall offer a set of lessons for cardinal numbers, covering at minimum the ranges 0-20, 20-100, and 100-1000.
* **FR5:** The system shall offer at least one lesson for ordinal numbers (e.g., "första," "andra").
* **FR6:** The system shall offer at least one specialized module for practicing "Time" (e.g., "klockan är tre," "kvart i fem").
* **FR7:** The system shall track the user's accuracy (percentage correct) for each lesson.
* **FR8:** The system shall track the user's average response speed (time from audio end to correct submission) for each lesson.
* **FR9:** The system shall display the user's saved accuracy and response speed on a simple progress screen.

### Non Functional
* **NFR1 (Platform):** The application must be a native Android application.
* **NFR2 (Logic):** All application logic must be written in Kotlin.
* **NFR3 (UI):** The user interface must be built using Jetpack Compose.
* **NFR4 (Data Storage):** All user progress data for the MVP must be stored locally on the device using the Room database library.
* **NFR5 (No Backend):** The MVP must be 100% on-device and must not require any network connection, user accounts, or cloud backend.
* **NFR6 (Performance):** The application UI must be lightweight and responsive. User input and feedback must feel instantaneous (P95 < 200ms).
* **NFR7 (TTS Quality):** The TTS engine used must provide a high-quality, natural-sounding Swedish voice. (This will be validated in the initial technical spike).

## User Interface Design Goals

### Overall UX Vision
A clean, distraction-free interface that prioritizes speed and focus. The design should feel "native" to Android, utilizing Material Design 3 principles for familiarity, but stripped back to keep the user focused entirely on the audio and the input field.

### Key Interaction Paradigms
* **Lesson Definition:** A lesson is an atomic unit of 10 randomly generated questions. It must be completed in full to save progress.
* **Custom In-App Numpad:** A custom grid layout (non-system keyboard) containing digits 0-9, a "Backspace" button, and a primary action button ("Check").
* **Audio Control:** A prominent "Replay" button (Speaker icon) allowing unlimited repetitions of the current number.

### Core Screens and Views
1.  **Lesson Selector (Home):** A clear list of available modules (Cardinal, Ordinal, Time) with visual indicators of past accuracy/speed.
2.  **Active Lesson View:** The main learning screen. Minimalist. Contains: Audio controls, Answer Display Area, and the **Custom Numpad**.
3.  **Lesson Summary:** A post-session screen showing the result: Accuracy %, Avg Speed, and a "Retry" or "Next Lesson" button.

### Interaction Logic (The "Check" Flow)
* **Correct Answer:**
    1.  Visual Success cue (e.g., Input turns green, checkmark animation).
    2.  Brief delay (e.g., 500ms).
    3.  App automatically advances to the next question (Audio plays immediately).
* **Incorrect Answer:**
    1.  Visual Error cue (e.g., Input shakes, turns red).
    2.  User stays on the current question.
    3.  Input remains editable (user backspaces/fixes their answer) and presses "Check" again.

### Accessibility
* **Standard:** WCAG AA compliance where applicable.
* **Specific Consideration:** High contrast text for readability.

### Branding
* **Aesthetic:** "Swedish Minimalist." Clean lines, ample whitespace.
* **Colors:** Likely leveraging the Swedish flag palette (Blue #006AA7, Yellow #FECC00) as accents against a clean white/dark mode background.

### Target Device and Platforms
* **Mobile Only:** Specifically optimized for Android phones (portrait mode).

## Technical Assumptions

### Repository Structure
* **Single Repository (Monorepo):** The entire Android project will be contained in a single Git repository.

### Service Architecture
* **Client-Side Monolith:** The application is a standalone Android binary. All business logic and data storage reside locally.
* **No Backend:** There are no external API dependencies other than the device's built-in TTS service.

### Core Technology Stack
* **Language:** Kotlin (100%).
* **UI Framework:** Jetpack Compose (Material Design 3).
* **Local Database:** Room (SQLite) for storing user progress.
* **Audio:** Android `TextToSpeech` API.

### Testing Requirements
* **Unit Testing:** Mandatory for "Lesson Logic" (number generation, answer validation).
* **UI Testing:** Automated tests for Custom Numpad interactions.
* **Manual Testing:** "Ear-testing" the TTS output is required.

## Epic List

* **Epic 1: Foundation & Core Loop:** Establish Android project, integrate TTS, build Custom Numpad, and implement basic "Listen-and-Type" for range 0–9.
* **Epic 2: Lesson Logic & Progress Tracking:** Implement "10-Question Lesson" structure, "Incorrect -> Stay" flow, and local Room database.
* **Epic 3: Content Expansion (MVP Modules):** Implement logic for Cardinal numbers (0–1000), Ordinal numbers, and "Time" module.
* **Epic 4: UI Polish & Launch Readiness:** Apply "Swedish Minimalist" styling and prepare for release.

## Epic Details

### Epic 1: Foundation & Core Loop
**Goal:** Create a "Walking Skeleton" of the app. A user can open the app, hear a number (0-9), type it using a custom numpad, and get a log message saying if they were right.

* **Story 1.1: Project Initialization & Navigation**
    * **As a** Developer, **I want** to set up the Android project with Jetpack Compose and Navigation, **so that** we have a stable foundation.
    * **Acceptance Criteria:**
        1.  New Android Project created using Kotlin & Jetpack Compose.
        2.  Navigation Host set up with two empty screens: `HomeScreen` and `LessonScreen`.
        3.  "Start Debug Lesson" button on Home navigates to Lesson.

* **Story 1.2: TTS Integration (The Spike Implementation)**
    * **As a** User, **I want** to hear a Swedish number spoken when the screen loads, **so that** I can practice listening.
    * **Acceptance Criteria:**
        1.  `TextToSpeech` engine initialized with `Locale("sv", "SE")`.
        2.  Helper function `speak(text: String)` created.
        3.  On `LessonScreen` load, app randomly selects digit 0-9 and speaks it.
        4.  "Replay" button invokes `speak()` again.

* **Story 1.3: Custom Numpad UI**
    * **As a** User, **I want** to tap large number buttons inside the app, **so that** I can enter my answer quickly without a system keyboard.
    * **Acceptance Criteria:**
        1.  Create `Numpad` Composable.
        2.  Grid layout (1-3, 4-6, 7-9, 0 centered bottom).
        3.  Includes "Backspace" button.
        4.  Includes "Check" (Action) button.
        5.  Tapping numbers updates a state variable `currentInput`.

* **Story 1.4: Basic Game Loop (0-9)**
    * **As a** User, **I want** to be told if my input matches the spoken number, **so that** I know if I understood correctly.
    * **Acceptance Criteria:**
        1.  Logic to compare `currentInput` vs `targetNumber`.
        2.  On "Check" press:
            * If match: Show "Correct" Toast, clear input, pick new random 0-9 number, speak it.
            * If no match: Show "Try Again" Toast, keep input.

### Epic 2: Lesson Logic & Progress Tracking
**Goal:** Transform the infinite loop into a structured lesson with persistent score tracking.

* **Story 2.1: The 10-Question Lesson Structure**
    * **As a** User, **I want** the lesson to end after 10 questions, **so that** I have a defined session length.
    * **Acceptance Criteria:**
        1.  Track `questionCount` (1/10).
        2.  UI shows progress "Question X of 10".
        3.  After 10th correct answer, navigate to a new `SummaryScreen`.

* **Story 2.2: Visual Feedback States (The "Check" Flow)**
    * **As a** User, **I want** clear visual cues when I answer, **so that** I don't have to read toast messages.
    * **Acceptance Criteria:**
        1.  Implement specific Input Field colors: Neutral (typing), Green (Correct), Red (Error).
        2.  On Error: Input turns red, shakes, user stays on question.
        3.  On Correct: Input turns green, 500ms delay, then next question.

* **Story 2.3: Room Database Setup**
    * **As a** System, **I want** to store lesson results locally, **so that** data persists after app close.
    * **Acceptance Criteria:**
        1.  Define `LessonResult` entity (id, timestamp, moduleName, accuracy, avgSpeedMs).
        2.  Create Room Database and DAO.
        3.  Repository layer to insert results.

* **Story 2.4: Metric Calculation & Saving**
    * **As a** User, **I want** my speed and accuracy saved when I finish, **so that** I can track my progress.
    * **Acceptance Criteria:**
        1.  Timer logic: Start timer when audio finishes speaking. Stop when "Check" is pressed.
        2.  Calculate `Accuracy` (Correct / (Correct + Incorrect attempts)).
        3.  Calculate `AvgSpeed` (Total time / 10).
        4.  On Lesson Complete, save `LessonResult` to DB.
        5.  Display these stats on `SummaryScreen`.

### Epic 3: Content Expansion (MVP Modules)
**Goal:** Replace the "0-9" placeholder with real Swedish number logic.

* **Story 3.1: Cardinal Number Generator**
    * **As a** User, **I want** to practice numbers up to 1000, **so that** I can handle prices and years.
    * **Acceptance Criteria:**
        1.  Create `NumberGenerator` interface.
        2.  Implement `CardinalGenerator`.
        3.  Logic to convert Int to Swedish text string (e.g., 23 -> "tjugotre").
        4.  Lesson Selector allows choosing range: 0-20, 0-100, 0-1000.

* **Story 3.2: Ordinal Number Generator**
    * **As a** User, **I want** to practice ordinal numbers, **so that** I know the difference between "three" and "third".
    * **Acceptance Criteria:**
        1.  Implement `OrdinalGenerator`.
        2.  Logic to convert Int to Swedish ordinal string (e.g., 3 -> "tredje").
        3.  TTS speaks the word. User types the digit.

* **Story 3.3: Specialized Module: Time**
    * **As a** User, **I want** to practice telling time, **so that** I can make appointments.
    * **Acceptance Criteria:**
        1.  Implement `TimeGenerator`.
        2.  Generate random HH:MM times (focus on 5-minute intervals).
        3.  Convert to Swedish spoken format (e.g., 14:15 -> "Kvart över två").
        4.  User input format: 4 digits (e.g., 0315 or 1415).

### Epic 4: UI Polish & Launch Readiness
**Goal:** Make it look professional and ready for the store.

* **Story 4.1: Apply "Swedish Minimalist" Theme**
    * **As a** User, **I want** a beautiful interface, **so that** I enjoy using the app.
    * **Acceptance Criteria:**
        1.  Apply Blue/Yellow/White color palette.
        2.  Style Numpad and Progress Bar.

* **Story 4.2: Home Screen Dashboard**
    * **As a** User, **I want** to see my history on the home screen, **so that** I know what to practice.
    * **Acceptance Criteria:**
        1.  Query Room DB for last 5 sessions.
        2.  Display simple list on `HomeScreen` showing recent Accuracy/Speed.
