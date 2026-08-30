# Third-party notices

## Waylight

This repository is a modified version of **Waylight** by **soradotwav**.
Waylight is licensed under the GNU Lesser General Public License version 3.0 or
later (`LGPL-3.0-or-later`). The license text is included in `LICENSE`.

Upstream project: https://github.com/soradotwav/waylight

## Optional interoperability

This fork contains compatibility code that can interact with optional third-party
mods or shader infrastructure, including **Punchy**, **Iris**,
**EMF/Fresh Animations**, **LambDynamicLights**, and FloodFill-style shader
lighting paths.

Those third-party projects are not redistributed by this repository except for
normal dependency declarations that may be resolved by the build system. Their
names and identifiers are used to implement or document interoperability.

## Independent lantern-holding implementation

The fallback first-person lantern pose in this public source tree is implemented
for Waylight using Minecraft pose-stack operations and Waylight's own motion
state. This repository does not include Amendments/Supplementaries source code,
classes, assets, or the previously tested exact transform/quaternion constants.
