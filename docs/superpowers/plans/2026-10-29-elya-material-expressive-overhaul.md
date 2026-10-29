# Elya Material 3 Expressive Overhaul — Implementation Plan

> **Execution:** Inline implementation using Superpowers; verify every task in CI before claiming completion.

**Goal:** Implement the approved Elya design overhaul from the 21-slide proposal, improve responsiveness on lower-powered Android devices, and deliver a verified debug APK without changing existing cycle or pill calculation semantics.

**Architecture:** Retain the existing Jetpack Compose + Material 3 app and Room-backed ViewModel. Reuse the current navigation, calendar library, charts, dialogs, and theme preference; improve surfaces and interactions in place. Memoize expensive date/statistics derivations, avoid redundant state collection and oversized rendering layers, and preserve the existing data model.

**Tech Stack:** Kotlin, Jetpack Compose, Material 3, Navigation Compose, Room, Coroutines, GitHub Actions.

**Spec:** Approved proposal: `Elya_Design_Overhaul_Proposal.pdf` (21 slides). Brand-system source: `app/src/main/java/com/ben/periodt/ui/theme/Apptheme.kt`.

## Global Constraints

- Keep the existing Room schema and cycle prediction / pill-state rules unchanged.
- Keep offline-first data handling, export/import behavior, privacy copy, attribution to Periodt, and GPL-3.0 notices.
- Use the Elya token palette: Ivory `#FFF8F5`, Paper `#FFFCFA`, Blush `#F4DFE7`, Rose `#8F405F`, Rose Light `#E7A4BC`, Plum `#613248`, Ink `#321B28`, Muted `#795C6B`, Border `#E9D8E0`; maintain the approved dark palette.
- Preserve the existing Calendar / Pill / Overview route IDs and settings route. Route labels may become Cycle / Pills / Insights.
- Keep controls functional; visual-only buttons and fake data are not acceptable.
- Do not call the app complete until a current CI run passes unit tests, `assembleDebug`, artifact upload, and the downloaded APK has been checked for existence and size.

## Review Focus

1. Calendar date boundaries and period/pill precedence — compare against the prior behavior for overlapping cycle/pill windows and prediction states.
2. No logged cycle / no prediction — present honest empty states and don't invent estimates.
3. Discovery, Learning, Normal, and active-pill modes — maintain their exact current semantics and copy context.
4. Small screens, system font scaling, talkback selection semantics, contrast, and touch targets.
5. Long cycle histories and many daily logs — avoid rendering/computing all data repeatedly on every scroll or calendar-cell recomposition.

---

## Task DYX-001 — Restore green build before extending the branch

**Files:** `app/src/main/java/com/ben/periodt/ui/calendar/CalendarScreen.kt`

**Action:** Preserve the fix restoring the opening brace on the CalendarScreen declaration. Confirm the failed run's parser errors came from this missing brace, then verify the next workflow's unit-test and APK-build steps.

**Verification:** Latest workflow for the feature branch has successful `Run Unit Tests`, `Assemble Debug APK`, and `Upload Debug APK` steps.

**Status:** The brace fix is committed; latest run had passed unit tests and was assembling the APK at plan creation.

## Task DYX-002 — Cycle home, calendar, and calendar performance

**Files:** `ui/MainScreen.kt`, `ui/calendar/CalendarScreen.kt`, targeted calendar tests.

**Action:** Use the approved Cycle hero, visible Cycle / Pills / Insights navigation labels, a clearly labelled contextual action, consistent Material 3 surfaces, responsive compact sizing, and a cached date index for calendar cells. Keep the date log sheet and cycle edit/delete behavior intact. Add regression tests for phase precedence, period boundaries, pill boundaries, fertile-window dates, and dates beyond the current prediction window.

**Verification:** Date-state tests prove the expected old precedence; unit tests pass; UI build is successful. Review for off-by-one errors and date-map coverage of the supported calendar scroll range.

**Status:** Initial implementation committed; regression tests and build review remain.

## Task DYX-003 — Insights screen and chart cost

**Files:** `ui/overview/OverviewScreen.kt`, targeted statistics tests.

**Action:** Add a clear Insights heading and compact summary hierarchy; make metric and chart cards consistent with Elya's surfaces and shape tokens; improve empty states and chart labels. Compute chart series / distributions once per `cycles` + `dailyLogs` change instead of each being derived independently when feasible. Keep charts horizontally scrollable and retain all real recorded data.

**Verification:** Unit tests cover empty data, one cycle, and daily overrides; chart values match existing stored data; navigation and charts compile in both themes.

## Task DYX-004 — Pills screen

**Files:** `ui/pill/PillTrackerScreen.kt`, `ui/MainScreen.kt` if a callback is needed.

**Action:** Improve active-pack and post-pill mode hierarchy; show pack progress with a labelled, accessible indicator; make setup available in the content area as well as the contextual action; render history efficiently while preserving deletion. Add confirmation before stopping pill tracking if the existing stop operation is destructive and can change prediction mode.

**Verification:** Exercise active-pack, no-pack, discovery, learning, normal, and completed-history states; confirm controls call existing ViewModel operations and that data/history survive navigation.

## Task DYX-005 — Onboarding, Settings, and global consistency

**Files:** `ui/onboarding/OnboardingPager.kt`, any separate animated background composable, `ui/overview/SettingsScreen.kt`, `ui/theme/Apptheme.kt`, and shared surface components as needed.

**Action:** Apply Elya palette, MaterialTheme typography, shape, and spacing tokens to onboarding/settings/profile surfaces; retain all help, privacy, backup, appearance, and import/export functions. Reduce unnecessarily continuous or full-screen animation work where inspection identifies it as a lag contributor, without removing the brand's visual character.

**Verification:** Compile all screens, preserve dark/light/system preference behavior, review screen states at compact dimensions and with larger text, and ensure no missing buttons/routes.

## Task DYX-006 — Accessibility, performance, and release gate

**Files:** touched screens, tests, and repository documentation as needed.

**Action:** Add selected-state semantics to custom navigation; verify button labels, contrast, touch target sizes, chart labels and empty states; inspect Compose APIs against current Context7 documentation. Check the full branch diff and review the approved proposal item by item.

**Verification:** Fresh GitHub Actions run passes unit tests and `assembleDebug`, uploaded debug APK is retrievable and non-empty, artifact link is verified, and PR diff contains no unrelated data-model changes. If interactive device profiling is unavailable, report that limitation explicitly rather than claiming measured FPS improvement.

---

## Ledger

- Ruling: implement on `feat/elya-brand-system`, not `main` — keeps the approved overhaul reviewable and rollback-safe — cost if wrong: an extra PR review cycle.
- Ruling: remove the unused Haze rendering layer rather than replacing it with another blur — no `hazeChild` surfaces used the existing Haze state, so the layer added cost without effect — cost if wrong: any subtle blur appearance would be lost; review screenshots.
- Finding: the prior date-cell code repeatedly scanned all cycles and pill packs three times for each calendar date, and repeated daily-log counting for each history row. The indexed implementation targets that CPU work; real-device performance still requires profiling.
- Finding: CI for the initial implementation failed at Kotlin parsing due to a missing `{` after the new `CalendarScreen` function declaration. The brace was restored; verify the fresh workflow before extending this claim.
