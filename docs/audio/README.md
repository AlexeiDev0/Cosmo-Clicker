# Bundled audio — 3 October 2026

Updated 11 October 2026: background playback cycles through Exploration Theme,
Outer Space Loop, and Observing The Star, at volume 0.135. Effects use a 0.75
gain multiplier. Pausing or leaving the app pauses the current track.

New sources, both published under CC0:

- Outer Space Loop by wipics: https://opengameart.org/content/outer-space-loop
  (`music_outer_space.mp3`, original `outer_space_2.mp3`).
- Another space background track by yd: https://opengameart.org/content/another-space-background-track
  (`music_observing_stars.ogg`, original `ObservingTheStar.ogg`).

`sfx_click_soft.wav` and `sfx_collect_soft.wav` are original synthesized effects,
reproducible with `tools/Generate-SoftFeedback.ps1`. They use short attack and
release envelopes to avoid sharp sample-edge clicks. Playback varies pitch
slightly to soften repetition; collection feedback remains rate limited.

All new audio is CC0. Original licence files and source-page snapshots are in `licenses/`.
Audio is bundled in the APK/AAB and never downloaded by the game. Attribution is optional.

| Resource in res/raw | Original asset | Source |
| --- | --- | --- |
| sfx_click.ogg | click_001.ogg | Kenney Interface Sounds |
| sfx_case_pulse.ogg | open_001.ogg | Kenney Interface Sounds |
| sfx_case_reveal.ogg | confirmation_002.ogg | Kenney Interface Sounds |
| sfx_event_start.ogg | question_001.ogg | Kenney Interface Sounds |
| sfx_event_success.ogg | confirmation_001.ogg | Kenney Interface Sounds |
| sfx_event_failure.ogg | error_001.ogg | Kenney Interface Sounds |
| sfx_resource_collect.ogg | twoTone1.ogg | Kenney Digital Audio |
| sfx_drone_action.ogg | spaceTrash1.ogg | Kenney Digital Audio |
| sfx_prestige.ogg | powerUp7.ogg | Kenney Digital Audio |
| sfx_planet_travel.ogg | phaserUp1.ogg | Kenney Digital Audio |
| music_exploration.ogg | Exploration Theme.ogg | Cleyton Kauffman |

Sources: https://kenney.nl/assets/interface-sounds,
https://kenney.nl/assets/digital-audio,
https://opengameart.org/content/exploration-theme.

The active music playlist is now `music_orbit_ambient_1.wav`,
`music_orbit_ambient_2.wav` and `music_orbit_ambient_3.wav`: three original
72-second stereo compositions generated locally by `tools/Generate-AmbientMusic.ps1`.
They use slow layered chords and gentle arpeggios. No external recordings are used.
Playback gain is 0.085, with a five-second equal-power crossfade. Both players
pause when the app leaves the foreground and resume the same transition position.
The historical imported music listed above is no longer shipped.

Short effects use SoundPool with at most six simultaneous streams. Collection
feedback is limited to once per 350 ms. The sound
setting disables music, effects and vibration; leaving the foreground stops effects
and pauses music. Save loading does not trigger collection/prestige notifications.

`sha256.csv` records the shipped files for licence provenance.
