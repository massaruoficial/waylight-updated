package com.soradotwav.waylight.lantern;

import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

public final class LanternVisibilityRules {
	public VirtualLanternState resolve(LocalPlayer player, Minecraft client, boolean enabled, LanternType lanternType, PoseMode poseMode) {
		if (!enabled || player == null || !player.isAlive() || player.isRemoved()) {
			return new VirtualLanternState(enabled, lanternType, poseMode, false, false);
		}

		if (player.isSwimming()) {
			return new VirtualLanternState(true, lanternType, poseMode, false, false);
		}

		boolean firstPerson = client.options.getCameraType() == CameraType.FIRST_PERSON;
		return new VirtualLanternState(true, lanternType, poseMode, true, !firstPerson);
	}
}
