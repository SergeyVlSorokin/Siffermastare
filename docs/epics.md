---
stepsCompleted: [1, 2, 3, 4]
inputDocuments: ['docs/prd.md', 'docs/architecture.md', 'docs/epics-template.md']
---

# Siffermastare - Epic Breakdown

## Overview

This document provides the complete epic and story breakdown for Siffermastare, decomposing the requirements from the PRD, UX Design if it exists, and Architecture requirements into implementable stories.

## Requirements Inventory

### Functional Requirements

FR1: System can generate **Informal Time** prompts using Swedish logic (e.g., "kvart i...", "halv...").
FR2: System can generate **Tricky Pair** discrimination tasks (e.g., alternating between 13/30, 14/40).
FR3: System can generate **Phone Number** sequences with distinct rhythmic grouping.
FR4: System can generate **Fractions** and speak them using correct Swedish nomenclature.
FR5: System can generate **Decimals** and speak them using "komma" format.
FR6: User can **Skip/Give Up** on a question after repeated failures (limit set to 3) to see the correct answer.
FR7: User can trigger a **Slow Replay** of the current prompt (temporary rate reduction for one utterance).
FR8: User receives immediate visual feedback (Correct/Incorrect) after submitting an answer.
FR9: User must **Confirm Exit** before leaving an active lesson.
FR10: System accepts **Flexible Time Input** (supporting both "9:30" and "09:30" formats).
FR11: System calculates and persists **Accuracy %** per module.
FR12: System calculates and persists **Average Response Time** per module.
FR13: System stores all data in a local encrypted database (No Account required).
FR14: System functions with **100% Offline Availability** for all core features.
FR15: System dynamically loads content generators via a plugin/factory pattern.

### NonFunctional Requirements

NFR1: TTS playback must initiate within **200ms** of user input to maintain "conversational flow".
NFR2: App cold start to "Ready for Input" must be under **1.5 seconds**.
NFR3: All text/UI elements must meet **WCAG AA** contrast standards.
NFR4: Touch targets must be at least **48x48dp**.
NFR5: App must remain fully functional with **0% packet loss** tolerance.

### Additional Requirements

- **Architecture:** Implement new generators (Informal Time, Tricky Pairs, Phone) using the existing `NumberGenerator` interface Strategy Pattern.
- **Architecture:** Maintain strict MVVM separation (Generator -> UseCase -> ViewModel -> UI).
- **Architecture:** No cloud infrastructure or backend services allowed (Offline First).
- **Architecture:** Use `fakeTTS` for integration testing of the new generators.

### FR Coverage Map

### FR Coverage Map

FR1 (Informal Time): Epic 6 - Content Factory
FR2 (Tricky Pairs): Epic 6 - Content Factory
FR3 (Phone Numbers): Epic 6 - Content Factory
FR4 (Fractions): Epic 6 - Content Factory
FR5 (Decimals): Epic 6 - Content Factory
FR6 (Skip/Give Up): Epic 5 - Learning Loop
FR7 (Slow Replay): Epic 5 - Learning Loop
FR8 (Feedback): Epic 5 - Learning Loop
FR9 (Confirm Exit): Epic 5 - Learning Loop
FR10 (Flex Time): Epic 5 - Learning Loop
FR11 (Accuracy Stats): Epic 5 - Learning Loop (Stats Foundation)
FR12 (Avg Time Stats): Epic 5 - Learning Loop (Stats Foundation)
FR13 (Local DB): Both (Foundation exists, Epic 5 enhances)
FR14 (Offline): Both (Foundation exists)
FR15 (Generator Factory): Epic 6 - Content Factory

## Epic List

### Epic 5: Learning Loop Refinement & Safety

**Goal:** Fix user frustrations in the current loop (getting stuck, speaking too fast, input formatting) to improve engagement and retention.
**FRs covered:** FR6, FR7, FR8, FR9, FR10, FR11, FR12, NFR1, NFR3, NFR4.

### Story 5.1: Slow Replay

As a learner,
I want to hear the number spoken slowly,
So that I can distinguish rapid sounds like "tjugo" vs "tio" when I am confused.

**Acceptance Criteria:**

**Given** I am in an active lesson
**When** I look at the screen
**Then** I should see a "Turtle" icon button near the replay button
**When** I tap the Turtle button
**Then** The current number should be spoken again via TTS
**And** The speech rate should be set to 0.7x (or optimal slow speed) for this single utterance
**And** Subsequent questions should return to normal speed automatically

### Story 5.2: "Give Up" Mechanism

As a learner,
I want to be able to "give up" on a question I clearly don't know,
So that I don't get stuck in an infinite loop and can learn from seeing the correct answer.

**Acceptance Criteria:**

**Given** I am in an active lesson
**When** I have entered an incorrect answer 3 times in a row for the same question
**Then** A "Ge Upp" (Give Up) button should appear below the input field
**When** I click "Ge Upp"
**Then** The correct answer should be displayed (e.g., "Svaret var: 14")
**And** The lesson should proceed to the next question
**And** The question should be marked as "Failed" (Score: 0) for my accuracy stats

### Story 5.3: Visual Time Separator

As a user practicing Time modules,
I want to see a visual separator (:) in the input field,
So that I understand the format expected and can read my input clearly.

**Acceptance Criteria:**

**Given** I am in a "Time" module lesson
**Then** The input field should display a colon (:) separator between hours and minutes
**When** I type numbers
**Then** They should fill in naturally around the separator

### Story 5.4: Safe Exit

As a user on the go (bus/train),
I want to be asked for confirmation if I accidentally try to close the lesson,
So that I don't lose my progress for the current session.

**Acceptance Criteria:**

**Given** I am in the middle of a lesson (e.g., Question 5/10)
**When** I press the system Back button OR the app's top-left Back arrow
**Then** A Dialog should appear asking "Avsluta lektionen?" (Quit lesson?)
**When** I confirm "Yes"
**Then** The lesson ends and I return to the dashboard (no stats saved)
**When** I select "No"
**Then** The dialog closes and I remain on the current question

### Story 5.5: Flexible Time Validation

As a user,
I want the app to accept "9:30" as a correct answer for "09:30",
So that I am not penalized for mathematical correctness vs strict formatting.

**Acceptance Criteria:**

**Given** The target answer is "09:30"
**When** I submit "9:30"
**Then** It should be marked as **Correct**
**Given** The target answer is "09:30"
**When** I submit "09:30"
**Then** It should be marked as **Correct**

<!-- Repeat for each story (M = 1, 2, 3...) within epic N -->

### Epic 6: Content Factory Expansion
**Goal:** Leverage the refined loop to introduce complex new generator types for real-world mastery.
**FRs covered:** FR1, FR2, FR3, FR4, FR5, FR15.

### Story 6.1: Informal Time Generator

As a learner,
I want to practice "Informal Time" (e.g., "kvart i tre"),
So that I can understand how Swedes actually tell time in daily life.

**Acceptance Criteria:**

**Given** I select the "Tid (Informell)" module
**When** The generator creates a question for 14:45
**Then** It should utilize the `TimeGenerator` logic to output "Kvart i tre" for TTS
**And** The target answer should remain "14:45"
**And** It should cover :15 (kvart över), :30 (halv), :45 (kvart i) and :00 scenarios

### Story 6.2: Tricky Pairs Generator

As a learner,
I want a module that specifically alternates between "13" and "30" (and similar pairs),
So that I can train my ear to distinguish the subtle stress difference between "-ton" and "-tio".

**Acceptance Criteria:**

**Given** I select the "Tricky Pairs" module
**When** A lesson is generated
**Then** It should produce pairs like 13/30, 14/40, 15/50, 16/60, 17/70, 18/80, 19/90 randomly
**And** The TTS should be clear enough to distinguish them (relying on standard Swedish stress patterns)

### Story 6.3: Phone Number Generator

As an expat,
I want to practice listening to 10-digit phone numbers,
So that I can write them down correctly when someone dictates them to me.

**Acceptance Criteria:**

**Given** I select the "Telefonnummer" module
**When** A question is generated
**Then** It should produce a 10-digit string (e.g., "0701234567")
**And** The TTS should speak it in rhythmic groups (e.g., "Noll sju noll... ett två tre... fyrtiofem... sextiosju")
**And** The input field should allow typing the full string

### Story 6.4: Fractions Generator

As a user,
I want to practice listening to fractions,
So that I can distinguish "en halv" (1/2) from "en helt" (1) and other common math terms.

**Acceptance Criteria:**

**Given** I select the "Bråk" (Fractions) module
**When** A question is generated (e.g., 1/2, 3/4, 1/3)
**Then** The TTS should read it naturally ("en halv", "tre fjärdedelar")
**And** The input should expect standard fraction notation (e.g., "1/2")

### Story 6.5: Decimals Generator

As a user,
I want to practice decimal numbers,
So that I can handle prices and measurements (e.g., "2,5").

**Acceptance Criteria:**

**Given** I select the "Decimaler" module
**When** A question is generated (e.g., 0.5, 12.50)
**Then** The TTS should read "komma" explicitly ("noll komma fem")
**And** The input should accept comma or dot as separator


<!-- Repeat for each epic in epics_list (N = 1, 2, 3...) -->


