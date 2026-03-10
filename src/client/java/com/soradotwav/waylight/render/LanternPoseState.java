package com.soradotwav.waylight.render;

public final class LanternPoseState {
	public float swayX;
	public float swayZ;
	public float bob;
	public float prevSwayX;
	public float prevSwayZ;
	public float prevBob;

	public float swayX(float tickDelta) {
		return lerp(tickDelta, prevSwayX, swayX);
	}

	public float swayZ(float tickDelta) {
		return lerp(tickDelta, prevSwayZ, swayZ);
	}

	public float bob(float tickDelta) {
		return lerp(tickDelta, prevBob, bob);
	}

	private static float lerp(float tickDelta, float start, float end) {
		return start + (end - start) * tickDelta;
	}
}
