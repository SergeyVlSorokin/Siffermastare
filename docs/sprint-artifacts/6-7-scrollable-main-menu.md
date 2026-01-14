# Story 6.7: Scrollable Main Menu

Status: done

## Story

As a User,
I want to be able to scroll main menu items if they do not fit on screen,
so that I can access all lessons even on smaller devices or when new content is added.

## Acceptance Criteria

1. **Given** the list of menu items on the Home Screen exceeds the visible screen height
   **When** I swipe up/down
   **Then** the menu list scrolls smoothly.

2. **Given** any screen size
   **Then** all menu buttons ("Siffror", "Tid", "Bråk", etc.) must be accessible via scrolling.

3. **Given** the "Scrollable" state
   **Then** the header ("Siffermästare") and Dashboard stats (Streak/Lessons) should ideally remain visible or scroll elegantly (Simple vertical scroll for the whole column is acceptable for MVP, but pinning header is nicer. Let's stick to **simple full-column scroll** or **scrollable menu section** to minimize complexity based on "Swedish Minimalist" theme guidelines. **Decision:** Make the column containing buttons scrollable.)

## Tasks / Subtasks

- [ ] Task 1: Update HomeScreen Layout
  - [ ] Modify `HomeScreen.kt`.
  - [ ] Apply `Modifier.verticalScroll(rememberScrollState())` to the Column containing the menu buttons.
  - [ ] Ensure padding/spacing remains consistent with the theme.

- [ ] Task 2: Verify Scrollability
  - [ ] Test on a small device emulator (or resize window) to ensure scrolling works.
  - [ ] Verify all buttons are reachable.
  - [ ] Ensure "Streak" and "Lessons" dashboard elements are still visible or part of the flow.

## Dev Notes

- **Implementation**: The current `HomeScreen` likely uses a `Column`. Just adding `.verticalScroll(rememberScrollState())` to the modifier chain is usually sufficient.
- **Context**: We have added many lessons (Fractions, Decimals, Phone Numbers), making the list long.
- **Design Reference**: Maintain the "Swedish Minimalist" spacing (16dp/24dp gaps).

## Technical Constraints

- Use standard Compose modifiers (`verticalScroll`).
- Do not use `LazyColumn` unless the list is dynamic/infinite (it's fixed size menu, so `Column` + `Scroll` is fine and simpler).

## File List
- app/src/main/java/com/siffermastare/ui/home/HomeScreen.kt
