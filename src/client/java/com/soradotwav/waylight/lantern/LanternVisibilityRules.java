package com.soradotwav.waylight.lantern;

import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

public final class LanternVisibilityRules {
	public VirtualLanternState resolve(LocalPlayer player, Minecraft client, boolean enabled, LanternType lanternType, PoseMode poseMode) {
		boolean firstPerson = client.options.getCameraType() == CameraType.FIRST_PERSON;

		if (!enabled || player == null || !player.isAlive() || player.isRemoved()) {
			return new VirtualLanternState(enabled, lanternType, poseMode, false, false, false, false);
		}

		if (poseMode == PoseMode.HAND_LEFT && (player.isSwimming() || !player.getOffhandItem().isEmpty())) {
			return new VirtualLanternState(true, lanternType, poseMode, false, false, true, false);
		}

		boolean firstPersonLight = com.soradotwav.WaylightClient.CONFIG_MANAGER.get().firstPersonLight;
		boolean lightActive = !firstPerson || firstPersonLight;

		if (com.soradotwav.WaylightClient.CONFIG_MANAGER.get().extinguishUnderwater && player.isUnderWater()) {
			return new VirtualLanternState(true, lanternType, poseMode, false, !firstPerson, false, true);
		}

		return new VirtualLanternState(true, lanternType, poseMode, lightActive, !firstPerson, false, false);
	}
}
