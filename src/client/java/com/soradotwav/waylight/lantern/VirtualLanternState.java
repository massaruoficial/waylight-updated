package com.soradotwav.waylight.lantern;

public record VirtualLanternState(
	boolean enabled,
	LanternType lanternType,
	PoseMode poseMode,
	boolean lightActive,
	boolean modelVisible,
	boolean temporarilySuppressed,
	boolean underwaterExtinguished
) {
}
