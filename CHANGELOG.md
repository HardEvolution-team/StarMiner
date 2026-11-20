# Changelog

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
