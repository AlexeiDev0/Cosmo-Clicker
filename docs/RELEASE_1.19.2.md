# Drona Salvage 1.19.2 — simplified artwork and privacy links

Package `com.orbitsalvagers.droneclicker`, version code 40.

All 29 drones use new industrial vector designs with fewer decorative elements,
metallic shading, a clear optic and working attachments. All three case tiers
use a matching cargo-container design with eight opening frames. Detailed
salvage items and bundled sounds from the previous release are retained.

The blue underlined privacy-policy link appears in Settings, on the start screen,
and on the first-run consent screen. It opens the full localized policy in the
app. Opening the link does not grant consent or start gameplay. The first-run
dialog only offers reading and closing; consent remains a separate checkbox
and button. Accepted players can also withdraw through the policy dialog.

The policy identifies Alexei Fitlin and Telegram @AlexFitlin, using the identity
and contact already present in the game. It describes local saves, settings,
anti-autoclick processing, Android backups, deletion, local audio and support.
The app text matches `docs/privacy-policy.html` and the three text exports.
There is no invented public URL: the HTML file still needs public hosting for
the Play Console privacy-policy field.

Artwork source: `tools/Build-BalancedSalvage.ps1` and
`docs/visual/balanced-salvage.json`; export with `tools/Export-FlatArtwork.ps1`.
Preview: `docs/visual/balanced-salvage.html`.
Checks completed before building: 53 valid vector files and 29 distinct drone
designs. On-device interaction and playback require a connected phone/emulator.
The new artwork was visually inspected through a rendered review page.
All 168 unit tests passed with zero failures or errors after the UI changes.

Release outputs: `app/build/outputs/apk/release/app-release.apk` and
`app/build/outputs/bundle/release/app-release.aab`.

`testDebugUnitTest lintDebug bundleRelease assembleRelease` completed successfully.
APK metadata confirms 1.19.2 / code 40; signature verification passed and all
11 bundled audio files remain in the APK. Localized app policy text matches the
English, Russian and Spanish text exports.
