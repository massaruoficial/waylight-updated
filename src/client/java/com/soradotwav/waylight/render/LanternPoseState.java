package com.soradotwav.waylight.render;

public final class LanternPoseState {
	public float pitchAngle;
	public float pitchVelocity;
	public float rollAngle;
	public float rollVelocity;
	public float yawLag;
	public float bob;
	public float prevPitchAngle;
	public float prevRollAngle;
	public float prevYawLag;
	public float prevBob;

	public float pitchAngle(float tickDelta) {
		return lerp(tickDelta, prevPitchAngle, pitchAngle);
	}

	public float rollAngle(float tickDelta) {
		return lerp(tickDelta, prevRollAngle, rollAngle);
	}

	public float yawLag(float tickDelta) {
		return lerp(tickDelta, prevYawLag, yawLag);
	}

	public float bob(float tickDelta) {
		return lerp(tickDelta, prevBob, bob);
	}

	private static float lerp(float tickDelta, float start, float end) {
		return start + (end - start) * tickDelta;
	}
}
