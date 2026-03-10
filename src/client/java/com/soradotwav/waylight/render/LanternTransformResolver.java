package com.soradotwav.waylight.render;

import com.soradotwav.waylight.config.WaylightConfig;
import com.soradotwav.waylight.lantern.PoseMode;
import com.soradotwav.waylight.lantern.VirtualLanternState;
import net.minecraft.client.player.LocalPlayer;
import org.joml.Vector3d;
import org.joml.Vector3f;

public final class LanternTransformResolver {
	private static final float EMISSION_X = 0.0F;
	private static final float EMISSION_Y = -0.28F;
	private static final float EMISSION_Z = 0.0F;
	private static final float BODY_ANCHOR_HEIGHT = 0.92F;
	private static final float BODY_ANCHOR_HEIGHT_CROUCH = 0.78F;
	private static final float LEFT_ARM_ANCHOR_X = -0.32F;
	private static final float LEFT_ARM_ANCHOR_Y = 1.18F;
	private static final float LEFT_ARM_ANCHOR_Y_CROUCH = 1.04F;
	private static final float LEFT_ARM_ANCHOR_Z = 0.0F;

	public LanternTransform resolveThirdPerson(VirtualLanternState lanternState, LanternPoseState poseState, WaylightConfig config) {
		if (lanternState.poseMode() == PoseMode.HAND_LEFT) {
			return resolveHandLeft(poseState);
		}

		return resolveHip(poseState, config);
	}

	private LanternTransform resolveHip(LanternPoseState poseState, WaylightConfig config) {
		float sideSign = "left".equals(config.lanternSide) ? 1.0F : -1.0F;
		float rotationSideSign = -sideSign;
		return new LanternTransform(
			LanternTransform.Attachment.BODY,
			0.2F * sideSign,
			0.60F + poseState.bob(1.0F) * 0.03F,
			-0.15F,
			165.0F + poseState.pitchAngle(1.0F),
			rotationSideSign * -35.0F + poseState.yawLag(1.0F),
			rotationSideSign * 15.0F + poseState.rollAngle(1.0F),
			EMISSION_X,
			EMISSION_Y,
			EMISSION_Z
		);
	}

	private LanternTransform resolveHandLeft(LanternPoseState poseState) {
		return new LanternTransform(
			LanternTransform.Attachment.LEFT_ARM,
			0.05F,
			0.60F,
			0.1F,
			-50.0F + poseState.pitchAngle(1.0F) * 0.2F,
			-10.0F - poseState.yawLag(1.0F) * 0.2F,
			poseState.rollAngle(1.0F) * 0.2F,
			EMISSION_X,
			EMISSION_Y,
			EMISSION_Z
		);
	}

	public boolean resolveWorldEmission(LocalPlayer player, VirtualLanternState lanternState, LanternPoseState poseState, WaylightConfig config, Vector3d destination) {
		LanternTransform transform = resolveThirdPerson(lanternState, poseState, config);
		Vector3f offset = new Vector3f(
			transform.translateX() + transform.emissionX(),
			transform.translateY() + transform.emissionY(),
			transform.translateZ() + transform.emissionZ()
		);

		offset.rotateZ((float) Math.toRadians(transform.rotateZ()));
		offset.rotateX((float) Math.toRadians(transform.rotateX()));
		offset.rotateY((float) Math.toRadians(transform.rotateY()));
		offset.rotateY((float) Math.toRadians(-player.yBodyRot));

		Vector3f anchor = resolveAnchor(player, transform.attachment());
		destination.set(player.getX() + anchor.x() + offset.x(), player.getY() + anchor.y() + offset.y(), player.getZ() + anchor.z() + offset.z());
		return true;
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
