package com.soradotwav.waylight.render;

import com.soradotwav.waylight.config.WaylightConfig;
import com.soradotwav.waylight.lantern.PoseMode;
import com.soradotwav.waylight.lantern.VirtualLanternState;

public final class LanternTransformResolver {
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
			rotationSideSign * 15.0F + poseState.rollAngle(1.0F)
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
			poseState.rollAngle(1.0F) * 0.2F
		);
	}
}
