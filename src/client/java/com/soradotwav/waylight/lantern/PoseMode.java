package com.soradotwav.waylight.lantern;

public enum PoseMode {
	HIP("hip");

	private final String configValue;

	PoseMode(String configValue) {
		this.configValue = configValue;
	}

	public String configValue() {
		return configValue;
	}

	public static PoseMode fromConfig(String value) {
		return HIP;
	}
}
