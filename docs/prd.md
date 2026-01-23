---
stepsCompleted: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11]
inputDocuments:
  - 'docs/project brief.md'
  - 'docs/architecture.md'
  - 'docs/project_context.md'
  - 'docs/front-end-spec.md'
  - 'docs/prd/requirements.md'
  - 'docs/prd/epic-details.md'
  - 'docs/prd/goals-and-background-context.md'
  - 'docs/prd/user-interface-design-goals.md'
  - 'docs/prd/technical-assumptions.md'
documentCounts:
  briefs: 1
  research: 0
  brainstorming: 0
  projectDocs: 9
workflowType: 'prd'
lastStep: 11
project_name: 'Siffermastare'
user_name: 'Sergei'
date: '2025-12-23'
---

# Product Requirements Document - Siffermastare

**Author:** Sergei
**Date:** 2025-12-23

## Executive Summary

Siffermästare is a specialized native Android application designed to bridge the gap between basic counting and true numerical fluency in Swedish. By moving beyond the MVP's core loop, the product roadmap now focuses on **Deep Content Expansion**. The goal is to provide learners and residents with a focused, offline-first tool to master high-stakes numerical formats—such as dates, times, and personal identification numbers (*personnummer*)—through a rigorous auditory-first approach that generic language apps neglect.

### What Makes This Special

Unlike broad language learning platforms that treat numbers as a minor vocabulary subset, Siffermästare treats numerical fluency as a complex skill requiring dedicated practice. Its value proposition lies in **Specialized Depth**: offering isolated, intensive training on the specific formats (like "14:45" vs "kvart i tre" or the speed of a spoken *personnummer*) that cause anxiety in real-world interactions. This "Content First" strategy ensures users build confidence in the exact scenarios where they currently fail.

## Project Classification

**Technical Type:** mobile_app (Native Android, Offline-first)
**Domain:** edtech (Language Learning)
**Complexity:** medium
**Project Context:** Brownfield - extending existing MVP system

## Success Criteria

### User Success
*   **Accuracy:** Users achieve consistently high accuracy (>80%) in identified problem areas (e.g., distinguishing "tyio" vs "tjugo").
*   **Speed:** Average response time decreases over repeated sessions, indicating automaticity.
*   **Confidence:** Users voluntarily attempt "Specialized Modules" (Time, Dates) after mastering basics.

### Business Success
*   **Engagement:** Active users complete average of 3+ learning sessions per week.
*   **Module Depth:** 60% of active users complete at least one Specialized Module.

### Technical Success
*   **Performance:** TTS playback initiates instantly (<200ms latency) to maintain flow.
*   **Reliability:** 100% offline functionality with zero crashes due to missing voice data.

### Measurable Outcomes
*   **MVP Success:** User completes 10-question lesson in "Cardinal" and "Time" modules with stats saved locally.

## Product Scope

### MVP - Minimum Viable Product
*   Core "Listen-and-Type" loop with Swedish TTS.
*   Cardinal Numbers (0-1000).
*   **One** Specialized Module: Time (Klockan).
*   Local Progress Tracking (Accuracy/Speed).
*   *No user accounts, no gamification.*

### Growth Features (Post-MVP)
*   **Content Expansion:** Dates, Phone Numbers, Personnummer, Currency.
*   **Advanced Stats:** "Mastery Level" calculation per module.

### Vision (Future)
*   Gamification (Streaks, Badges).
*   Voice Input mode.
*   Cloud Sync.

## User Journeys

### Journey 1: Maria - The specialized breakthrough
Maria is an intermediate learner who can count to 100 but constantly mixes up "ett" (one) and "första" (first) in conversation. It's a small error that marks her as a beginner. She opens Siffermästare and bypasses the basic "1-100" lesson she's already done. She selects the **New "Ordinals" Module**. The app fires rapid-fire prompts: "Tredje... Sjunde... Femte...". After three 2-minute sessions on the bus, she internalizes the pattern. That evening, she correctly says "Det är min första gång" without hesitation.

### Journey 2: Liam - The high-stakes appointment
Liam needs to book a medical appointment but is terrified of the receptionist asking for his *personnummer* over the phone. He usually freezes or has to repeat it in English. He opens Siffermästare's **"Personnummer" Module**. The app simulates the rapid, grouped rhythm of spoken IDs: "Åttioett, noll-tre, tjugo-sju...". He fails to keep up at first. He tries again. And again. By the tenth try, his brain stops translating and starts just *hearing*. He calls the clinic, recites his number smoothly, and the receptionist says "Tack" without pausing. He breathes a sigh of relief.

### Journey 3: Sergei (The Maintainer) - The scalable architecture
Sergei wants to add a "Currency" module requested by users. In a typical legacy app, this would mean hacking UI code and duplicating logic. Instead, Siffermästare's "Generator" pattern allows him to simply create a new `CurrencyGenerator` class implementing the standard interface. He defines the rules (saying "kronor" after the number), registers it in the Factory, and the new module automatically appears in the UI with correct tracking and settings. He ships the feature in hours, not days, proving the system is ready for scale.

### Journey Requirements Summary
*   **Module Selection:** UI must allow jumping to specific, advanced topics (Ordinals, Personnummer) without forced linear progression.
*   **Rhythm/Grouping:** TTS engine must support different "rhythms" for different data types (e.g., grouping phone numbers vs single digits).
*   **Extensible Architecture:** The code structure must treat "Content Types" as plugins/strategies to allow easy expansion by the Maintainer.

## Mobile App Specific Requirements

### Project-Type Overview
Siffermästare is a **Native Android** application designed for **100% Offline** usage. It relies exclusively on on-device processing (TTS) and local persistence (Room) to ensure privacy and reliability.

### Technical Architecture Considerations

**Offline Mode Strategy**
*   **Zero-Network Core:** All lesson generation, audio playback, and scoring must function without an internet connection.
*   **Updates:** App updates will be managed strictly via the Google Play Store mechanism; no in-app update checks required.
*   **Future Network Needs:** Network capabilities (Billing/Monetization) are explicitly deferred to a future definition phase.

**Data Persistence**
*   **Local-Only:** All user progress is stored in a local Room database.
*   **Retention:** No auto-pruning required; text-based lesson records are negligible in size relative to modern capability.

### Implementation Considerations

**Device Permissions**
*   **Current State:** Zero additional permissions required (No Microphone, No Notifications, No Location).
*   **Future Scope:** Permissions will only be added when features (Voice Input) are prioritized.

**Background Work**
*   **Constraint:** No `WorkManager` or background services required. All logic executes while the app is in the foreground.

**Store Compliance**
*   **Privacy:** Complete isolation. The Data Safety section will declare "No data collected/shared".

## Project Scoping & Phased Development

### Phase 1: MVP (Completed)
*   **Core Loop:** Listen-and-Type with Room persistence.
*   **Content:**
    *   Cardinal Numbers (0-20)
    *   Cardinal Numbers (20-100)
    *   Cardinal Numbers (100-1000)
    *   **Ordinals** (1:a - 20:e)
    *   **Time (Formal)** (HH:MM digital format)
*   **Stats:** Basic accuracy/count tracking.

### Phase 2: Refinement & Specialized Content (Next Epic)
**New Content Modules:**
*   **Informal Time:** "Halv tre", "Kvart i fem" logic.
*   **Tricky Pairs:** Specialized generator for 13-19 vs 30-90 distinctions (e.g., distinguishing *tretton* vs *trettio*).
*   **Phone Numbers:** Rhythmic grouping of digits.
*   **Fractions:** Swedish fraction nomenclature (e.g., "en halv", "tre fjärdedelar").
*   **Decimals:** Decimal pronunciation (e.g., "noll komma fem").
*   **Granular Ranges:** Split 0-20 into "0-9" and "10-20" for easier onboarding.

**Critical UX Improvements:**
*   **"Give Up" / Skip:** Mechanism to show answer and move on after 3 failures.
*   **Slow Speed Replay:** "slow replay" button utilizing TTS rate control.
*   **Formatting Fixes:** Proper Time separators (09:30).
*   **Time input flexibility:** Allow "9:30" and "09:30" formats.
*   **Safety:** Exit confirmation dialog during active lessons.

### Phase 3: Adaptive Intelligence (Current Focus)
*   **Bayesian Knowledge Tracing:** Implement a Beta-Distribution based engine to model user probability of knowing specific numbers (Atoms).
*   **Granular Decomposition:** Decompose complex answers (e.g., "25") into atomic components ("20", "5") for precise error tracking.
*   *See [Technical Spec: Learning Model](learning-model-spec.md) for algorithmic details.*

### MVP Strategy & Philosophy
**Approach:** Problem-Solving MVP - Focusing strictly on the specialized utility of auditory training.
**Resource Requirements:** Solo Developer (Maintainer) using Generator Pattern for scale.

### Risk Mitigation Strategy
*   **Technical Risks:** Generator pattern complexity managed by strict Interface adherence.
*   **Market Risks:** Validated by immediate utility to Expats (Personnummer).
*   **Resource Risks:** Scope is modular; features can be dropped purely by not implementing the specific Generator.

## Functional Requirements

### Content Generation Engine
*   **FR1:** System can generate **Informal Time** prompts using Swedish logic (e.g., "kvart i...", "halv...").
*   **FR2:** System can generate **Tricky Pair** discrimination tasks (e.g., alternating between 13/30, 14/40).
*   **FR3:** System can generate **Phone Number** sequences with distinct rhythmic grouping.
*   **FR4:** System can generate **Fractions** and speak them using correct Swedish nomenclature.
*   **FR5:** System can generate **Decimals** and speak them using "komma" format.

### Learning Practice Loop
*   **FR6:** User can **Skip/Give Up** on a question after repeated failures (limit set to 3) to see the correct answer.
*   **FR7:** User can trigger a **Slow Replay** of the current prompt (temporary rate reduction for one utterance).
*   **FR8:** User receives immediate visual feedback (Correct/Incorrect) after submitting an answer.
*   **FR9:** User must **Confirm Exit** before leaving an active lesson.
*   **FR10:** System accepts **Flexible Time Input** (supporting both "9:30" and "09:30" formats).

### Progress & Persistence
*   **FR11:** System calculates and persists **Accuracy %** per module.
*   **FR12:** System calculates and persists **Average Response Time** per module.
*   **FR13:** System stores all data in a local encrypted database (No Account required).

### System & Architecture
*   **FR14:** System functions with **100% Offline Availability** for all core features.
*   **FR15:** System dynamically loads content generators via a plugin/factory pattern.
*   **FR16:** System tracks user knowledge of **Atomic Numbers** (0-9, 10-19, Tens) using a persistent Bayesian model.
*   **FR17:** System **decomposes** user answers to attribute Success/Failure to specific atomic components.
*   **FR18:** System applies a **Forgetting Factor** to knowledge estimates to account for user learning/rustiness over time.

## Non-Functional Requirements

### Performance & Latency
*   **NFR1 (Audio):** TTS playback must initiate within **200ms** of user input to maintain "conversational flow".
*   **NFR2 (Startup):** App cold start to "Ready for Input" must be under **1.5 seconds**.

### Accessibility
*   **NFR3 (Contrast):** All text/UI elements must meet **WCAG AA** contrast standards (essential for our minimalist theme).
*   **NFR4 (Sizing):** Touch targets must be at least **48x48dp** to support usage in transit/motion.

### Reliability
*   **NFR5 (Offline):** App must remain fully functional with **0% packet loss** tolerance (i.e., complete air-gap safety).
