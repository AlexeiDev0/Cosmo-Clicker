# Visual update, 2026-10-06

Generated with the built-in image_gen tool. Original generated PNGs are preserved in this directory. The game uses background_cosmic_v5.png, launcher foreground v3, 29 toon_drone sprites and 28 toon_debris sprites.

Prompts: background_v5_prompt.txt, drones_v3_prompt.txt, debris_v3_prompt.txt and launcher_prompt.txt. Sprite extraction and launcher density exports use tools/Import-VisualUpdate.ps1 with System.Drawing. The importer chooses low-alpha gutters near grid boundaries to avoid cropping drone wings. Generation was performed by image_gen; the importer only packages the resulting assets.

Changes: planet/drone launcher icon, blue/violet portrait nebula background, stylized drones and salvage objects. Existing resource mappings preserve drone identities and game balance.

Follow-up: bg_hangar_calm_v2.png is a newly generated hangar background; toon_debris_01_v4.png through toon_debris_28_v4.png simplify the salvage objects. Prompts are hangar_v2_prompt.txt and debris_v4_prompt.txt. Both use built-in image_gen. The lock ui_lock_toon_v3.xml is a repo-native vector with silver body and cyan optic to match the drones. Case descriptions were added in Russian, English and Spanish; displayed odds and purchases remain unchanged.
