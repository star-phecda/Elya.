# Elya approved Material 3 Expressive overhaul — implementation plan

## Authority and constraints
- Authority: approved 21-slide proposal `Elya_Design_Overhaul_Proposal.pptx` / PDF in the conversation.
- Platform: existing native Android app, Kotlin + Jetpack Compose.
- Work only on `feat/elya-brand-system`; do not merge into main.
- Preserve upstream attribution and GPL-3.0.
- Preserve cycle prediction, cycle dates, pill-pack/post-pill modes, daily logs, reminders, profile, and persisted data schema.
- All mock dates/numbers in the deck are illustrative; production values must come from existing state and domain logic.
- A green commit/CI job is not sufficient to claim completion; final APK must be built, artifact retrieved, and verified.

## Visual system
- Light palette: Ivory `#FFF8F5`, Paper `#FFFCFA`, Blush `#F4DFE7`, Rose `#8F405F`, RoseLight `#E7A4BC`, Plum `#613248`, Ink `#321B28`, Muted `#795C6B`, Border `#E9D8E0`.
- Dark palette: Night `#171117`, NightSurface `#2A1C26`, NightVariant `#3C2836`, NightText `#FFF2F7`, NightMuted `#D4BAC7`.
- Prefer large rounded hero surfaces, 22–30dp corners for major cards, 16–20dp for controls, content-led hierarchy, compact but readable labels, accessible contrast, selected-state motion, and reduced unnecessary blur/effects.
- Preserve responsive layouts at narrow phone widths and system font scaling.

## Task ledger and execution order

### Task 0 — restore a green build
Files: `ui/calendar/CalendarScreen.kt`, `ui/MainScreen.kt`.
- Fix the missing function-body brace exposed by CI.
- Run unit tests and assemble debug APK before stacking further changes.
- Inspect compiler output, not only the Actions status.

### Task 1 — calendar performance and correctness
Files: `ui/calendar/CalendarScreen.kt`, calendar-related tests.
- Compute a date index once per relevant state update for visible calendar range, not per cell.
- Remove repeated per-cell searches across cycles/pill packs and repeated daily-log counting.
- Avoid duplicate StateFlow collection in composition.
- Preserve period ranges, predicted period/fertile days, ovulation marker, pill-pack shading, today outline, per-day logging, and date navigation.
- Add unit tests for period ranges, predicted phases, pill packs, and range boundaries; verify cached dates cover the full month/week viewport.

### Task 2 — Cycle home and navigation
Files: `ui/MainScreen.kt`, `ui/calendar/CalendarScreen.kt`.
- Label navigation as Cycle / Pills / Insights, retain profile access.
- Add the Cycle hero card: cycle day or contextual status, next estimate, appropriately cautious confidence language, and Log today.
- Log today opens the existing log flow when a cycle covers today; otherwise opens the existing add-period flow.
- Use the theme's background rather than expensive redundant full-screen haze/gradient layers.
- Ensure button labels are not clipped on narrow devices.

### Task 3 — Insights
Files: `ui/overview/OverviewScreen.kt`.
- Use a clear title and explanatory subtitle, compact average-cycle/period/recorded-cycle stats, a single prominent upcoming estimate, and readable bleeding/pain/blood-colour charts.
- Preserve data calculation functions and disclaimers. Avoid claiming medical certainty.
- Improve card rhythm, axis labels, empty states, and dark-theme readability.

### Task 4 — Pills
Files: `ui/pill/PillTrackerScreen.kt`.
- Make active pack state, progress, next relevant date, post-pill state, and history visually distinct.
- Preserve pack setup, pill count, start/stop dates, and post-pill learning/discovery behavior.
- Use clear, labelled actions and no decorative-only controls.

### Task 5 — onboarding
Files: `ui/onboarding/OnboardingPager.kt` and existing onboarding components.
- Apply the approved brand system consistently to welcome, setup, and cycle-configuration pages.
- Keep all existing onboarding fields, validation, skip/back/continue behavior, and persistence.
- Improve hierarchy and step feedback without changing the underlying data model.

### Task 6 — settings/profile/reminders
Files: `ui/overview/SettingsScreen.kt`, profile components, reminder flow.
- Group settings into clear labelled sections, unify icons/surfaces, preserve import/export/privacy options, reminders, theme selection, profile editing, and dialogs.
- Keep destructive actions explicit and confirmed.
- Do not remove any existing setting.

### Task 7 — system-wide finish
Files: theme/token files, shared components, any touched screens.
- Verify typography, shapes, spacing, focus/pressed/disabled/selected states, dark mode, contrast, and system font scaling.
- Reduce costly blur/shadows/overdraw where unnecessary.
- Add accessibility labels to icon-only actions and check touch targets.

### Task 8 — final verification and delivery
- Run full unit test suite and debug build on final head.
- Retrieve the APK artifact from that exact commit and verify artifact existence/hash.
- Review changed files and diff against this plan.
- Check CI result and APK package metadata; report any checks that cannot be run (e.g., physical-device visual test) honestly.
- Do not merge without separate user approval.

## Definition of done
- All tasks above completed or explicitly listed as blocked with evidence.
- No broken Kotlin compilation; unit tests and `assembleDebug` pass on the final commit.
- Debug APK artifact is available from the final commit.
- Existing business logic and persisted data are preserved.
- Final review of every changed file is complete; no critical/important review findings remain.
