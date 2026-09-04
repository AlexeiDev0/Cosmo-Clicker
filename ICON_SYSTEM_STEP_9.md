# Step 9 — Icon system

## Direction

Primary interface icons use one compact cosmic line-art language: a cyan/blue
energy accent, dark navy body, rounded technical geometry, and a clear silhouette
that remains readable at 18–40 dp. Decorative illustrations are not used as
navigation glyphs.

## Resource families

- Navigation: `ic_nav_*_minimal`
- Economy and rewards: `ic_currency_debris_v2`, `ic_drone_energy_cell_v2`,
  `ic_prestige_hologram_v2`, `ic_achievement_medal`, `ic_goal_route_minimal`
- Actions and states: `ic_action_*`, `ui_lock_control_v2`, `ui_new_badge_v2`,
  `ui_close_control_v2`
- Settings: `icon_settings_*_v2`
- Upgrade products: `ic_product_*` and the matching illustrated upgrade assets
- Events, drones, planets, cases, and debris use their dedicated illustrated
  resource families at card/scene size, not as arbitrary navigation icons.

## Rules

- Preserve intrinsic colors for illustrated icons; do not apply Material tint.
- Use vector line icons for compact navigation and actions.
- Use raster illustrations only where the layout provides enough size for their
  details to remain legible.
- Keep icon content descriptions on interactive controls; decorative art stays
  excluded from accessibility semantics.
- Locked, disabled, active, selected, and completed meaning comes from the shared
  control treatment, not from unrelated replacement symbols.

## Integration audit

The bottom navigation now consistently uses the `ic_nav_*_minimal` family for
goals, shop, hangar, and statistics. The earlier generated shop and hangar
illustrations remain available for larger decorative placements but are no longer
shrunk into navigation slots.

The separately generated `ic_nav_shop_cosmic_v3.png` was intentionally not wired
in: its terminal scene is too detailed for a 38 dp glyph. It is left untouched
pending either a larger shop-header placement or explicit removal after reference
auditing.
