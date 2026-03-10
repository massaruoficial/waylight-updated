package com.soradotwav.waylight.lantern;

public enum LanternType {
	NORMAL("normal"),
	SOUL("soul");

	private final String configValue;

	LanternType(String configValue) {
		this.configValue = configValue;
	}

	public String configValue() {
		return configValue;
	}

	public static LanternType fromConfig(String value) {
		return "soul".equals(value) ? SOUL : NORMAL;
	}
}
