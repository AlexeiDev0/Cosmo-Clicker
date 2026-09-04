# Step 11 — Drone visual set

The fleet contains 29 configured drones and 29 unique `drone_*_v2` resources.
The existing registry maps every `drone_1`…`drone_29` ID to its corresponding
asset; rarity bands and income values are unchanged.

`FleetIcon` now applies one shared presentation frame to every drone: a subtle
rarity-colored halo, a thin rarity ring, and restrained shadow. The artwork itself
remains untouched, so drone silhouettes and traits stay consistent while rarity is
legible at hangar-card and gameplay sizes.

The active-fleet, discovery, cargo, infection, and repair states remain owned by
the existing state/controller logic. This step changes only presentation.

Generated asset: `drawable-nodpi/drone_fleet_showcase_v1.png`. It was created with
the `imagegen` stylized-concept game-asset prompt using an existing drone as the
style reference, then visually reviewed before being placed in the Hangar overview
card. The generated image is intentionally used at large size; the compact drone
icons remain the approved per-drone resources.

For the Hangar-specific pass, `drawable-nodpi/bg_hangar_fleet_command_v1.png` was
generated with the existing Hangar scene as a style reference. It is now the
Hangar panel backdrop; the previous `bg_hangar_command_v3` remains in the project
for safe rollback and other screens.
