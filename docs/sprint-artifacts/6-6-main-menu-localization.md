# Story 6.6: Swedish Main Menu Localization

Status: done

## Story

As a User,
I want the main menu options to be displayed in Swedish,
so that the entire app experience feels immersive and consistent with the learning language.

## Acceptance Criteria

1. **Given** I am on the Home Screen
   **Then** all lesson buttons/menu items should display Swedish text.

2. **Mappings**:
   - "Numbers 0-20" -> "Siffror 0-20"
   - "Numbers 20-100" -> "Siffror 20-100"
   - "Numbers 100-1000" -> "Siffror 100-1000"
   - "Ordinals" -> "Ordningstal"
   - "Time (Digital)" -> "Tid (Digital)"
   - "Time (Analogue)" -> "Tid (Analog)"
   - "Tricky Pairs" -> "Kluriga Par"
   - "Phone Numbers" -> "Telefonnummer"
   - "Fractions" -> "Bråk"
   - "Decimals" -> "Decimaltal"

## Tasks / Subtasks

- [x] Task 1: Update String Resources
  - [x] Modify `res/values/strings.xml` to update `menu_*` values to Swedish.
  - [x] Ensure `home_title` or other labels are also appropriate (Siffermästare is already good).

## Dev Notes

- This is a simple resource update.
- Verify no other screens (like Summary) have hardcoded English that needs obvious quick fixing, though this story focuses on Main Menu.

## File List
- app/src/main/res/values/strings.xml
