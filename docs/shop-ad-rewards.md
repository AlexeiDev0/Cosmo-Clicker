# Shop rewards and simple scrap — 11 October 2026

The shop Rewards tab offers a common case for three confirmed rewarded views and debris for one confirmed rewarded view. Case progress is saved across restarts, and each view is started separately. No currency is spent and earned cases do not increase paid case prices. If another case reward is visible, the earned case waits until it can be opened.

Debris rewards start at 5,000. The quote is the rounded maximum of 5,000, the existing planet-scaled 5,000 reward for the highest owned planet, and 60 seconds of current fleet passive income without temporary ad/trade effects. The displayed amount is fixed before loading the ad; changes during loading do not change that reward. Duplicate and stale reward callbacks cannot credit the same offer twice. Rewards and case progress use synchronous saving with rollback if saving fails.

A single tap on an explicitly labelled advertising button permits requests, loads the ad and shows it when ready while the screen is active. The separate game advertising confirmation was removed; Google UMP remains in the production flow. Loading failures, cancellation and closing without the SDK reward callback give no credit. Debug still uses test ads. Production ad serving and device behavior require a device/account check.

Policy version 9 and local EN/RU/ES policy exports describe the new behavior. The public policy site needs the updated exported text before distribution.

All 48 debris vectors use flat colors and simple details. Common salvage includes bolts, nuts, washers, bent pipes, screws, metal offcuts and gears. Stable collection IDs and rarity pools are preserved. Regenerate with `tools/Build-SimpleScrap.ps1`; see `simple-scrap-preview.svg` for the contact sheet.
