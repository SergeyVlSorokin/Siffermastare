# Story 1.4: Basic Game Loop (0-9)

Status: ready-for-dev

## Story

As a User,
I want to be told if my input matches the spoken number,
so that I know if I understood correctly.

## Acceptance Criteria

1. Logic to compare `currentInput` vs `targetNumber`.
2. On "Check" press:
   - If match: Show "Correct" Toast, clear input, pick new random 0-9 number, speak it.
   - If no match: Show "Try Again" Toast, keep input.

## Tasks / Subtasks

- [ ] Task 1: Implement answer comparison logic (AC: 1)
  - [ ] Create comparison function: `compareAnswer(userInput: String, targetNumber: Int): Boolean`
  - [ ] Handle string to int conversion for comparison
  - [ ] Compare `currentInput` (String) with `targetNumber` (Int)
  - [ ] Handle edge cases: empty input, non-numeric input

- [ ] Task 2: Implement Check button action (AC: 2)
  - [ ] Connect Check button in LessonScreen to validation logic
  - [ ] Get current `targetNumber` (from Story 1.2 - random 0-9)
  - [ ] Compare `currentInput` with `targetNumber`
  - [ ] Handle both match and no-match cases

- [ ] Task 3: Correct answer flow (AC: 2)
  - [ ] Show "Correct" Toast message
  - [ ] Clear `currentInput` state (reset to empty string)
  - [ ] Generate new random digit 0-9
  - [ ] Convert new digit to Swedish text
  - [ ] Call TTS `speak()` with new Swedish number
  - [ ] Update `targetNumber` state

- [ ] Task 4: Incorrect answer flow (AC: 2)
  - [ ] Show "Try Again" Toast message
  - [ ] Keep `currentInput` unchanged (user can edit)
  - [ ] Keep same `targetNumber` (user stays on question)
  - [ ] Allow user to backspace and try again

- [ ] Task 5: State management updates
  - [ ] Add `targetNumber: Int` state to LessonScreen
  - [ ] Initialize `targetNumber` on screen load (random 0-9)
  - [ ] Update `targetNumber` when correct answer is submitted
  - [ ] Ensure state updates trigger UI recomposition

## Dev Notes

### Architecture Compliance

**Source Tree Structure:** [Source: docs/architecture.md#Source Tree Structure]
- Logic can be in LessonScreen for now (simple comparison)
- Future: This logic will move to ViewModel/UseCase in Epic 2
- For this story: Keep it simple in LessonScreen composable

**Layer Separation:** [Source: docs/project_context.md#Architecture Boundaries]
- UI layer (LessonScreen): Contains comparison logic for now (acceptable for MVP)
- Future refactoring: Move to Domain layer (UseCase) in Epic 2
- Data layer (TTSManager): Already created in Story 1.2

**State Management:** [Source: docs/project_context.md#State Management Pattern]
- Use `remember { mutableStateOf() }` for `targetNumber` and `currentInput`
- State managed in LessonScreen (will move to ViewModel in Epic 2)
- Pattern to follow: Single state updates, clear state flow

### Technical Requirements

**Answer Comparison:** [Source: docs/project_context.md#String Handling]
- Compare `currentInput: String` with `targetNumber: Int`
- Convert String to Int for comparison: `currentInput.toIntOrNull()`
- Handle edge cases: empty string, non-numeric input (treat as incorrect)

**String Handling:** [Source: docs/project_context.md#String Handling]
- Use `String` type for user input to preserve leading zeros (not needed for 0-9, but pattern for future)
- Answer comparison: Compare strings, not integers (for consistency with future stories)
- For 0-9: Can compare as Int, but use String pattern for consistency

**Toast Messages:**
- Use `Toast.makeText()` or Compose `Snackbar` for user feedback
- "Correct" message for successful match
- "Try Again" message for incorrect answer
- Note: UI Design Goals mention visual feedback (green/red), but PRD specifies Toast for this story

**Random Number Generation:**
- Use `Random.nextInt(0, 10)` or `(0..9).random()` to generate 0-9
- Generate on screen load (initial question)
- Generate new number after correct answer

**TTS Integration:**
- Reuse TTSManager from Story 1.2
- Convert digit to Swedish text using mapping from Story 1.2
- Call `speak()` with Swedish text after generating new number

### File Structure Requirements

**Files to Modify:**
```
app/src/main/java/com/siffermastare/
└── ui/
    └── lesson/
        └── LessonScreen.kt  (add comparison logic and Check button action)
```

**No New Files Required:**
- Reuse TTSManager from Story 1.2
- Reuse Numpad component from Story 1.3
- Add logic to existing LessonScreen

### Implementation Details

**State Variables:**
```kotlin
var currentInput by remember { mutableStateOf("") }
var targetNumber by remember { mutableStateOf((0..9).random()) }
```

**Answer Comparison Function:**
```kotlin
fun compareAnswer(userInput: String, targetNumber: Int): Boolean {
    val userNumber = userInput.toIntOrNull()
    return userNumber == targetNumber
}
```

**Check Button Action:**
```kotlin
onCheckClick = {
    if (compareAnswer(currentInput, targetNumber)) {
        // Correct answer flow
        showToast("Correct")
        currentInput = ""
        targetNumber = (0..9).random()
        val swedishText = digitToSwedish(targetNumber)
        ttsManager.speak(swedishText)
    } else {
        // Incorrect answer flow
        showToast("Try Again")
        // Keep currentInput and targetNumber unchanged
    }
}
```

**Toast Implementation:**
- Option A: Use Android Toast (requires Context)
  ```kotlin
  Toast.makeText(context, "Correct", Toast.LENGTH_SHORT).show()
  ```
- Option B: Use Compose Snackbar (better UX, Material Design)
  ```kotlin
  val snackbarHostState = remember { SnackbarHostState() }
  // Show snackbar: snackbarHostState.showSnackbar("Correct")
  ```
- Recommendation: Use Snackbar for better Material Design integration

**Random Number Generation:**
- Generate initial number on screen load: `LaunchedEffect(Unit) { targetNumber = (0..9).random() }`
- Generate new number after correct answer: `targetNumber = (0..9).random()`
- Convert to Swedish before speaking: Reuse conversion from Story 1.2

### Visual Feedback (Future Enhancement)

**Note:** UI Design Goals mention visual feedback: [Source: docs/prd/user-interface-design-goals.md#Interaction Logic]
- Correct: Input turns green, checkmark animation, 500ms delay
- Incorrect: Input shakes, turns red

**For This Story:**
- PRD specifies Toast messages (simpler implementation)
- Visual feedback (colors, animations) can be added in future stories
- Focus on functional game loop first, enhance UX later

**Future Enhancement Path:**
- Story 2.2 will add visual feedback states (Green/Red)
- This story establishes the core loop, visual polish comes next

### Testing Requirements

**Manual Testing Checklist:**
- [ ] Correct answer: Toast shows "Correct", input clears, new number generated and spoken
- [ ] Incorrect answer: Toast shows "Try Again", input remains, same number stays
- [ ] Empty input + Check: Should show "Try Again" (empty is incorrect)
- [ ] Non-numeric input + Check: Should show "Try Again" (invalid input)
- [ ] Multiple correct answers in sequence: Each generates new random number
- [ ] Backspace after incorrect: User can edit and try again
- [ ] TTS speaks new number after correct answer

**Edge Cases to Test:**
- Empty input when Check is pressed
- Non-numeric characters in input (should handle gracefully)
- Very long input (should compare correctly)
- Rapid Check button clicks (should handle properly)

**No Unit Tests Required:**
- This is MVP foundation story
- Manual testing sufficient for game loop validation
- Unit tests for comparison logic can be added in Epic 2

### Project Structure Notes

**Alignment with Architecture:**
- ✅ Logic in UI layer for now (acceptable for MVP)
- ✅ Will refactor to Domain layer (UseCase) in Epic 2
- ✅ Reuses components from previous stories (TTSManager, Numpad)

**Dependencies:**
- ✅ No new dependencies needed
- ✅ Reuses existing TTSManager and Numpad components

### Critical Don't-Miss Rules

**Anti-Patterns to Avoid:** [Source: docs/project_context.md#Anti-Patterns]
- ❌ NO blocking main thread - all operations should be non-blocking
- ❌ NO business logic in Composables - acceptable for MVP, will refactor in Epic 2
- ❌ NO hardcoded English text - use Swedish number conversion

**State Management:** [Source: docs/project_context.md#State Management Pattern]
- Clear state on correct answer
- Keep state on incorrect answer
- Update state atomically (avoid race conditions)

**Answer Comparison:** [Source: docs/project_context.md#String Handling]
- Handle string to int conversion safely (`toIntOrNull()`)
- Handle edge cases: empty, non-numeric input
- Compare correctly: String input vs Int target

**TTS Integration:**
- Always convert digit to Swedish before speaking
- Reuse conversion function from Story 1.2
- Ensure TTS speaks new number after correct answer

### Game Loop Flow

**Initial State:**
1. Screen loads
2. Generate random digit 0-9 → `targetNumber`
3. Convert to Swedish text
4. Speak number via TTS

**User Interaction:**
1. User taps digit buttons → updates `currentInput`
2. User taps Check button → triggers comparison

**Correct Answer Flow:**
1. Compare: `currentInput` == `targetNumber` → true
2. Show "Correct" Toast/Snackbar
3. Clear `currentInput` → ""
4. Generate new random 0-9 → `targetNumber`
5. Convert to Swedish text
6. Speak new number via TTS
7. Loop continues

**Incorrect Answer Flow:**
1. Compare: `currentInput` != `targetNumber` → false
2. Show "Try Again" Toast/Snackbar
3. Keep `currentInput` unchanged
4. Keep `targetNumber` unchanged
5. User can backspace and try again

### References

- **PRD:** [Source: docs/prd/epic-details.md#Story 1.4: Basic Game Loop]
- **UI Design Goals:** [Source: docs/prd/user-interface-design-goals.md#Interaction Logic]
- **Architecture:** [Source: docs/architecture.md#High-Level Architecture]
- **Project Context:** [Source: docs/project_context.md#String Handling, State Management Pattern]
- **Previous Stories:**
  - [Source: docs/sprint-artifacts/1-1-project-initialization-navigation.md]
  - [Source: docs/sprint-artifacts/1-2-tts-integration.md]
  - [Source: docs/sprint-artifacts/1-3-custom-numpad-ui.md]

## Dev Agent Record

### Context Reference

- PRD: docs/prd/epic-details.md (Epic 1, Story 1.4)
- UI Design: docs/prd/user-interface-design-goals.md (Interaction Logic)
- Architecture: docs/architecture.md (High-Level Architecture)
- Project Context: docs/project_context.md (String Handling, State Management)
- Previous Stories: docs/sprint-artifacts/1-1-project-initialization-navigation.md, 1-2-tts-integration.md, 1-3-custom-numpad-ui.md

### Agent Model Used

_To be filled by dev agent_

### Debug Log References

_To be filled during implementation_

### Completion Notes List

_To be filled when story is complete_

### File List

_To be filled when story is complete_

