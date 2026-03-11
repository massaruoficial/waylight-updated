package com.soradotwav.waylight.render;

public enum FirstPersonHandMotionMode {
	PHYSICS,
	STATIC;

	public static FirstPersonHandMotionMode fromConfig(String value) {
		return "static".equals(value) ? STATIC : PHYSICS;
	}
}
