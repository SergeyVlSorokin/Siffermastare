# Story 7.14: Knowledge Dashboard

Status: done

## Story

As a learner,
I want to see my overall mastery of Swedish numbers on the home screen,
so that I feel motivated by my progress.

## Acceptance Criteria

1. **Given** I am on the Home Screen
2. **Then** The old "Total Lessons / Streak" text stats are consolidated into a single clickable card
3. **And** The card shows: title "Sammanlagd behärskning", the Beta Heatmap bar, and "Lektioner: N" (left-aligned) alongside "Svit: N Dagar" (right-aligned) in button-equivalent font size
4. **And** The card has a white/dark-mode-adaptive background with a blue border (matching button primary color)
5. **And** A visual "Overall Mastery Bar" (Beta Heatmap gradient style) is prominently displayed within this card
6. **And** This overall bar statistically aggregates the total $\alpha$ (successes) and $\beta$ (failures) across all tracked atoms into a single distribution by averaging the values
7. **When** The user taps the overall mastery stat card
8. **Then** They navigate to a new "Mastery Dashboard" screen ("Behärskningsöversikt")
9. **And** The Mastery Dashboard shows: global mastery row (title + percentage), global Beta Heatmap bar, color legend, "Detaljerad översikt" heading with sort icon and an "Otestad" toggle checkbox on the same line
10. **And** The dashboard displays **all possible atoms** — including untested ones (shown as gray baseline bars) — not just atoms present in the database
11. **And** Unchecking the "Visa otestade" checkbox hides all atoms with no recorded data (baseline alpha=1, beta=1)
12. **And** The user can sort atoms via a drop-down with three options: "Naturlig ordning" (default), "Kunskap (låg först)", "Kunskap (hög först)"
13. **And** Atom display names are human-readable (e.g. "ord:4" → "4 (ordningstal)", "#kvart" → "kvart", "time_informal|halv" → "halv")
14. **And** The dashboard contains a color legend explaining Red/Yellow/Green without statistical jargon
15. **And** If no practiced atoms are present, an appropriate Swedish-language empty state message is shown

## Tasks / Subtasks

- [x] Task 1: Domain Logic for Processing Atom States
  - [x] 1.1 Create `GetMasteryDataUseCase.kt` accepting a `KnowledgeRepository` and a list of `NumberGenerator` instances.
  - [x] 1.2 Enumerate **all** possible atom IDs from generators via `getAllAtomIds()`. Merge with DB states: atoms missing from DB receive the baseline prior `(α=1, β=1)`.
  - [x] 1.3 For each atom, calculate individual proficiency $\mu = \frac{\alpha}{\alpha + \beta}$ and format its display name (strip category pipes; convert `ord:N` → `N (ordningstal)`; strip `#` prefix).
  - [x] 1.4 Calculate global aggregate by averaging all α and β values.
  - [x] 1.5 Expose a `MasteryData` model containing `globalState` and the flat list of all atom states.
- [x] Task 2: `getAllAtomIds()` on Generator Interface
  - [x] 2.1 Add `getAllAtomIds(): Set<String>` to the `NumberGenerator` interface.
  - [x] 2.2 Implement this method in all 10 generators: `CardinalGenerator`, `OrdinalGenerator`, `TrickyPairsGenerator`, `TimeGenerator`, `InformalTimeGenerator`, `FractionsGenerator`, `DecimalsGenerator`, `PhoneNumberGenerator`.
  - [x] 2.3 Expose `allGenerators` as a shared lazy property on `SiffermastareApplication` for DI.
- [x] Task 3: Sorting in MasteryViewModel
  - [x] 3.1 Add `MasterySortOption` enum: `NATURAL_ORDER` (default), `KNOWLEDGE_INCREASING`, `KNOWLEDGE_DECREASING`.
  - [x] 3.2 Add `showUntested: Boolean` state (default `true`). When false, filter out atoms where `alpha == 1f && beta == 1f`.
  - [x] 3.3 Use `combine()` on `_masteryData`, `_sortOption`, `_showUntested` flows to reactively produce `MasteryUiState.Success`.
  - [x] 3.4 Natural order comparator strips numeric part from IDs and sorts numerically (matching Story 7.13 pattern).
- [x] Task 4: Home Screen Card Redesign
  - [x] 4.1 Replace the two separate stat cards with a single `OutlinedCard` using `surface` background and 2dp primary-color border.
  - [x] 4.2 Inside the card: title row, `BetaHeatmapBar`, then a `Row` with `Lektioner: N` (left) and `Svit: N Dagar` (right) at `labelLarge` font size.
  - [x] 4.3 Entire card is clickable and navigates to `Screen.Mastery`.
- [x] Task 5: Mastery Dashboard Screen UI
  - [x] 5.1 Global mastery shown as a `Row` with title + percentage (color-coded), then `BetaHeatmapBar` below.
  - [x] 5.2 Color legend (Behärskas/Inlärning/Behöver övas/Otestad) using colored circles.
  - [x] 5.3 "Detaljerad översikt" heading with sort dropdown icon and "Visa otestade" checkbox inline on the same row.
  - [x] 5.4 Atom list in a `LazyVerticalGrid` (2 columns). Each cell shows display name, percentage, and `BetaHeatmapBar`.
  - [x] 5.5 Empty state distinguishes between no data ever vs. all atoms filtered by toggle.
- [x] Task 6: Color Scheme for Mastery Levels
  - [x] 6.1 `getMasteryColor(mu: Float)`: Green ≥ 85%, Amber/Yellow ≥ 50% (and ≠ 0.5), LightGray at exactly 0.5 (untested prior), Red < 50%.
- [x] Task 7: BetaHeatmapBar Visualization Redesign
  - [x] 7.1 Gradient is **inverted**: opacity is 0 at the PDF peak (mode), ramps up to 0.6 at the tails. This makes distribution concentrated→ narrow colored band; uniform → wide color fill.
  - [x] 7.2 μ line is drawn in the **knowledge color** (not white), with a soft glow (wide semi-transparent line + sharp thin line on top), always fully opaque.
  - [x] 7.3 Untested atoms (α=1, β=1) show a flat gray gradient with no μ line.
- [x] Task 8: Localization
  - [x] 8.1 All UI strings in `strings.xml` in Swedish: dashboard card, mastery screen title, sort options, legend labels, empty states, toggle label.
- [x] Task 9: Testing
  - [x] 9.1 `GetMasteryDataUseCaseTest`: verifies global aggregate math, atom display name formatting, and correct behavior with no DB data.

## Dev Notes

- **Architecture Compliance:**
  - Strict MVVM: `HomeViewModel` holds state, `HomeScreen` observes via `collectAsStateWithLifecycle()`.
  - The aggregation of `atom_states` is domain logic. Define pure Kotlin use cases; no Android dependencies.
  - The actual state fetching must be asynchronous using coroutines (`viewModelScope`). Never block the main thread.
  - `MasteryViewModel` uses `combine()` to reactively merge three flows: mastery data, sort option, and show-untested flag.
- **Technical Requirements:**
  - `atom_states` represents a Beta Distribution. $\alpha$ is success weight, $\beta$ is failure weight.
  - The calculated proficiency should reflect the core math of the distribution. Default prior is $\alpha=1.0, \beta=1.0$.
  - All possible atoms must be enumerated from generators via `getAllAtomIds()` — the DB only holds atoms that have been answered at least once.
  - Untested atoms (never in DB) are merged in with baseline prior `(1,1)` so they appear gray in the dashboard.
- **Visualization Details (`BetaHeatmapBar`):**
  - Gradient alpha is **inverted** relative to the PDF: `alpha = 0.6 * (1 - normalizedPdf)`. Tails opaque, peak transparent.
  - μ line: knowledge color, `alpha = 1.0`, drawn with a soft wide glow (`alpha = 0.35f, strokeWidth = 6dp`) and a sharp thin core (`strokeWidth = 2dp`).
  - Untested (α=1, β=1): flat gray gradient, no μ line.
- **Color Levels (getMasteryColor):**
  - Green (`0xFF4CAF50`) at μ ≥ 85%
  - Amber (`0xFFFFC107`) at μ > 50% (in learning)
  - LightGray at μ = exactly 50% (untested prior)
  - Red (`0xFFF44336`) at μ < 50%
- **Library/Frameworks:**
  - 100% Jetpack Compose for the UI. No XML. Use Compose BOM pin for versions.
- **Previous Story Intelligence:**
  - Story 7.11 and 7.12 set up the `KnowledgeEngine` and the underlying Room database updates.
  - Story 7.13 introduced the natural-sort `idComparator` logic — reused here for `NATURAL_ORDER` sorting.
  - Story 7.14 is for lifetime global mastery, thus requires database reads alongside full atom enumeration from generators.

### Project Structure Notes

- `app/src/main/java/com/siffermastare/domain/generators/NumberGenerator.kt` [MODIFY — added `getAllAtomIds()`]
- `app/src/main/java/com/siffermastare/domain/generators/*.kt` [MODIFY — all 10 generators implement `getAllAtomIds()`]
- `app/src/main/java/com/siffermastare/SiffermastareApplication.kt` [MODIFY — added `allGenerators` lazy property]
- `app/src/main/java/com/siffermastare/domain/usecases/GetMasteryDataUseCase.kt` [MODIFY]
- `app/src/main/java/com/siffermastare/ui/home/HomeViewModel.kt` [MODIFY]
- `app/src/main/java/com/siffermastare/ui/home/HomeScreen.kt` [MODIFY]
- `app/src/main/java/com/siffermastare/ui/mastery/MasteryScreen.kt` [MODIFY]
- `app/src/main/java/com/siffermastare/ui/mastery/MasteryViewModel.kt` [MODIFY]
- `app/src/main/java/com/siffermastare/ui/components/BetaHeatmapBar.kt` [MODIFY]
- `app/src/main/res/values/strings.xml` [MODIFY]
- `app/src/main/java/com/siffermastare/ui/navigation/Screen.kt` [MODIFY]
- `app/src/main/java/com/siffermastare/MainActivity.kt` [MODIFY]
- `app/src/test/java/com/siffermastare/domain/usecases/GetMasteryDataUseCaseTest.kt` [MODIFY]


## Dev Agent Record

### Agent Model Used

Antigravity (Google DeepMind)

### Completion Notes List

- ✅ Implemented Global Mastery and individual atom Beta Curve Heatmap Bar. Added `GetMasteryDataUseCase` and `MasteryScreen`. Updated `HomeViewModel`. Tests pass.
- ✅ Added full atom enumeration from generators via `getAllAtomIds()`. Dashboard now shows all possible atoms including untested (gray) ones.
- ✅ Added sorting (natural/ascending/descending by μ) with dropdown in Mastery Screen.
- ✅ Added "Visa otestade" checkbox to toggle visibility of untested atoms.
- ✅ Redesigned Home Screen stats into a single outlined clickable card.
- ✅ Redesigned `BetaHeatmapBar`: inverted PDF gradient (tails opaque, peak transparent), knowledge-colored μ line with soft glow.
- ✅ Color thresholds: Green ≥ 85%, Amber > 50%, Gray = 50% (prior), Red < 50%.
- ✅ All UI strings in Swedish.

### File List

- docs/sprint-artifacts/7-14-knowledge-dashboard.md [MODIFY]
- docs/epics.md [MODIFY]
- docs/sprint-artifacts/sprint-status.yaml [MODIFY]
- app/src/main/java/com/siffermastare/SiffermastareApplication.kt [MODIFY]
- app/src/main/java/com/siffermastare/data/database/AtomStateDao.kt [MODIFY]
- app/src/test/java/com/siffermastare/testdoubles/FakeAtomStateDao.kt [MODIFY]
- app/src/main/java/com/siffermastare/data/repository/KnowledgeRepository.kt [MODIFY]
- app/src/test/java/com/siffermastare/testdoubles/FakeKnowledgeRepository.kt [MODIFY]
- app/src/main/java/com/siffermastare/data/repository/RoomKnowledgeRepository.kt [MODIFY]
- app/src/main/java/com/siffermastare/domain/generators/NumberGenerator.kt [MODIFY]
- app/src/main/java/com/siffermastare/domain/generators/CardinalGenerator.kt [MODIFY]
- app/src/main/java/com/siffermastare/domain/generators/OrdinalGenerator.kt [MODIFY]
- app/src/main/java/com/siffermastare/domain/generators/TrickyPairsGenerator.kt [MODIFY]
- app/src/main/java/com/siffermastare/domain/generators/TimeGenerator.kt [MODIFY]
- app/src/main/java/com/siffermastare/domain/generators/InformalTimeGenerator.kt [MODIFY]
- app/src/main/java/com/siffermastare/domain/generators/FractionsGenerator.kt [MODIFY]
- app/src/main/java/com/siffermastare/domain/generators/DecimalsGenerator.kt [MODIFY]
- app/src/main/java/com/siffermastare/domain/generators/PhoneNumberGenerator.kt [MODIFY]
- app/src/main/java/com/siffermastare/domain/model/AtomMastery.kt [NEW]
- app/src/main/java/com/siffermastare/domain/model/MasteryData.kt [NEW]
- app/src/main/java/com/siffermastare/domain/usecases/GetMasteryDataUseCase.kt [NEW]
- app/src/main/java/com/siffermastare/ui/home/HomeViewModel.kt [MODIFY]
- app/src/main/java/com/siffermastare/ui/home/HomeScreen.kt [MODIFY]
- app/src/main/java/com/siffermastare/ui/components/BetaHeatmapBar.kt [NEW]
- app/src/main/java/com/siffermastare/ui/mastery/MasteryScreen.kt [NEW]
- app/src/main/java/com/siffermastare/ui/mastery/MasteryViewModel.kt [NEW]
- app/src/main/java/com/siffermastare/ui/navigation/Screen.kt [MODIFY]
- app/src/main/java/com/siffermastare/MainActivity.kt [MODIFY]
- app/src/main/res/values/strings.xml [MODIFY]
- app/src/test/java/com/siffermastare/domain/usecases/GetMasteryDataUseCaseTest.kt [MODIFY]
- app/src/test/java/com/siffermastare/ui/home/HomeViewModelTest.kt [MODIFY]

