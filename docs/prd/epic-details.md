# Epic Details

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

### Epic 6: Advanced Content (Nuances & Real World)
**Goal:** Move beyond basic mechanics to difficult/common Swedish specific number patterns.

* **Story 6.1: Informal Time Generator (Analogue)**
    * **As a** User, **I want** to practice "analogue" time speaking (e.g., "kvart i tre"), **so that** I can understand how Swedes actually tell time.
    * **Acceptance Criteria:**
        1.  New `InformalTimeGenerator` implementing existing interface.
        2.  Logic for: "över", "i", "halv", "kvart över", "kvart i", "fem i halv", "fem över halv".
        3.  TTS speaks informal string (e.g., "fem i halv tre").
        4.  User must enter digital format (e.g., 0225 or 1425).

* **Story 6.2: Tricky Pairs Generator**
    * **As a** User, **I want** a dedicated mode for easily confused numbers, **so that** I can fine-tune my listening.
    * **Acceptance Criteria:**
        1.  Generator that exclusively produces pairs like: 7/20 (sju/tjugo), 6/60 (sex/sextio), 13/30 (tretton/trettio).
        2.  Higher frequency of these problematic numbers compared to random noise.

* **Story 6.3: Phone Number Generator**
    * **As a** User, **I want** to practice listening to long sequences of digits, **so that** I can write down phone numbers.
    * **Acceptance Criteria:**
        1.  Generate standard Swedish mobile format (07x-xxx xx xx).
        2.  TTS speaks them in natural rhythm (groups).
        3.  Input field adapts to allow longer input (10 digits).
        4.  Validation checks the full sequence.

* **Story 6.4: Fractions Generator**
    * **As a** User, **I want** to understand spoken fractions, **so that** I can follow recipes or measurements.
    * **Acceptance Criteria:**
        1.  Update Numpad to include a "/" button (or reuse an empty slot).
        2.  Generate common fractions: 1/2, 1/3, 1/4, 3/4.
        3.  Speak: "en halv", "en tredjedel", "en fjärdedel", "tre fjärdedelar".
        4.  User types "1", "/", "2".

* **Story 6.5: Decimals Generator**
    * **As a** User, **I want** to practice numbers with decimal points, **so that** I can handle prices and precise measurements.
    * **Acceptance Criteria:**
        1.  Update Numpad to include a "," button.
        2.  Speak: "tre komma fem" (3,5) or prices "tre och femtio" (3,50).
        3.  User types "3", ",", "5".
