# Changelog

## 0.9.15
### Changed
- The original mod file is now downloaded automatically on first start (needs ArchaicLib 1.42.0+): sources and mirrors are listed in `config/archaiclib/originals/` and can be edited there. If the download fails, a banner on the main menu links to the manual download.

## 0.9.14
### Changed
- Performance: uses ArchaicLib 1.40.0 FastRandom instead of java.util.Random, plus the 1.40.0 cached block-state / allocation-free legacy world calls. Requires ArchaicLib 1.40.0+.

## 0.9.13
- Fixed a startup crash/mixin conflict with Cave Biomes API (both mods modified the same void-height constant in Entity); the star void check is now a plain inject.

## 0.9.12
- Fixed chunks disappearing/culling wrongly while standing on star sides (rotated gravity): terrain setup now uses the real eye position. Skipped automatically when Celeritas/Sodium-style renderers are installed.

## 0.9.11
- **No original assets are bundled any more.** Textures are read at runtime from your own copy of the original `starminer1710-0.9.9.jar` in `.minecraft/archaic_originals/` (via ArchaicLib).
- License changed to HE-RSAL 1.1; source published on GitHub.

## 0.9.10
- Fixed the player freezing / getting stuck while walking on stars: collisions in rotated gravity now tolerate rounding errors, so the hitbox no longer sinks 1 ulp into the ground and blocks all sideways movement.
- Fixed the server rubber-banding players on star sides ("moved wrongly"): vanilla only ignores the world-Y error, which is the wrong axis under sideways gravity.
- The hitbox is no longer rebuilt (and drifted) on every position update; it keeps the exact box from the movement code.
- Vanilla "push out of blocks" and the server-side jump guess are disabled under abnormal gravity (they work in world axes).
- Removed a redundant per-move chunk update on the server (performance).

## 0.9.9
First release of the 1.12.2 port (Cleanroom Loader). Based on Starminer 0.9.9 for 1.7.10 by ARUBE.

- Gravity system rewritten with Mixins (all six directions).
- Blocks, items, entities and star generation ported.
