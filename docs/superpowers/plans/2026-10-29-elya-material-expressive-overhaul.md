# Elya Material 3 Expressive Overhaul — Implementation Plan

> **Workflow:** Superpowers execution + test-first changes, with Dyslex.ai task sequencing and accessibility review. Context7 was used to verify current Compose patterns.

**Goal:** Implement the approved 21-slide Elya redesign and address avoidable UI work that contributes to lag, without changing saved cycle/pill data or prediction semantics.

**Architecture:** Keep the existing Jetpack Compose + Material 3 application, navigation destinations, Room schema and ViewModel. Reuse the app's current dialogs, calendar engine and chart components while introducing a shared date index, one memoized Insights analytics projection, accessible navigation, lazy histories and theme-token consistency.

**Tech Stack:** Kotlin, Jetpack Compose, Material 3, Navigation Compose, Room, Coroutines, GitHub Actions.

**Spec:** Approved proposal: `Elya_Design_Overhaul_Proposal.pdf` (21 slides). Shared brand system: `app/src/main/java/com/ben/periodt/ui/theme/Apptheme.kt`.

## Global Constraints

- Keep the existing Room schema, cycle prediction algorithm, post-pill state rules and pill pack date rules unchanged.
- Keep data local, retain export/import behavior, reminders, widget refreshes, privacy copy, Periodt attribution and GPL-3.0 notices.
- Palette: Ivory `#FFF8F5`, Paper `#FFFCFA`, Blush `#F4DFE7`, Rose `#8F405F`, Rose Light `#E7A4BC`, Plum `#613248`, Ink `#321B28`, Muted `#795C6B`, Border `#E9D8E0`; retain the established dark palette.
- Preserve existing route IDs (`calendar`, `pill`, `overview`, `settings`); present the three primary routes as Cycle / Pills / Insights.
- Every visible action must invoke real behavior; no fake sample health values or inert action buttons.
- Completion gate: current branch build must pass `testDebugUnitTest`, `assembleDebug` and artifact upload. Retrieve the APK, confirm size/type/checksum, and distinguish those checks from unavailable on-device performance profiling.

## Review Focus

1. Calendar month bounds, adjacent-day range highlights, and logged-period / pill / prediction precedence.
2. No history, a single cycle, daily-log overrides and expired predictions.
3. Discovery, Learning, Normal and active pill pack modes.
4. Compact widths, larger font settings, readable contrasts, selected navigation semantics and touch targets.
5. Long cycle/pill history and repeated daily-log/chart calculations.

---

## Task DYX-001 — Restore and verify the build gate

**Goal:** Keep Kotlin compilation healthy before screen work expands.

**Files:** `ui/calendar/CalendarScreen.kt`, build workflow.

**Action:** Restore the missing opening brace in `CalendarScreen`, inspect the parser errors, and run the CI test/build pipeline.

**Why:** The initial calendar patch broke parsing, so subsequent work must not rest on an unverified source state.

**Verification:** The fix was committed and a later workflow passed unit tests, `assembleDebug` and upload. Additional current-head verification is recorded below.

**Expected result:** No declaration/parser errors; APK assembly completes.

- [x] Restore the function body and diagnose the parser failure.
- [x] Verify a complete test/build/artifact workflow before extending the screens.

## Task DYX-002 — Cycle home, calendar and date-cell performance

**Goal:** Implement the new Cycle entry point and remove repeated calendar-cell data scans.

**Files:** `ui/MainScreen.kt`, `ui/calendar/CalendarScreen.kt`, `app/src/test/java/com/ben/periodt/uiux/shared/CalendarDateIndexTests.kt`.

**Action:** Add the Cycle hero with honest prediction-state text and a connected Log action; label navigation; index phase, cycle and pill data for the calendar's full month-navigation range; cache per-cycle daily-log counts; collect pill-pack state once.

**Why:** The previous day cell searched cycles and packs separately for the current, previous and next date. History rows also rescanned all daily logs.

**Verification:** Six date-index tests cover overlap priority, pill priority, the exclusive prediction end, paused fertile-window states, adjacent-day range highlights, and both calendar spillover bounds.

**Expected result:** Calendar dates use constant-time map lookups when composing cells; saved cycle and pill rules remain unchanged.

- [x] Add the Cycle hero and connect it to existing log/add-cycle behavior.
- [x] Add and memoize a bounded day index with correct navigation bounds.
- [x] Cache history log counts and remove nested StateFlow collection.
- [x] Add date-boundary and phase-precedence regression coverage.
- [x] Include the full neighboring calendar grid plus adjacent-day phase checks at both scroll-range edges.

## Task DYX-003 — Insights hierarchy and chart performance

**Goal:** Make Insights a clear, readable summary and eliminate redundant analytics passes.

**Files:** `ui/overview/OverviewScreen.kt`, `app/src/test/java/com/ben/periodt/uiux/shared/InsightsSeriesTests.kt`.

**Action:** Add an Insights heading and helpful empty state; use semantic MaterialTheme tokens for cards and labels; remove an unused recent-trends calculation; derive bleeding, pain and blood-color data in one memoized chronological pass.

**Why:** The hidden recent-trends block did work even though its display was commented out. The three charts each independently walked the full cycle history.

**Verification:** Three tests cover empty history, per-day overrides and chronological counts; chart empty states and scrolling are retained.

**Expected result:** All three charts use the same memoized source projection, without changing historical values.

- [x] Add the Insights heading, subtitle and shared surface/text tokens.
- [x] Remove the unused recent-trends calculation.
- [x] Consolidate the three chart projections into one memoized pass.
- [x] Add tests for empty results, daily overrides and chronological order.

## Task DYX-004 — Pills screen and history

**Goal:** Make pack setup and state changes explicit, and scale better with long histories.

**Files:** `ui/pill/PillTrackerScreen.kt`, `ui/MainScreen.kt`.

**Action:** Use a keyed LazyColumn for the screen and pack history; provide an in-content setup action connected to the existing setup dialog; apply theme tokens; ask for confirmation before stopping the active pack.

**Why:** A normal `Column` composed every history row, and pill setup was largely dependent on the contextual bottom action.

**Verification:** The active pack still uses the existing ViewModel; stop confirmation calls the original `stopPillTracking()` only on confirm and clearly states that history is retained.

**Expected result:** History rows are composed lazily, setup is discoverable, and stopping is deliberate.

- [x] Convert pack history to a stable-key lazy list.
- [x] Add and wire the setup call-to-action.
- [x] Add an explicit confirmation for the pack-stop state change.

## Task DYX-005 — Onboarding, Settings and shared surfaces

**Goal:** Make the whole app feel like the same Elya product instead of a branded home surrounded by upstream styles.

**Files:** `ui/onboarding/OnboardingWidgets.kt`, `ui/overview/SettingsScreen.kt`, `ui/profiles/ProfileComponents.kt`, `ui/overview/FeedbackComponents.kt`, `ui/calendar/CalendarScreen.kt`, `ui/calendar/AddCycleDialog.kt`, `ui/pill/PillTrackingSetupDialog.kt`, `reminder/RemindersDialog.kt`, theme components.

**Action:** Apply semantic Material 3 colors to onboarding actions, Settings, profile/feedback dialogs, calendar/day-log sheets, cycle-log sheets, pill setup and reminders. Pair selected options with contrasting on-container text and radio-option semantics. Keep the icon pattern but stop the infinite full-screen canvas redraw. Retain privacy, backups, reminders, appearance selection, import/export and profile operations.

**Why:** Continuous decorative redraws cost work with no interaction value, and hardcoded upstream palettes made dialogs and settings visually inconsistent.

**Verification:** Full app compile and existing functions/tests. Theme preference still resolves through the existing light/dark/system setting.

**Expected result:** Elya's ivory/blush/rose/plum and dark palettes are consistent across primary screens and sheets without changing business behavior.

- [x] Remove infinite decorative canvas animation while retaining the visual pattern.
- [x] Use MaterialTheme tokens across Settings, onboarding, profile/feedback, calendar/day-log, cycle-log, pill-setup and reminder sheets.
- [x] Pair selected status chips with suitable foreground colors and expose selected radio-option semantics.
- [x] Preserve the existing settings, backup, reminder and profile actions.

## Task DYX-006 — Accessibility, final verification and artifact

**Goal:** Verify the complete branch and deliver an actual APK.

**Files:** custom navigation, touched screens, regression tests, CI artifact.

**Action:** Mark the selected tab semantically, check the full PR diff, run current CI, retrieve the uploaded APK and record its size/checksum. Review current Compose APIs with Context7, and apply Dyslex.ai accessibility/task-sequencing guidance without weakening Superpowers test/verification gates.

**Verification:** The current application-code commit `46910115e448294af17da5c7f3a99ed90d34576c` passed workflow `40890460167`: `testDebugUnitTest`, `assembleDebug`, and APK upload all succeeded. Artifact `12903679078` was retrieved and inspected; `app-debug.apk` is 53,006,044 bytes with SHA-256 `eda5ca5ab2a2d41806b34a795aac5209cc7e03f4cb13ef040be135d7550f275a`. The artifact ZIP is 41,460,448 bytes with SHA-256 `a117d11682c87f3ec768c9e32862cecb4196e0f2b2b76f0e1ca2f3879b10292b`. `file` identifies the APK as an Android package and `unzip -t` reports no archive errors. The calendar parser issue and Insights downsampling duplicate/endpoint-test issues found during CI were corrected; the current run passes. A device profiler / physical handset is not available here, so no numeric FPS or real-device responsiveness claim is made.

**Expected result:** Reviewable PR and a non-empty debug APK whose build, tests and artifact existance have been verified.

- [x] Add TalkBack selected-state semantics to primary navigation.
- [x] Use current Context7 documentation and review accessibility labels/empty states.
- [x] Verify the application-code build, tests and artifact.
- [ ] Run on-device profile / visual smoke test when an Android device or emulator is available.

---

## Implementation Ledger

- **Ruling:** Keep work on `feat/elya-brand-system`, not `main`, so the redesign remains reviewable and rollback-safe.
- **Ruling:** Remove Haze from the main screen because the old Haze state had no `hazeChild` surfaces; it added a rendering layer without contributing a visible blur.
- **Measured by source inspection:** Calendar repeated per-cell cycle/pill scans were replaced with an indexed date lookup that includes the neighboring grid weeks; history daily-log counts are precomputed; Insights charts share one derived data pass; Pill history is lazy; onboarding's perpetual canvas transition is static; setup/logging/reminder sheets share theme tokens.
- **Final verification:** Application-code commit `4916d628e95ff12349378d262adf946bef3c9075` passed GitHub Actions run `40890728650`: `testDebugUnitTest`, `assembleDebug`, and APK upload all succeeded. Artifact `12905185568` was retrieved from that exact run. Extracted APK: 53,006,044 bytes; SHA-256 `4b8085eafaec90e9a633a48841e2b983a1b8af1f3ef9865e86997d9d6f05a2fd`. Artifact ZIP: 41,463,661 bytes; SHA-256 `d260658492980c8a5729aba0ec15b52aff4fc3b24b284ebd0100342b22833060`. `file` identifies the extracted file as an Android package, and `unzip -t` reports no archive errors. The branch tip after this build contains only documentation updates; application source is unchanged from the verified SHA.
- **Known limitation:** No physical-device/Emulator frame trace or pixel-level screenshot run was available. The lag-related source hotspots were addressed, but a measured frames-per-second gain cannot be claimed.
- **Next action:** Update the PR title/body to reflect the full implementation; keep the PR open as a draft for review. No further application source changes are planned unless current-head verification exposes a failure.
