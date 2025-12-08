# Story 1.2: TTS Integration (The Spike Implementation)

Status: ready-for-dev

## Story

As a User,
I want to hear a Swedish number spoken when the screen loads,
so that I can practice listening.

## Acceptance Criteria

1. `TextToSpeech` engine initialized with `Locale("sv", "SE")`.
2. Helper function `speak(text: String)` created.
3. On `LessonScreen` load, app randomly selects digit 0-9 and speaks it.
4. "Replay" button invokes `speak()` again.

## Tasks / Subtasks

- [ ] Task 1: Create TTSManager wrapper class (AC: 1, 2)
  - [ ] Create `data/tts/TTSManager.kt` following architecture structure
  - [ ] Implement TTS initialization with Swedish locale `Locale("sv", "SE")`
  - [ ] Create `speak(text: String)` helper function
  - [ ] Handle TTS initialization callbacks (OnInitListener)
  - [ ] Implement error handling for missing Swedish voice

- [ ] Task 2: Integrate TTSManager into LessonScreen (AC: 3, 4)
  - [ ] Update `ui/lesson/LessonScreen.kt` to use TTSManager
  - [ ] Generate random digit 0-9 on screen load
  - [ ] Call `speak()` with Swedish number string when screen loads
  - [ ] Add "Replay" button (Speaker icon) to LessonScreen
  - [ ] Implement replay functionality to call `speak()` again

- [ ] Task 3: Swedish number conversion (AC: 3)
  - [ ] Create helper function to convert digit (0-9) to Swedish text
  - [ ] Map: 0="noll", 1="ett", 2="två", 3="tre", 4="fyra", 5="fem", 6="sex", 7="sju", 8="åtta", 9="nio"
  - [ ] Use this conversion when calling `speak()`

- [ ] Task 4: Error handling and user feedback (AC: 1)
  - [ ] Check if Swedish voice is available after TTS initialization
  - [ ] Show Snackbar with link to System Settings if voice missing
  - [ ] Never crash the app - handle all TTS failures gracefully

## Dev Notes

### Architecture Compliance

**Source Tree Structure:** [Source: docs/architecture.md#Source Tree Structure]
- Create `TTSManager.kt` in `app/src/main/java/com/siffermastare/data/tts/`
- Update `LessonScreen.kt` in `app/src/main/java/com/siffermastare/ui/lesson/`
- Follow Clean Architecture: TTSManager in Data layer, LessonScreen in UI layer

**TTSManager Responsibilities:** [Source: docs/architecture.md#Data Components]
- Handles initialization, language checking (Swedish), speaking text, and error callbacks
- Isolates Android dependencies (wraps Android TextToSpeech class)
- Provides clean interface for UI layer to use

**Layer Separation:** [Source: docs/project_context.md#Architecture Boundaries]
- Data layer (TTSManager): Android dependencies allowed (TextToSpeech API)
- UI layer (LessonScreen): Only presentation, calls TTSManager
- Domain layer: Not needed for this story (number conversion can be simple helper in UI or Data layer)

### Technical Requirements

**TTS API:** [Source: docs/architecture.md#Technology Stack Table]
- Use Android native `TextToSpeech` API (no external dependencies)
- Zero external dependency size
- Uses device's built-in TTS engine

**Swedish Locale:** [Source: docs/prd/epic-details.md#Story 1.2]
- Initialize with `Locale("sv", "SE")` - Swedish (Sweden)
- Verify Swedish voice is available on device
- Handle case where Swedish voice pack is not installed

**Coroutines Usage:** [Source: docs/project_context.md#Coroutines & Async Patterns]
- TTS operations should be in coroutines (though TextToSpeech API is mostly synchronous)
- Use `viewModelScope` if ViewModel is created (not required for this story, but pattern to follow)
- For now, TTS can be called directly from Composable (TextToSpeech is thread-safe)

**Error Handling:** [Source: docs/project_context.md#Error Handling]
- TTS failures: Handle Swedish voice missing gracefully
- Show Snackbar linking to System Settings, never crash
- Log errors for debugging but don't block user flow

### File Structure Requirements

**Files to Create:**
```
app/src/main/java/com/siffermastare/
└── data/
    └── tts/
        └── TTSManager.kt
```

**Files to Modify:**
```
app/src/main/java/com/siffermastare/
└── ui/
    └── lesson/
        └── LessonScreen.kt  (add TTS integration and Replay button)
```

### TTSManager Implementation Details

**Class Structure:**
```kotlin
class TTSManager(private val context: Context) {
    private var textToSpeech: TextToSpeech? = null
    private var isInitialized = false
    
    fun initialize(callback: (Boolean) -> Unit)
    fun speak(text: String)
    fun shutdown()
}
```

**Initialization Pattern:**
- Create TextToSpeech instance with context and OnInitListener
- Check if Swedish locale is available after initialization
- Set language to `Locale("sv", "SE")`
- Handle initialization success/failure in callback

**Error Handling:**
- If Swedish voice not available: Show Snackbar with action to open System Settings
- If TTS initialization fails: Log error, show user-friendly message
- Never throw exceptions that could crash the app

**Swedish Number Conversion:**
- Simple mapping function: `fun digitToSwedish(digit: Int): String`
- Map digits 0-9 to Swedish words
- Can be placed in TTSManager or as separate utility (keep it simple for now)

### LessonScreen Integration

**Random Number Generation:**
- Use `Random.nextInt(0, 10)` or `(0..9).random()` to select digit 0-9
- Generate on screen load (use `LaunchedEffect` or `DisposableEffect`)
- Convert digit to Swedish text before calling `speak()`

**Replay Button:**
- Use Material3 `IconButton` with Speaker icon
- Place prominently on LessonScreen (per PRD: "prominent Replay button")
- On click: Call `speak()` again with same number text
- Use `Icons.Default.VolumeUp` or similar Material icon

**Screen Lifecycle:**
- Initialize TTSManager when screen loads
- Shutdown TTSManager when screen is disposed (cleanup)
- Use `DisposableEffect` or `LaunchedEffect` for lifecycle management

### Testing Requirements

**Manual Testing Checklist:**
- [ ] TTS initializes with Swedish locale
- [ ] Swedish number (0-9) is spoken when LessonScreen loads
- [ ] Replay button speaks the number again
- [ ] App handles missing Swedish voice gracefully (shows Snackbar)
- [ ] App doesn't crash if TTS initialization fails
- [ ] TTS resources are cleaned up when screen is disposed

**Edge Cases to Test:**
- Device without Swedish TTS voice pack installed
- TTS initialization failure
- Multiple rapid clicks on Replay button
- Screen rotation (TTS should reinitialize if needed)

**No Unit Tests Required:**
- This is a spike implementation (validation story)
- Manual "ear-testing" required per PRD
- Unit tests for TTS logic can be added in later stories

### Project Structure Notes

**Alignment with Architecture:**
- ✅ TTSManager in Data layer (`data/tts/`) - correct location
- ✅ LessonScreen in UI layer (`ui/lesson/`) - correct location
- ✅ Clean separation: UI calls Data layer, no business logic in UI

**Dependencies:**
- ✅ No new dependencies needed - uses Android native TextToSpeech API
- ✅ Coroutines already in dependencies (for future async patterns)

### Critical Don't-Miss Rules

**Anti-Patterns to Avoid:** [Source: docs/project_context.md#Anti-Patterns]
- ❌ NO blocking main thread - TTS operations should be non-blocking
- ❌ NO crashing on TTS failures - always handle errors gracefully
- ❌ NO hardcoded English numbers - must use Swedish conversion

**Edge Cases:** [Source: docs/project_context.md#Edge Cases]
- **Swedish TTS voice missing:** Show Snackbar with link to System Settings, never crash
- **TTS initialization failure:** Log error, show user-friendly message, allow app to continue
- **Screen lifecycle:** Properly cleanup TTS resources when screen disposed

**Error Handling:** [Source: docs/project_context.md#Error Handling]
- TTS failures: Handle Swedish voice missing gracefully - show Snackbar linking to System Settings, never crash
- All TTS errors should be logged but not block user flow

### Swedish Number Mapping

**Digit to Swedish Text:**
- 0 → "noll"
- 1 → "ett"
- 2 → "två"
- 3 → "tre"
- 4 → "fyra"
- 5 → "fem"
- 6 → "sex"
- 7 → "sju"
- 8 → "åtta"
- 9 → "nio"

**Implementation:**
- Simple when/switch statement or map
- Can be function in TTSManager or separate utility
- Keep it simple for this spike story

### References

- **PRD:** [Source: docs/prd/epic-details.md#Story 1.2: TTS Integration]
- **Architecture:** [Source: docs/architecture.md#Data Components, Technology Stack Table]
- **Project Context:** [Source: docs/project_context.md#Error Handling, Edge Cases]
- **Android TTS Docs:** https://developer.android.com/reference/android/speech/tts/TextToSpeech
- **Previous Story:** [Source: docs/sprint-artifacts/1-1-project-initialization-navigation.md]

## Dev Agent Record

### Context Reference

- PRD: docs/prd/epic-details.md (Epic 1, Story 1.2)
- Architecture: docs/architecture.md (TTSManager, Data Components)
- Project Context: docs/project_context.md (Error Handling, Edge Cases)
- Previous Story: docs/sprint-artifacts/1-1-project-initialization-navigation.md

### Agent Model Used

_To be filled by dev agent_

### Debug Log References

_To be filled during implementation_

### Completion Notes List

_To be filled when story is complete_

### File List

_To be filled when story is complete_

