# Space Salvage: minimal fleet and salvage expansion

The fleet now uses 29 new vector designs with flat colors, simple outlines and distinct body/sensor combinations. The 28 existing salvage types were redrawn in the same style and 20 new discovery IDs (29–48) were added. The collection lock and cargo crate also use flat vectors. No gradients, material textures or scratches are used in these assets.

Artwork is stored in `app/src/main/res/drawable/minimal_drone_*_v5.xml`, `minimal_debris_*_v7.xml`, `minimal_collection_lock_v5.xml` and `minimal_cargo_v5.xml`. Regenerate it with `tools/Build-MinimalArtwork.ps1`. Browse `docs/minimal-artwork-preview.html` to inspect every design.

`DebrisCatalog` supplies one shared range and rarity assignment for spawning, the collection, save decoding and quests. Existing discovery IDs, rarity odds, reward values and achievement IDs remain stable. New salvage is available through the existing rarity pools. The collection and galactic collection progress now count 48 types. Names are supplied in Russian, English and Spanish.

Regression checks cover discovery decoding, malformed/out-of-range IDs, old rarity assignments, complete spawn coverage and distinct drawable resources. Final tests, lint and APK assembly are run with Gradle.

Detailed store descriptions are in `play-store-listing/store-description-ru.txt`, `store-description-en.txt` and `store-description-es.txt`. Their full description bodies are below 4,000 characters each and their short descriptions are below 80 characters.

The descriptions use the implemented game features: 29 drone models, 48 salvage types, 39 planets, station modules, events, quests, prestige, local saves and optional rewarded ads. The structure was informed by the official Google Play descriptions of [Idle Planet Miner](https://play.google.com/store/apps/details?id=com.TironiumTech.IdlePlanetMiner) and [Idle Space Company](https://play.google.com/store/apps/details?id=com.blingblinggames.idlespacecompany). The copy is original.

This update uses native vector artwork rather than image generation because the requested design removes material rendering and extends the project's existing vector system. Earlier bitmap variants remain available for rollback.

## Matching cases and scenes

All three case types now use flat vector artwork, including all eight opening frames per type (24 resources). Their neutral, blue and gold accents match in the shop and opening overlay. Case cards have a flat background and thin border, and the opening backdrop uses a quiet flat fill.

Twelve scenes now use flat navy shapes, sparse stars and geometric details around the margins. The main screen, start screen and settings are connected to these replacements along with the shop, hangar, quests, achievements, statistics, cases, events, prestige and offline scene aliases. Central areas remain clear for text and game content. No bitmap textures or gradient fills are used by the new scenes.

Assets: `app/src/main/res/drawable/minimal_case_*_v5.xml` and `minimal_bg_*_v7.xml`. Regenerate with `tools/Build-MinimalCasesAndScenes.ps1`. Inspect `docs/minimal-cases-scenes-preview.html` or its PNG preview.
