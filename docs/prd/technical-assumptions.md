# Technical Assumptions

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
