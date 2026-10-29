# Elya scroll-jank and identity cleanup

## Task DYX-001 — lock requested app identity strings with tests
Goal: Ensure the in-app contact address and settings dedication match the requested values exactly.
Files: `app/src/test/java/com/ben/periodt/uiux/shared/ElyaIdentityTest.kt`, shared identity constants, ProfileComponents, SettingsScreen, README, privacy-policy.md.
Action: Add failing tests first, then introduce a single source of truth for app-visible identity values and replace old contact references.
Why: Avoid mismatches between profile contact actions, displayed email and settings footer.
Verification: Observe the test fail before implementation, then run unit tests and the full debug build.

## Task DYX-002 — remove competing calendar collapse animations
Goal: Keep vertical swipes responsive on the Cycle page.
Files: `app/src/main/java/com/ben/periodt/ui/calendar/CalendarScreen.kt`.
Root cause from source inspection: the vertical nested-scroll callback swaps the full month grid for the week grid while simultaneously running an AnimatedContent transition, animateContentSize on both the hero and calendar wrapper, AnimatedVisibility expand/shrink for the legend, and an animated scroll of the second calendar state.
Action: Use non-animated state synchronization (scrollToWeek/scrollToMonth) when switching modes, make the month/week branch switch direct, render the legend conditionally, and remove size animations tied to the same scroll-driven compact state. Leave unrelated dialogs/interactions unchanged.
Verification: Re-run unit tests and debug build; inspect final source diff and exact CI run. A physical-device frame trace is unavailable in this environment, so no numeric FPS claim will be made.

## Task DYX-003 — verify repository identity and build
Action: Search current source/docs for old Gmail and requested dedication; run full GitHub Actions unit tests + assembleDebug; retrieve artifact from exact final source commit and verify APK ZIP integrity, size, type and SHA-256.
Expected result: Updated APK on feature branch with no old Gmail remains in the text source audit and passing CI.
