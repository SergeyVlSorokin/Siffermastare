# Project Brief: Siffermästare (Swedish Number Learner)

### 1. Executive Summary

This project brief outlines the development of a specialized Android application designed to help users learn and master Swedish numbers. The core functionality centers on interactive audio-based learning, where the app speaks a randomly selected number in Swedish, and the user must input the correct numerical answer. The system will track user progress based on correctness, speed, and task complexity, offering tailored lessons of varying difficulty (e.g., number ranges, ordinal vs. cardinal) and specialized formats (e.g., phone numbers, dates, times, and personal numbers).

### 2. Problem Statement

Learning numbers in a new language, particularly Swedish, presents unique challenges beyond simple memorization. While basic counting (1-10) is straightforward, mastering larger numbers, complex combinations, ordinal numbers (first, second), and specialized formats used in daily life is a significant hurdle.

Learners often struggle with:
* **Auditory Comprehension:** Difficulty understanding spoken numbers, especially when said quickly or in context.
* **Specialized Formats:** Swedish phone numbers, dates, times, and the *personnummer* (personal identification number) have distinct structures and pronunciations that are not typically covered in standard language apps.
* **Lack of Contextual Practice:** Most learning tools focus on abstract drills, failing to prepare learners for real-world scenarios like hearing a price, a time, or a phone number in conversation.
* **Stagnant Learning:** Existing tools often lack sophisticated progress tracking, leaving users unsure of their actual proficiency or where they need to improve.

Existing solutions are often too generic, lumping numbers in with general vocabulary and failing to provide the deep, specialized, and tracked practice required for true mastery.

### 3. Proposed Solution

We propose a specialized, gamified Android application focused exclusively on mastering Swedish numbers through interactive auditory practice.

The core solution is a "listen-and-type" learning loop:
1.  The app will use a high-quality text-to-speech (TTS) engine to generate and speak a Swedish number.
2.  The user will be prompted to type the corresponding digits into the app.
3.  The app provides instant feedback on whether the answer is correct or incorrect.

This core loop will be enhanced by a comprehensive learning system:
* **Progressive Complexity:** Users can select lessons that scale in difficulty, from basic ranges (e.g., 0-20) to more complex ones (e.g., 100-1,000,000) and distinguishing between cardinal ("one") and ordinal ("first") numbers.
* **Specialized Modules:** Dedicated training modules for real-world formats and complex types, including:
    * Phone numbers
    * Dates
    * Times
    * Personal numbers (*personnummer*)
    * **Fractions** (e.g., "en halv," "en tredjedel")
    * **Decimals** (e.g., "noll komma fem")
* **Advanced Progress Tracking:** The system will move beyond simple right/wrong answers to track metrics like response speed and accuracy over time. This data will feed into a user-facing dashboard, highlighting strengths, weaknesses, and a "mastery level" for different number types.

This focused approach ensures users build robust auditory comprehension and practical skills for real-world situations, providing a clear path from basic counting to complete numerical fluency.

### 4. Target Users

#### Primary User Segment: The Language Learner

* **Who they are:** Students actively learning Swedish, likely at a beginner-to-intermediate (A2-B1) level. They are using other tools (like Duolingo, Babbel, or traditional classes) but find these resources lack depth in practical, spoken number comprehension.
* **Behaviors:** They study regularly and are motivated to achieve fluency. They get frustrated when they can read numbers but can't understand them in a real conversation.
* **Needs & Pains:**
    * Pain: "I can order 'en kaffe' but I freeze when the cashier says 'femtioåtta kronor'."
    * Pain: "I don't understand the difference between 'första' (first) and 'ett' (one) in context."
    * Need: A focused tool to drill this one, difficult topic until it becomes automatic.

#### Secondary User Segment: The Expat / New Resident

* **Who they are:** Individuals who have recently moved to Sweden for work or personal reasons. They may be at any language level (from total beginner to advanced), but they face an immediate, practical need to navigate Swedish bureaucracy and daily life.
* **Behaviors:** They are highly motivated by necessity. They need to be 100% accurate when hearing or giving critical numbers.
* **Needs & Pains:**
    * Pain: High-stakes anxiety about mishearing a *personnummer* (personal ID number), a phone number, or a booking reference.
    * Pain: Difficulty understanding times and dates when making appointments.
    * Need: A tool that specifically drills the high-stakes "nominal" numbers (dates, times, *personnummer*) that are essential for living in Sweden.

### 5. Goals & Success Metrics

#### Business Objectives

* **User Engagement:** Achieve a high rate of user engagement, defined by users completing an average of 3+ learning sessions per week.
* **Educational Effectiveness:** Demonstrate measurable improvement in user proficiency, with a goal of 80% of active users showing a 50% increase in accuracy and speed within their first two weeks.
* **Module Completion:** Drive users to master all aspects of Swedish numbers, with a target of 60% of active users completing at least one "Specialized Module" (e.g., Dates, *personnummer*).

#### User Success Metrics

We will track the following to measure individual user success and proficiency:

* **Accuracy Rate:** Percentage of correct answers, tracked per lesson and per number category (e.g., "Accuracy on 100-1000", "Accuracy on Dates").
* **Response Speed:** Average time (in seconds) from when the audio finishes to when the user submits a correct answer. Improvement in this metric indicates automaticity.
* **Mastery Level:** A calculated score per module, based on a combination of sustained accuracy and speed, indicating the user is ready to move to a harder level.

#### Key Performance Indicators (KPIs)

* **Daily Active Users (DAU):** To measure daily engagement.
* **Session Completion Rate:** The percentage of users who start a lesson and complete it.
* **Specialized Module Adoption:** The percentage of new users who try at least one "Specialized Module" within their first week.

### 6. MVP Scope

#### Core Features (Must Have for MVP)

* **1. Core Learning Loop:** The primary "listen-and-type" functionality.
* **2. Audio Generation:** Integration of a high-quality Swedish Text-to-Speech (TTS) engine.
* **3. Basic Lessons:** A small set of progressive lessons for cardinal numbers (e.g., 0-20, 20-100, 100-1000).
* **4. Ordinal Number Lesson:** At least one lesson to teach ordinal numbers ("first", "second", etc.) as this is a key confusion point.
* **5. Specialized Module (Proof of Concept):** *One* specialized module: **Time**.
* **6. Basic Local Progress Tracking:** The app must track **accuracy** (correct/incorrect) and **response speed** for each lesson and store this progress *locally* on the user's device.

#### Out of Scope for MVP

To ensure we can launch quickly, the following features will be delayed until a future version:

* **No User Accounts:** All progress will be saved on the device only. No cloud sync or login.
* **Limited Specialized Modules:** We will launch with only one (Time). The other modules (dates, phone numbers, *personnummer*, fractions, decimals) will be added in updates.
* **No Calculated "Mastery Level":** The MVP will track raw accuracy and speed, but will not yet calculate a combined "Mastery Level" or aggregate score.
* **No Gamification:** Features like badges, leaderboards, or score multipliers will be added later.

#### MVP Success Criteria

The MVP will be considered a success if a user can download the app, complete a 10-question lesson in a cardinal module (e.g., "0-100") and the "Time" module, and see their **accuracy score and average response time** saved on a simple progress screen.

### 7. Post-MVP Vision

#### Phase 2 Features

Following a successful MVP launch, our immediate priority will be to build out the full suite of specialized learning modules:

* **Module Expansion:**
    * Dates
    * Phone Numbers
    * Personal Numbers (*personnummer*)
    * Fractions
    * Decimals
    * Currency / Measures
* **Advanced Progress Tracking:** Implement the "Mastery Level" calculation, which combines accuracy and speed into a single, motivating score for each module.
* **User Accounts & Cloud Sync:** Introduce user accounts to allow progress to be saved and synced across devices.

#### Long-term Vision

* **Gamification:** Introduce a full gamification layer, including badges, achievements, daily streaks, and leaderboards to drive long-term engagement.
* **Aggregate Fluency Score:** Develop a single, aggregate "Swedish Number Fluency" score that summarizes a user's mastery across all categories.
* **Voice Input:** Explore adding a "speak-and-verify" mode, where the user speaks the number in Swedish, using voice recognition to check their pronunciation and accuracy.

#### Expansion Opportunities

* **"Real-World Scenarios":** A mode that simulates a real conversation (e.g., a cashier, a train station announcement) and requires the user to pick out the number.
* **Other Languages:** The core framework of this app could be adapted to teach difficult number systems in other languages (e.g., French, Danish, Japanese).

### 8. Technical Considerations

#### Platform Requirements

* **Target Platforms:** The primary target platform is **Android**. The app should be built as a native Android application.
* **Performance Requirements:** The app must be lightweight and fast. Audio playback (TTS) must be near-instantaneous, and the UI must respond immediately to user input to ensure the response speed tracking is accurate.

#### Technology Preferences

* **Frontend (UI):** **Jetpack Compose** is the preferred modern toolkit for building the UI.
* **Backend (Logic):** **Kotlin** is the preferred language for all application logic.
* **Database:** **Room** (part of Android Jetpack) is preferred for storing user progress locally on the device.
* **Hosting/Infrastructure:** Not applicable for the MVP, as all data and logic will be 100% on-device.

#### Architecture Considerations

* **Integration Requirements:** The core technical dependency is a high-quality **Swedish Text-to-Speech (TTS) engine**. We must research and select a TTS service that provides a natural-sounding Swedish voice (e.g., Google's TTS, Samsung's TTS, or a third-party embedded engine).
* **Security/Compliance:** As no user data is being transmitted or stored in the cloud for the MVP, security requirements are minimal. We must ensure any local data storage is private to the app.

### 9. Constraints & Assumptions

#### Constraints

* **MVP is On-Device Only:** The initial MVP release *must not* require any backend server or cloud database. All logic and user progress data must be handled 100% on the user's local device.
* **Native Android Only:** This project is constrained to building a native Android application. Cross-platform solutions (like React Native, Flutter) or web-based apps are not in scope.

#### Key Assumptions

* **TTS Quality is Sufficient:** We are assuming that a high-quality, natural-sounding Swedish Text-to-Speech (TTS) engine is readily available and accessible on the Android platform (e.g., via Google's built-in TTS) and that it can correctly pronounce all required number formats.
* **Learning Loop is Engaging:** We are assuming that the core "listen-and-type" mechanic is sufficiently engaging on its own for an MVP, without the need for advanced gamification.

### 10. Risks & Open Questions

#### Key Risks

* **1. TTS Quality Risk (High):** The entire app's success depends on the quality of the Swedish Text-to-Speech (TTS) engine. If the built-in Android TTS for Swedish is unnatural, robotic, or inaccurate (especially with complex numbers, ordinals, or times), the core learning value is destroyed.
    * **Mitigation:** A "Spike" (a small, quick technical investigation) must be the first development task. We must test the TTS engine with a list of difficult numbers *before* building any UI.
* **2. Engagement Risk (Medium):** The simple "listen-and-type" loop, without gamification, may not be engaging enough to retain users, even if it is educational.
    * **Mitigation:** We must get the MVP in front of real users as quickly as possible to measure session completion and retention. If retention is low, we must fast-track gamification from the Post-MVP vision.
* **3. Number Generation Complexity (Low):** Generating random, valid, and *sensible* numbers for all modules (especially dates, times, and *personnummer*) might be logically complex.
    * **Mitigation:** The logic for each specialized module must be carefully planned and tested to ensure it produces valid and varied examples.

#### Open Questions

* **TTS Engine Selection:** Which specific TTS engine will we use? Do we need to bundle one, or can we rely on the user's built-in Android TTS?
* **"Time" Module Logic:** What constitutes a "time"? Are we doing 12-hour or 24-hour format? Are we saying "kvart i tre" (quarter to three) or just "två och fyrtiofem" (2:45)?
* **User Feedback Channel:** How will we collect feedback from our MVP users if there are no user accounts and no server?

### 11. Appendices

#### A. Research Summary

N/A - No formal market research or competitive analysis was conducted for this initial brief.

#### B. Stakeholder Input

N/A - All input has been gathered directly for this document.

#### C. References

* (To be added - e.g., links to selected TTS engine documentation, Android development guidelines for Jetpack Compose, etc.)
