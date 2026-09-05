## 1.0.0-v18.2+26.2

- restored Iris shader IDs 1015/1022 from the current source;
- added a second, native LambDynamicLights integration path for non-shader rendering;
- the local player now becomes a LambDynamicLights entity light source while Waylight is active, independent of Iris/FloodFill item IDs;
- kept the existing custom behavior adapter as a compatibility fallback.

# Changelog

## 1.0.0-v17.8+26.2

Public-source cleanup release for Minecraft 26.2.

- retained Punchy virtual-lantern integration and occupied-offhand handling;
- retained Iris/FloodFill Lantern and Soul Lantern held-light compatibility;
- retained LambDynamicLights behavior;
- retained EMF/Fresh Animations compatibility;
- finalized the independent fallback first-person lantern orientation;
- removed development-only migration notes and obsolete version-history files;
- normalized repository metadata, attribution, README and `.gitignore`;
- kept the project targeted exclusively at Minecraft 26.2 Fabric.
