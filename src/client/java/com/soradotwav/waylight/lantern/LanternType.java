package com.soradotwav.waylight.lantern;

public enum LanternType {
	NORMAL,
	SOUL;

	public static LanternType fromConfig(String value) {
		return "soul".equals(value) ? SOUL : NORMAL;
	}
}
