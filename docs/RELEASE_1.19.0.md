# Drona Salvage 1.19.0 — release preparation

Package: `com.orbitsalvagers.droneclicker`  
Version: `1.19.0`, code `38`

## Privacy files

Publish `docs/privacy-policy.html` at a public HTTPS address and enter that
address in Play Console. The file contains all three supported languages and
the existing developer contact, Telegram @AlexFitlin. Plain text copies are
`privacy-policy-ru.txt`, `privacy-policy-en.txt`, and `privacy-policy-es.txt`.
The same text is shown inside the application. Version 3 requests agreement
again from users who accepted an older policy. This does not delete progress.

Google Play requires an active public policy URL, even for apps without personal
data collection. A local file is ready for hosting but is not a public URL:
https://support.google.com/googleplay/android-developer/answer/10144311

## Data safety for this build

The source manifest requests VIBRATE; there is no INTERNET permission. The merged
release manifest also contains AndroidX's app-specific signature permission for
non-exported dynamic receivers. This grants no network or personal-data access. Dependencies
contain AndroidX, Compose and Kotlin, with no advertising, analytics or billing
SDK. Game saves, settings, consent and input-protection metadata stay locally.
Android backup/transfer may copy saves and settings; consent and input protection
are excluded. Telegram is an external link that the player chooses to open.

On this implementation, no user data is transmitted by the game to the developer
or third-party servers. Reconfirm this against the final merged release manifest
before filling in Data safety. In the listing, declare no ads and no real-money
in-app purchases. Set target audience and content rating from the intended
audience and actual game content; do not infer them from the absence of ads.

## Audio provenance

`docs/audio/README.md` maps the shipped files to their original sources.
`docs/audio/licenses/` contains original licences and source snapshots;
`docs/audio/sha256.csv` identifies the bundled bytes. All newly connected audio
is CC0 and requires no attribution.

## Release validation

On 3 October 2026, `testDebugUnitTest` passed all 168 tests with zero failures
or errors. All 11 bundled Ogg headers and all three localized policy XML files
were validated. No phone or emulator was connected during preparation, so
device playback and interaction checks remain to be performed.

`lintDebug`, release vital lint, `bundleRelease`, and `assembleRelease` completed
successfully. The signed APK and AAB identify version 1.19.0 / code 38.
APK signature verification passed. All 11 shipped audio assets were matched by
SHA-256 inside the optimized APK. Debug lint reports 0 errors and 135 warnings;
warnings remain for existing artwork and other project code.

Run unit tests, Android lint, and the signed release build. On a device, check
rapid taps, salvage collection, drone taps, all three case tiers, event results,
prestige, planet unlock, the sound toggle, backgrounding, and resuming. Check
first-run policy agreement, withdrawal, and progress restoration. Device audio
quality and actual interaction flow require device testing.

Signed AAB output: `app/build/outputs/bundle/release/app-release.aab`.
Signed APK output: `app/build/outputs/apk/release/app-release.apk`.
The signing credentials remain in local ignored files. This preparation does
not upload the app to Google Play or publish the policy.
