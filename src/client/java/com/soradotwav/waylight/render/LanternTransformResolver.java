package com.soradotwav.waylight.render;

import com.soradotwav.waylight.config.WaylightConfig;
import com.soradotwav.waylight.lantern.LanternPosition;
import com.soradotwav.waylight.lantern.VirtualLanternState;

public final class LanternTransformResolver {
	private static final float EMISSION_X = 0.0F;
	private static final float EMISSION_Y = -0.28F;
	private static final float EMISSION_Z = 0.0F;

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
}
