package com.soradotwav.waylight.render;

import com.soradotwav.waylight.config.WaylightConfig;
import com.soradotwav.waylight.lantern.VirtualLanternState;
import net.minecraft.client.player.LocalPlayer;
import org.joml.Vector3d;
import org.joml.Vector3f;

public final class LanternRigResolver {
	private static final float BODY_ANCHOR_HEIGHT = 0.92F;
	private static final float BODY_ANCHOR_HEIGHT_CROUCH = 0.78F;
	private static final float LEFT_ARM_ANCHOR_X = -0.32F;
	private static final float LEFT_ARM_ANCHOR_Y = 1.18F;
	private static final float LEFT_ARM_ANCHOR_Y_CROUCH = 1.04F;
	private static final float LEFT_ARM_ANCHOR_Z = 0.0F;

	private final LanternTransformResolver transformResolver = new LanternTransformResolver();

	public LanternRig resolve(VirtualLanternState lanternState, LanternPoseState poseState, WaylightConfig config) {
		return new LanternRig(transformResolver.resolveThirdPerson(lanternState, poseState, config));
	}

	public LanternRig resolveThirdPerson(VirtualLanternState lanternState, LanternPoseState poseState, WaylightConfig config) {
		return resolve(lanternState, poseState, config);
	}

	public void resolveWorldLightCore(LocalPlayer player, LanternRig rig, Vector3d destination) {
		LanternTransform transform = rig.transform();
		Vector3f offset = new Vector3f(
			transform.translateX() + rig.lightCoreX(),
			transform.translateY() + rig.lightCoreY(),
			transform.translateZ() + rig.lightCoreZ()
		);

		offset.rotateZ((float) Math.toRadians(transform.rotateZ()));
		offset.rotateX((float) Math.toRadians(transform.rotateX()));
		offset.rotateY((float) Math.toRadians(transform.rotateY()));
		offset.rotateY((float) Math.toRadians(-player.yBodyRot));

		Vector3f anchor = resolveAnchor(player, rig.attachment());
		destination.set(
			player.getX() + anchor.x() + offset.x(),
			player.getY() + anchor.y() + offset.y(),
			player.getZ() + anchor.z() + offset.z()
		);
	}

	public LanternTransformResolver transformResolver() {
		return transformResolver;
	}

	private static Vector3f resolveAnchor(LocalPlayer player, LanternTransform.Attachment attachment) {
		if (attachment == LanternTransform.Attachment.LEFT_ARM) {
			return new Vector3f(
				LEFT_ARM_ANCHOR_X,
				player.isCrouching() ? LEFT_ARM_ANCHOR_Y_CROUCH : LEFT_ARM_ANCHOR_Y,
				LEFT_ARM_ANCHOR_Z
			);
		}

		return new Vector3f(
			0.0F,
			player.isCrouching() ? BODY_ANCHOR_HEIGHT_CROUCH : BODY_ANCHOR_HEIGHT,
			0.0F
		);
	}
}
