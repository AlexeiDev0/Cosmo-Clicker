# Drona Salvage — final audit report

Date: 2026-09-03  
Branch: `codex/drona-audit-redesign-20260902`  
Rollback point: `33b8d6a` (`chore: capture pre-audit baseline`)

## Confirmed findings

### CRITICAL

No confirmed critical issue remained after reproduction and regression testing.

### HIGH — fixed

1. A queued asynchronous save could write an older snapshot after a newer state and
   lose progress. `StateSaveCoordinator` now serializes writes while capturing the
   latest state; regression coverage verifies the ordering.
2. A displayed case reward could be collected twice and advance a bundle more than
   once. Reward collection is now a guarded state transition in `CaseController`.
3. Planet purchase accepted a non-next planet when its other gates were satisfied,
   allowing progression to be skipped. `GameRules.purchaseOrSelectPlanet` now also
   enforces route order.
4. Daily rewards could be replayed by moving the wall clock backwards. The stored
   claim day is now monotonic and a regression test covers rollback.
5. Prestige discarded the daily reward claim/streak fields. Those additive
   progression fields now survive prestige and are covered by a regression test.
6. Active and pending multi-stage events were not fully persisted, so process death
   could lose progress or allow inconsistent resolution. `EventStateCodec` now
   validates and restores the complete event state, including malformed-save
   handling.
7. Offline progress could be credited again after lifecycle restoration or process
   interruption. The time window is acknowledged before presentation, zero/invalid
   results are idempotent, and foreground application is saved immediately.
8. Daily/weekly quests could regenerate after clock rollback. Calendar keys are now
   monotonic and completed cycles are retained until time moves forward.

### MEDIUM — fixed

1. Invalid persisted upgrade/fleet levels could escape supported bounds and distort
   income/cost calculations. Restore and controller paths now clamp or reject them.
2. Timed event data with non-finite rewards or malformed fields could poison state.
   The codec rejects corrupt payloads safely.
3. Background music could resume despite the current sound preference during a
   lifecycle transition. Resume now checks the current setting.

### LOW / POTENTIAL

- Device-only layout, frame pacing, low-memory image decoding, large-font behavior,
  and OEM lifecycle behavior remain device checks, not code-confirmed defects.
- No speculative issue was changed without a reproducible code path.

## Regression coverage added

Coverage was added for duplicate case collection, serialized latest-state saves,
invalid economy levels, route-order purchase gates, prestige preservation, daily
reward clock rollback, active/pending event round trips and corrupt data, idempotent
offline credit, and daily/weekly quest clock rollback.

Current JVM suite: **143 tests, passed**.

## Visual redesign and assets

The baseline already contains the completed 1.18.2 visual redesign: a unified cosmic
UI system; gameplay, shop, hangar, event, case, statistics, settings, prestige,
quests, and achievements scenes; 39 planet assets; complete drone and case families;
navigation/status icons; localized dialogs and state visuals. This audit did not
replace those verified project assets again and did not alter economy, reward tables,
planet IDs/order, drone rarity/traits/income, event identities, or save keys except
for additive backward-compatible persistence fields.

Old assets were not deleted during this finishing pass. Existing cleanup remains
recoverable from Git history.

## Verification

- `testDebugUnitTest`: **passed** (143 tests).
- `lintDebug`: **passed**.
- `assembleDebug`: **passed**; APK generated at
  `app/build/outputs/apk/debug/app-debug.apk` (92,451,480 bytes).
- EN/RU/ES: **544 keys each**, no missing or extra keys.
- Resource merge/package succeeded, confirming drawable references used by debug.
- Connected Android tests were not run because no connected emulator/device was
  available in this environment.

## Rollback

All audit fixes are isolated after baseline commit `33b8d6a`. The main branch was not
modified. The complete finishing pass can be reviewed or reverted commit-by-commit:

- `fd429f2` — progression and save protection
- `26fb531` — gated progression rewards
- `e1731d3` — timed progress lifecycle restoration
- `2ef3fad` — timed lifecycle hardening

