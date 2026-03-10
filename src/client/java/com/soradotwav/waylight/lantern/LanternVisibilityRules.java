package com.soradotwav.waylight.lantern;

import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

public final class LanternVisibilityRules {
	public VirtualLanternState resolve(LocalPlayer player, Minecraft client, boolean enabled, LanternType lanternType, PoseMode poseMode) {
		boolean firstPerson = client.options.getCameraType() == CameraType.FIRST_PERSON;

		if (!enabled || player == null || !player.isAlive() || player.isRemoved()) {
			return new VirtualLanternState(enabled, lanternType, poseMode, false, false);
		}

		if (com.soradotwav.WaylightClient.CONFIG_MANAGER.get().extinguishUnderwater && player.isUnderWater()) {
			return new VirtualLanternState(true, lanternType, poseMode, false, !firstPerson);
		}

		return new VirtualLanternState(true, lanternType, poseMode, true, !firstPerson);
	}
}
