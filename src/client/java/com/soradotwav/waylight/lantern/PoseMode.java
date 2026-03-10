package com.soradotwav.waylight.lantern;

public enum PoseMode {
	HIP,
	HAND_LEFT;

	public static PoseMode fromConfig(String value) {
		return "hand_left".equals(value) ? HAND_LEFT : HIP;
	}
}
