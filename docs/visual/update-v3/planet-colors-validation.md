# Planet colors and shop fixes, 2026-10-06

The built-in image_gen tool generated bg_shop_calm_v4.png and the softer silver/cyan salvage sheet debris-v5-source.png. Prompts: shop_v4_prompt.txt and debris_v5_prompt.txt. Sprite assets: app/src/main/res/drawable-nodpi/toon_debris_01_v5.png through toon_debris_28_v5.png.

PlanetDebrisPalette.kt defines distinct colors for all 39 destinations. Earth preserves the source colors; magma p9 is red/orange; the ice world p28 is pale blue. Runtime luminance recoloring preserves transparency and shading without creating duplicate sprite sets. Matching colors apply to floating debris, drone cargo and the collection. Three unit tests verify original/fallback colors, palette uniqueness, magma and ice color directions.

Shop purchase buttons now use the same currency resource as the main balance. The generated shop background bypasses the legacy painted-background mapping. Closed case icons use three new native silver vectors with different rarity accents. Each information dialog already uses its own localized description and actual odds for its selected case.

Language fixes: explicitly provide localized LocalResources alongside LocalContext and LocalConfiguration so stringResource resolves the selected language. System language uses the system configuration locale rather than a potentially overridden application default. Device-level language switching still needs verification.
