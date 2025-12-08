# Story 1.3: Custom Numpad UI

Status: ready-for-dev

## Story

As a User,
I want to tap large number buttons inside the app,
so that I can enter my answer quickly without a system keyboard.

## Acceptance Criteria

1. Create `Numpad` Composable.
2. Grid layout (1-3, 4-6, 7-9, 0 centered bottom).
3. Includes "Backspace" button.
4. Includes "Check" (Action) button.
5. Tapping numbers updates a state variable `currentInput`.

## Tasks / Subtasks

- [ ] Task 1: Create Numpad Composable component (AC: 1, 2)
  - [ ] Create `ui/components/Numpad.kt` following architecture structure
  - [ ] Implement grid layout using Compose `LazyVerticalGrid` or `Row`/`Column` with `GridCells`
  - [ ] Create digit buttons 1-3 (top row), 4-6 (middle row), 7-9 (third row)
  - [ ] Create digit button 0 (centered bottom row)
  - [ ] Use Material3 Button components with appropriate styling

- [ ] Task 2: Implement Backspace button (AC: 3)
  - [ ] Add "Backspace" button to numpad layout
  - [ ] Use appropriate icon (Material Icons: `Icons.Default.Backspace` or text "⌫")
  - [ ] Implement callback to remove last character from input
  - [ ] Handle edge case: Backspace on empty input (no-op)

- [ ] Task 3: Implement Check button (AC: 4)
  - [ ] Add "Check" button as primary action button
  - [ ] Use Material3 Button with primary styling (accent color)
  - [ ] Position prominently (likely bottom center or right side)
  - [ ] Implement callback for check action (validation will be in Story 1.4)

- [ ] Task 4: State management integration (AC: 5)
  - [ ] Accept `currentInput: String` as parameter (state from parent)
  - [ ] Accept callback `onDigitClick: (Int) -> Unit` for digit buttons
  - [ ] Accept callback `onBackspaceClick: () -> Unit` for backspace
  - [ ] Accept callback `onCheckClick: () -> Unit` for check button
  - [ ] Update parent state through callbacks (no local state in Numpad)

- [ ] Task 5: Integrate Numpad into LessonScreen (AC: 5)
  - [ ] Add `currentInput` state variable to LessonScreen
  - [ ] Add Numpad component to LessonScreen layout
  - [ ] Connect digit button clicks to update `currentInput`
  - [ ] Connect Backspace to remove last character
  - [ ] Connect Check button (action will be implemented in Story 1.4)

- [ ] Task 6: Styling and UX polish (AC: 1, 2)
  - [ ] Apply Swedish Minimalist aesthetic (clean lines, ample whitespace)
  - [ ] Use Swedish flag colors as accents (Blue #006AA7, Yellow #FECC00)
  - [ ] Ensure buttons are large and easy to tap (accessibility)
  - [ ] High contrast text for readability (WCAG AA compliance)

## Dev Notes

### Architecture Compliance

**Source Tree Structure:** [Source: docs/architecture.md#Source Tree Structure]
- Create `Numpad.kt` in `app/src/main/java/com/siffermastare/ui/components/`
- Update `LessonScreen.kt` in `app/src/main/java/com/siffermastare/ui/lesson/`
- Follow Clean Architecture: Reusable component in UI layer

**Component Organization:** [Source: docs/project_context.md#Component Organization]
- Reusable components: Place in `ui/components/` (e.g., `Numpad`, `FeedbackText`)
- Numpad is a reusable component that can be used across different screens

**State Management Pattern:** [Source: docs/project_context.md#State Management Pattern]
- Numpad should NOT have local state - it's a controlled component
- Parent (LessonScreen) manages `currentInput` state
- Numpad receives state and callbacks as parameters
- Follows "State-driven" pattern from project context

### Technical Requirements

**Compose Requirements:** [Source: docs/project_context.md#Framework-Specific Rules]
- All UI must be Jetpack Compose (NO XML layouts)
- Use Material Design 3 components (Button, IconButton)
- Use `LazyVerticalGrid` or `Row`/`Column` with `GridCells.Fixed(3)` for grid layout

**Grid Layout Specification:** [Source: docs/project_context.md#Custom Numpad Component]
- **Top row:** Digits 1, 2, 3
- **Middle row:** Digits 4, 5, 6
- **Third row:** Digits 7, 8, 9
- **Bottom row:** Digit 0 centered (spans or centered in row)

**Button Requirements:** [Source: docs/project_context.md#Custom Numpad Component]
- Digits 0-9: Number buttons
- "Backspace" button: Remove last character
- "Check" button: Primary action button (submit answer)

**State Management:** [Source: docs/project_context.md#State Management Pattern]
- Numpad is a controlled component - receives state via parameters
- Parent manages `currentInput: String` state
- Callbacks: `onDigitClick(Int)`, `onBackspaceClick()`, `onCheckClick()`
- No `remember` or local state in Numpad component itself

### File Structure Requirements

**Files to Create:**
```
app/src/main/java/com/siffermastare/
└── ui/
    └── components/
        └── Numpad.kt
```

**Files to Modify:**
```
app/src/main/java/com/siffermastare/
└── ui/
    └── lesson/
        └── LessonScreen.kt  (add currentInput state and Numpad component)
```

### Numpad Component Implementation Details

**Component Signature:**
```kotlin
@Composable
fun Numpad(
    currentInput: String,
    onDigitClick: (Int) -> Unit,
    onBackspaceClick: () -> Unit,
    onCheckClick: () -> Unit,
    modifier: Modifier = Modifier
)
```

**Grid Layout Options:**
1. **Option A:** Use `LazyVerticalGrid` with `GridCells.Fixed(3)`
   - Top row: 1, 2, 3
   - Middle row: 4, 5, 6
   - Third row: 7, 8, 9
   - Bottom row: Use `Row` with centered 0

2. **Option B:** Use nested `Row` and `Column`
   - More control over layout
   - Easier to center 0 in bottom row

**Button Implementation:**
- Digit buttons: Use Material3 `Button` or `OutlinedButton`
- Backspace: Use `IconButton` with `Icons.Default.Backspace` or text "⌫"
- Check: Use Material3 `Button` with primary styling (accent color)
- All buttons should be large enough for easy tapping (accessibility)

**State Updates:**
- Digit click: Append digit to `currentInput` (parent handles via `onDigitClick`)
- Backspace: Remove last character from `currentInput` (parent handles via `onBackspaceClick`)
- Check: Trigger validation (parent handles via `onCheckClick` - validation in Story 1.4)

### LessonScreen Integration

**State Management:**
```kotlin
var currentInput by remember { mutableStateOf("") }
```

**Numpad Integration:**
- Add Numpad component to LessonScreen layout
- Pass `currentInput` as parameter
- Pass callbacks:
  - `onDigitClick = { digit -> currentInput += digit.toString() }`
  - `onBackspaceClick = { if (currentInput.isNotEmpty()) currentInput = currentInput.dropLast(1) }`
  - `onCheckClick = { /* validation in Story 1.4 */ }`

**Display Current Input:**
- Show `currentInput` value on screen (Text composable)
- Place above or below Numpad
- Style according to Swedish Minimalist aesthetic

### Styling and UX Requirements

**Swedish Minimalist Aesthetic:** [Source: docs/prd/user-interface-design-goals.md#Branding]
- Clean lines, ample whitespace
- Distraction-free interface
- Focus on speed and usability

**Color Palette:** [Source: docs/project_context.md#Theme & Styling]
- Swedish flag colors: Blue #006AA7, Yellow #FECC00 as accents
- Use against clean white/dark mode background
- Check button: Use primary/accent color (Blue or Yellow)

**Accessibility:** [Source: docs/project_context.md#Theme & Styling]
- High contrast text (WCAG AA compliance)
- Large buttons for easy tapping
- Clear visual feedback on button press

**Button Sizing:**
- Buttons should be large enough for comfortable tapping
- Consider minimum touch target size (48dp recommended)
- Ensure spacing between buttons for easy interaction

### Testing Requirements

**Manual Testing Checklist:**
- [ ] All digit buttons (0-9) are visible and correctly laid out
- [ ] Grid layout matches specification (1-3, 4-6, 7-9, 0 centered)
- [ ] Tapping digit buttons updates `currentInput` correctly
- [ ] Backspace button removes last character
- [ ] Backspace on empty input does nothing (no crash)
- [ ] Check button is visible and clickable
- [ ] Buttons are large enough for easy tapping
- [ ] Layout works in portrait mode
- [ ] High contrast text is readable

**UI Testing (Future):**
- UI tests for Numpad interactions can be added in later stories
- Focus on manual testing for this story

**Edge Cases to Test:**
- Empty input + Backspace (should be no-op)
- Very long input (should handle gracefully)
- Rapid button tapping (should be responsive)
- Screen rotation (layout should adapt)

### Project Structure Notes

**Alignment with Architecture:**
- ✅ Numpad in `ui/components/` - correct location for reusable components
- ✅ LessonScreen in `ui/lesson/` - correct location for screen-level composables
- ✅ State management in parent (LessonScreen) - follows controlled component pattern

**Dependencies:**
- ✅ No new dependencies needed - uses existing Compose and Material3 libraries

### Critical Don't-Miss Rules

**Anti-Patterns to Avoid:** [Source: docs/project_context.md#Anti-Patterns]
- ❌ NO local state in Numpad - it's a controlled component
- ❌ NO business logic in Numpad - only UI presentation
- ❌ NO XML layouts - all UI must be Compose

**State Management:** [Source: docs/project_context.md#Custom Numpad Component]
- **State-driven:** Update based on ViewModel state, not local state
- For this story: Update based on parent state (LessonScreen), not local `remember`
- Pattern to follow: When ViewModels are added, state will come from ViewModel

**Component Design:**
- Numpad should be reusable and composable
- Accept all state and callbacks as parameters
- No side effects or internal state management

### Visual Feedback States (Future)

**Note:** Visual feedback states (Green for correct, Red for error) will be implemented in Story 1.4. For this story:
- Numpad buttons should have neutral/default state
- Input display should show current value
- Visual feedback (colors, animations) will be added when validation is implemented

### References

- **PRD:** [Source: docs/prd/epic-details.md#Story 1.3: Custom Numpad UI]
- **UI Design Goals:** [Source: docs/prd/user-interface-design-goals.md#Key Interaction Paradigms]
- **Architecture:** [Source: docs/architecture.md#Source Tree Structure]
- **Project Context:** [Source: docs/project_context.md#Custom Numpad Component, State Management Pattern]
- **Previous Stories:** 
  - [Source: docs/sprint-artifacts/1-1-project-initialization-navigation.md]
  - [Source: docs/sprint-artifacts/1-2-tts-integration.md]

## Dev Agent Record

### Context Reference

- PRD: docs/prd/epic-details.md (Epic 1, Story 1.3)
- UI Design: docs/prd/user-interface-design-goals.md
- Architecture: docs/architecture.md (Source Tree Structure)
- Project Context: docs/project_context.md (Custom Numpad Component, State Management)
- Previous Stories: docs/sprint-artifacts/1-1-project-initialization-navigation.md, 1-2-tts-integration.md

### Agent Model Used

_To be filled by dev agent_

### Debug Log References

_To be filled during implementation_

### Completion Notes List

_To be filled when story is complete_

### File List

_To be filled when story is complete_

