package com.soradotwav.waylight.render;

import net.minecraft.client.model.geom.PartPose;

/**
 * Small optional bridge used when Entity Model Features is installed.
 *
 * EMF evaluates the CEM player animation on its own ModelPart tree. The
 * vanilla PlayerModel.leftArm therefore does not contain the final Fresh
 * Animations transform. The optional EMF mixin captures that final pose and
 * exposes it here for Waylight's third-person attachment renderer.
 */
public final class EmfPlayerPoseBridge {
    private static PartPose lastLeftArmPose;
    private static long captureSerial;

    private EmfPlayerPoseBridge() {}

    public static void captureLeftArm(PartPose pose) {
        if (pose == null) return;
        lastLeftArmPose = pose;
        captureSerial++;
    }

    public static PartPose leftArmPose() {
        return lastLeftArmPose;
    }

    public static long captureSerial() {
        return captureSerial;
    }

    public static void clear() {
        lastLeftArmPose = null;
    }
}
