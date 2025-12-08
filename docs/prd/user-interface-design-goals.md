# User Interface Design Goals

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
