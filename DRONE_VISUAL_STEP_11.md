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
