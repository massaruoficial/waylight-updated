# Waylight 26.2 — Community Fork

A client-side virtual lantern mod for **Minecraft 26.2 Fabric**, based on
[Waylight](https://github.com/soradotwav/waylight) by **soradotwav**.

This fork keeps Waylight's virtual-lantern behavior and adds compatibility and
rendering work for a modern 26.2 client setup.

## Features in this fork

- virtual Lantern / Soul Lantern without occupying the real offhand slot;
- dynamic lighting through LambDynamicLights;
- optional Punchy integration, including the virtual offhand render stack;
- correct Punchy behavior while the player's real offhand is occupied;
- optional Iris/FloodFill held-light bridge for Lantern and Soul Lantern;
- EMF / Fresh Animations player-arm attachment compatibility;
- independent first-person lantern pose and spring motion when Punchy is absent;
- Minecraft 26.2-only Stonecutter build target.

Punchy, Iris, EMF/Fresh Animations and shader packs are **not bundled** with
this repository. Their names are used only for optional interoperability.

## Requirements

- Minecraft 26.2
- Fabric Loader 0.19.3 or newer
- Fabric API
- Java 25 or newer
- LambDynamicLights 4.12.0+26.2 or newer

Mod Menu is optional.

## Building

Clone the repository and run the included Gradle wrapper:

```bash
chmod +x gradlew
./gradlew build
```

The active Stonecutter target is `26.2-fabric`. Build artifacts are written to
the Gradle build output directories.

## License and attribution

Waylight and this modified source tree are distributed under
**LGPL-3.0-or-later**. See [`LICENSE`](LICENSE).

The original Waylight project is authored by **soradotwav**. This repository is
an unofficial community fork and is not presented as an official upstream
release.

The custom compatibility and first-person rendering changes in this fork do not
bundle Amendments/Supplementaries code, classes or assets. See
[`THIRD_PARTY_NOTICES.md`](THIRD_PARTY_NOTICES.md) for additional attribution
and interoperability notes.
