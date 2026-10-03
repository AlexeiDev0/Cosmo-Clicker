# Painted background, 2026-09-30

Generated with the built-in imagegen tool. Reference image: `app/src/main/res/drawable-nodpi/planet_1_painted_v3.png`.

Asset: `app/src/main/res/drawable-nodpi/background_nebula_painted_v1.png`.

One shared painted scene is drawn through `PaintedBackground.kt`, with destination-specific colour washes for 12 backgrounds. Existing resource IDs remain valid. The original vector backgrounds are retained. Settings and the privacy gate now also display the scene.

## Final generation prompt

Create one portrait 1024x1536 background illustration for an Android cosmic idle game. Style reference: the attached project's painted Earth-like planet: beautifully hand-painted, soft brushwork, luminous pastel cyan and pale mint highlights, violet blue shadows, creamy warm starlight. Paint an atmospheric deep indigo outer-space sky with flowing painterly nebula cloud wisps concentrated on top left and lower right edges, a few delicate small stars, very subtle distant planet crescent in the upper right margin. Center 65 percent stays dark, calm, almost empty to preserve readability of white UI and allow a large planet sprite to sit over it. Edges are also dark enough for labels. Rich material texture, gentle diffuse light, no harsh neon, no geometric vector shapes. Full bleed opaque background. No text, no letters, no interface, no buttons, no large central planet. This is a single shared scene texture, not an atlas.

## Visual limits

The generated scene was inspected directly. Runtime layout and text contrast on an Android device still require device testing; the local emulator cannot boot because its hardware acceleration driver is missing. Button and panel changes use existing artwork and Compose styling. No gameplay or save format changes were made in this pass.
