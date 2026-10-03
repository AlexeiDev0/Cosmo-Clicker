# Drona Salvage 1.19.1 — artwork update

Package `com.orbitsalvagers.droneclicker`, version code 39.

The play area uses all 28 original detailed debris images above the planet;
the drones' collection mechanics remain connected to the same target IDs.
Returning drones display the collected item from their cargo ID, with a crate
fallback for cargo that has no debris ID.
All 29 drones use the original detailed painted sprites with armour, optics,
rotors and claws instead of simplified vector replacements. Engine glow uses the
existing shared animation phase and stays steady with reduced motion.
Atlas thumbnails use cropped source cells instead of oversized translated images.

The three case tiers use their original detailed painted artwork and retain eight
opening frames each. Reveal framing and glow follow the tier's colour.
The 14 main control icons now use metallic vector panels consistently across
navigation and settings.

Audio and privacy files from 1.19.0 remain included. The policy must still be
hosted publicly before entering its URL in Play Console. Device playback and
visual checks require a connected phone or emulator.

Source generation: `tools/Build-ColorfulDesign.ps1`,
`tools/Export-FlatArtwork.ps1`, and `tools/Update-ControlIcons.ps1`.
The vector review pages remain available for fallback artwork. The active painted
artwork is shown in `docs/visual/painted-salvage.html`.

Outputs after a successful build:
`app/build/outputs/apk/release/app-release.apk`,
`app/build/outputs/bundle/release/app-release.aab`.

Validation: all 168 unit tests passed with zero failures/errors. All 81 detailed
sprite paths in the review page and 67 vector resources were checked. The vector
fallback artwork was also inspected through rendered review pages. No connected
phone or emulator was available for on-device artwork and playback verification.

The full `testDebugUnitTest lintDebug bundleRelease assembleRelease` run completed
successfully. APK metadata confirms 1.19.1 / code 39. APK signature verification
passed, and all 11 CC0 audio files were matched by SHA-256 inside the final APK.
