# Validation, 2026-10-06

Final command: `gradlew.bat testDebugUnitTest --console=plain lintDebug assembleDebug assembleDebugAndroidTest --max-workers=2`.

Result: BUILD SUCCESSFUL. 176 unit tests, zero failures and errors. Main debug APK and instrumentation APK built successfully. Instrumentation compilation initially failed due to an unnecessary assertDoesNotExist import; the import was removed and compilation passed.

Physical phone: Android 13, model 2201116SG, connected using wireless ADB. Original shared preferences were backed up to build/device-validation/20261006-212545/original-preferences.tar. Streaming installation failed with INSTALL_FAILED_USER_RESTRICTED. A subsequent no-streaming installation lost its ADB connection while copying the APK (Broken pipe). The device is now absent from adb devices. No UI tests were executed, no app data was cleared, and the saved progress/settings were not overwritten. Phone validation and installation remain pending reconnection.

Google Play readiness remains conditional on an adult account owner, real production AdMob identifiers (for an advertising release), a freshly signed release AAB, device tests and completed Play Console declarations. The debug APK is for testing, not a Play Store upload.
