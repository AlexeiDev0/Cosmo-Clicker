# Step 10 — Planet visual set

The route contains exactly 39 planet configurations (`p1`…`p39`) and exactly 39
matching `planet_1_v2`…`planet_39_v2` resources. Resource hashes are all unique;
no planet silently aliases another asset. IDs, route order, unlock conditions,
prices, and economy remain unchanged.

`PlanetButton` now supplies a shared cosmic treatment around every sprite:

- a restrained color-matched radial glow;
- a thin orbital ellipse for silhouette and depth;
- reduced-alpha treatment for locked planets;
- rotation that follows the existing reduced-motion switch.

This keeps the early-route sphere illustrations and late-route detailed worlds in
one readable stylized language without regenerating or replacing approved artwork.
The original planet files remain intact and can be reverted independently.
