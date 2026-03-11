![Waylight Banner](assets/waylight-banner.png)

**A client-side virtual lantern mod for Fabric**, giving your player configurable vanilla lantern carry visuals with localized dynamic light and no inventory item requirement.

> Built for Minecraft `1.21.11`. Waylight is client-side only and does not modify server-side gameplay.

Waylight includes:

- Vanilla lantern and soul lantern rendering
- Carry positions: right hip, left hip, and left hand
- First-person and third-person lantern behavior
- Procedural carry motion with configurable intensity
- Light emission localized to the lantern rig via LambDynamicLights
- Optional auto-equip in darkness and auto-unequip in brightness
- Underwater extinguish behavior
- Full Mod Menu + YACL configuration UI

If you encounter any issues, please [report them here](https://github.com/soradotwav/waylight/issues).

### Controls

- `L`: Toggle lantern on/off

### Config Highlights

- Lantern type: `Lantern` or `Soul Lantern`
- Lantern position: `Right Hip`, `Left Hip`, or `Left Hand`
- First-person light toggle
- First-person hand motion: `Physics` or `Static`
- Auto-light behavior with threshold (`0-15`)
- Motion intensity slider (`25%-200%`)
- Debug anchor gizmo toggle

Config is saved at `config/waylight.json`.

### Requirements

- Minecraft `1.21.11`
- Java `21+`
- Fabric Loader `>=0.18.4`
- Fabric API
- LambDynamicLights `>=4.9.1+1.21.11`
- Optional: Mod Menu

### Behavior Notes

- Waylight is virtual and does not require a real lantern item.
- In left-hand mode, the lantern suppresses if you are swimming or your offhand is occupied.
- With `Extinguish Underwater` enabled, the lantern can remain visible while light output is disabled.
- In first-person hip modes, the model is hidden by default; `First-Person Light` controls whether light remains active.

### License

`LGPL-3.0-or-later` (see [LICENSE](LICENSE)).
