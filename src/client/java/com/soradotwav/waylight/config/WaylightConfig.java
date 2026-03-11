package com.soradotwav.waylight.config;

import com.soradotwav.waylight.lantern.LanternPosition;
import com.soradotwav.waylight.lantern.LanternType;
import com.soradotwav.waylight.render.FirstPersonHandMotionMode;

public final class WaylightConfig {
    public boolean enabled = false;
    public boolean autoEquipInDarkness = false;
    public boolean autoUnequipInBrightness = false;
    public boolean firstPersonLight = true;
    public boolean debugAnchorGizmo = false;
    public boolean extinguishUnderwater = true;

    public int autoLightThreshold = 7;
    public int motionIntensity = 100;

    public FirstPersonHandMotionMode firstPersonHandMotion = FirstPersonHandMotionMode.PHYSICS;
    public LanternType lanternType = LanternType.NORMAL;
    public LanternPosition lanternPosition = LanternPosition.RIGHT_HIP;
}
