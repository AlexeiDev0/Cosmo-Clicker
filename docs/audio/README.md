# Bundled audio — 3 October 2026

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

Short effects use SoundPool with at most six simultaneous streams. Collection
feedback is limited to once per 350 ms. Music loops at a lower volume. The sound
setting disables music, effects and vibration; leaving the foreground stops effects
and pauses music. Save loading does not trigger collection/prestige notifications.

`sha256.csv` records the shipped files for licence provenance.
