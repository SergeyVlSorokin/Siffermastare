# Requirements

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
