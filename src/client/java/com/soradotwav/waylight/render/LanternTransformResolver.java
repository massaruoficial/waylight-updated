package com.soradotwav.waylight.render;

import com.soradotwav.waylight.config.WaylightConfig;
import com.soradotwav.waylight.lantern.LanternPosition;
import com.soradotwav.waylight.lantern.VirtualLanternState;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec3;
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
	private static final double FIRST_PERSON_LEFT_OFFSET = 0.34D;
	private static final double FIRST_PERSON_FORWARD_OFFSET = 0.58D;
	private static final double FIRST_PERSON_VERTICAL_OFFSET = -0.24D;
	private static final double FIRST_PERSON_YAW_LAG_SCALE = 0.0035D;
	private static final double FIRST_PERSON_PITCH_LIFT_SCALE = 0.0025D;

	public LanternTransform resolveThirdPerson(VirtualLanternState lanternState, LanternPoseState poseState, WaylightConfig config) {
		if (lanternState.lanternPosition().isHandHeld()) {
			return resolveHandLeft(poseState);
		}

		return resolveHip(poseState, lanternState.lanternPosition());
	}

	private LanternTransform resolveHip(LanternPoseState poseState, LanternPosition lanternPosition) {
		float sideSign = lanternPosition == LanternPosition.LEFT_HIP ? 1.0F : -1.0F;
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
		boolean firstPerson = Minecraft.getInstance().options.getCameraType() == CameraType.FIRST_PERSON;
		if (firstPerson && lanternState.lanternPosition().isHandHeld()) {
			resolveFirstPersonHandLeftEmission(player, poseState, destination);
			return true;
		}

		LanternTransform transform = resolveThirdPerson(lanternState, poseState, config);
		float anchorYaw = player.yBodyRot;
		Vector3f offset = new Vector3f(
			transform.translateX() + transform.emissionX(),
			transform.translateY() + transform.emissionY(),
			transform.translateZ() + transform.emissionZ()
		);

		offset.rotateZ((float) Math.toRadians(transform.rotateZ()));
		offset.rotateX((float) Math.toRadians(transform.rotateX()));
		offset.rotateY((float) Math.toRadians(transform.rotateY()));
		offset.rotateY((float) Math.toRadians(-anchorYaw));

		Vector3f anchor = resolveAnchor(player, transform.attachment());
		destination.set(player.getX() + anchor.x() + offset.x(), player.getY() + anchor.y() + offset.y(), player.getZ() + anchor.z() + offset.z());
		return true;
	}

	private static void resolveFirstPersonHandLeftEmission(LocalPlayer player, LanternPoseState poseState, Vector3d destination) {
		Vec3 eyePosition = player.getEyePosition();
		Vec3 forward = player.getViewVector(1.0F).normalize();
		Vec3 left = new Vec3(forward.z, 0.0, -forward.x).normalize();
		double yawOffset = poseState.yawLag(1.0F) * FIRST_PERSON_YAW_LAG_SCALE;
		double pitchOffset = -poseState.pitchAngle(1.0F) * FIRST_PERSON_PITCH_LIFT_SCALE;
		Vec3 emission = eyePosition
			.add(left.scale(FIRST_PERSON_LEFT_OFFSET + yawOffset))
			.add(forward.scale(FIRST_PERSON_FORWARD_OFFSET))
			.add(0.0, FIRST_PERSON_VERTICAL_OFFSET + pitchOffset, 0.0);
		destination.set(emission.x, emission.y, emission.z);
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
