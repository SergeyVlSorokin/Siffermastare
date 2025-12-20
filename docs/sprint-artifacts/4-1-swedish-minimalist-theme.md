# Story 4.1: Apply Swedish Minimalist Theme

Status: Done

## Story

As a User,
I want the app to look clean, modern, and "Swedish",
so that I enjoy using it and it feels professional.

## Acceptance Criteria

1.  **Color Palette**: Define a primary palette inspired by Sweden (Blue/Yellow accents? Or minimal monochrome + wood/nature tones?).
    -   *Decision*: Use a "Modern Scandinavian" look. Clean white/off-white backgrounds, sharp typography (Inter/Roboto), high contrast dark text.
    -   Primary: Deep Swedish Blue (#006AA7) or a softer variant.
    -   Secondary: Warm Yellow (#FECC00) for highlights/CTAs (sparingly).
    -   Surface: Paper white / Light gray.
2.  **Typography**: Use Material3 typography but customized for readability. Large numbers, clear instructions.
3.  **Components**:
    -   Buttons: Rounded corners, flat or soft shadow.
    -   Cards: Check-in with Material3 Card defaults but clean up padding.
4.  **Dark Mode**: Ensure the theme supports Dark Mode (Dark Navy background?).

## Tasks / Subtasks

- [x] Task 1: Define Color and Type Systems (AC: 1, 2)
  - [x] Update `Color.kt`.
  - [x] Update `Type.kt`.
  - [x] Update `Theme.kt`.
- [x] Task 2: Refactor Components (AC: 3)
  - [x] Update `HomeScreen` buttons to use new styles.
  - [x] Update `LessonScreen` Numpad and Input/Text.
- [x] Task 3: Dark Mode Verification (AC: 4)
  - [x] Verify colors in dark theme.

## Dev Agent Record

### File List
- `app/src/main/java/com/siffermastare/ui/theme/Color.kt`
- `app/src/main/java/com/siffermastare/ui/theme/Type.kt`
- `app/src/main/java/com/siffermastare/ui/theme/Theme.kt`
- `app/src/main/java/com/siffermastare/ui/home/HomeScreen.kt`
- `app/src/main/java/com/siffermastare/ui/lesson/LessonScreen.kt`
