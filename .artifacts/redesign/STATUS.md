# Drona Salvage visual replacement

2026-09-11 continuation:
- Finished wiring the existing 28 debris sprites (14 replacements and 14 additions) into collection/quest UI; collection is available in achievements and goals.
- Collection milestones: 10 / 15 / 20 / 28. Daily unique-type goal: 10; weekly: 15. Discoveries persist across prestige; timed quests track their own unique IDs.
- Fixed compilation failure from missing cosmic_navigation/events/controls_atlas_v1 resources. CosmicIconPainter now uses the existing metal-and-light vectors, including aliases for currency, energy, and prestige.
- Updated resource/spawn tests to 28 types; added full-collection reward/progress and generic-counter regression coverage.
- Built-in image generation replaced drone_12_v2.png and drone_13_v2.png (512px, verified transparent alpha) in app/src/main/res/drawable-nodpi. Masters and prompts are retained here. Drone 12 uses its saved prompt plus a background-removal correction; drone 13 has its own prompt file.
- Drones 01–13 now have recorded replacements. Drones 14–29 remain for a later art pass; this continuation covers the requested small additional batch.
- No Android devices/emulators were connected (`adb devices`); on-device tests were unavailable.
- Final unit test run: 157 tests, zero failures (`testDebugUnitTest`, 2026-09-11).
- `assembleDebug` and `lintDebug` succeeded on 2026-09-11. APK: app/build/outputs/apk/debug/app-debug.apk. Lint report: app/build/reports/lint-results-debug.html.

User requested a full redesign and explicitly asked to replace every game image.
Direction is derived from inspected existing art: working salvage drones, unusual colorful planets, navy worn metal, ivory armor, cyan optics, amber industry and violet space.

The original visual inventory is `inventory.json`; active raster inventory is `../../tmp/visual-audit/active-images.json`. Original image backups are in `../../tmp/visual-audit/originals` (the main background backup was made after the first replacement; Git contains its earlier version).

`masters/` contains generated full-resolution files. `prompts/` contains individual generation specifications. `completed.jsonl` records imported resources and SHA256 values. `Import-Art.ps1` only copies and resizes generated images, preserving alpha. Generation uses the built-in image tool.

Previous-session tool cells are no longer resumable. Planet 14 failed once with a reference decode error and must be retried. Verify all inventory entries against completion log, do not infer completion from loop indices.

2026-09-09: Built-in generation resumed successfully. Imported drone_09_v2.png and saved its prompt. There are 35 imported resources (21 planets, 9 drones, 5 backgrounds), with 159 active raster entries remaining in pending.json from the existing inventory. Full replacement is not complete. compileDebugKotlin passed after this import.

Kotlin compilation passed after moving a misplaced existing closing brace in QuestPanel.

2026-09-09: Verification results recovered from local build artifacts after the reward fixes:
- Unit tests: 150 tests, 0 failures, 0 errors (`app/build/test-results/testDebugUnitTest/TEST-*.xml`).
- Debug game APK and Android test APK are present: `app/build/outputs/apk/debug/app-debug.apk` and `app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk` (built at 17:13 and 17:14 respectively).
- Separate debug lint report completed at 17:19:40: 0 errors, 2 warnings (`app/build/reports/lint-results-debug.html`). Both warnings concern available dependency updates: Android Gradle Plugin and Kotlin Compose plugin.
- These artifacts postdate the latest source edit at 17:11:32. Checks were not rerun when recording this result.
- Building the Android test APK does not establish that device/emulator tests passed; their execution remains unverified.
