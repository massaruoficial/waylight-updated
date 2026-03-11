package com.soradotwav.waylight.lantern;

public enum LanternPosition {
	RIGHT_HIP,
	LEFT_HIP,
	LEFT_HAND;

	public static LanternPosition fromConfig(String value) {
		if ("left_hip".equals(value)) {
			return LEFT_HIP;
		}
		if ("left_hand".equals(value)) {
			return LEFT_HAND;
		}
		return RIGHT_HIP;
	}

	public boolean isHandHeld() {
		return this == LEFT_HAND;
	}

	public boolean isHipMounted() {
		return this == RIGHT_HIP || this == LEFT_HIP;
	}

	public boolean isLeftSide() {
		return this == LEFT_HIP || this == LEFT_HAND;
	}
}
