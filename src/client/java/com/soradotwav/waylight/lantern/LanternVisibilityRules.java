package com.soradotwav.waylight.lantern;

import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

public final class LanternVisibilityRules {
	public VirtualLanternState resolve(LocalPlayer player, Minecraft client, boolean enabled, LanternType lanternType, LanternPosition lanternPosition) {
		boolean firstPerson = client.options.getCameraType() == CameraType.FIRST_PERSON;

		if (!enabled || player == null || !player.isAlive() || player.isRemoved()) {
			return new VirtualLanternState(enabled, lanternType, lanternPosition, false, false, false, false);
		}

		if (lanternPosition.isHandHeld() && (player.isSwimming() || !player.getOffhandItem().isEmpty())) {
			return new VirtualLanternState(true, lanternType, lanternPosition, false, false, true, false);
		}

		boolean firstPersonLight = com.soradotwav.WaylightClient.CONFIG_MANAGER.get().firstPersonLight;
		boolean lightActive = !firstPerson || firstPersonLight;
		boolean modelVisible = lanternPosition.isHandHeld() || !firstPerson;

		if (com.soradotwav.WaylightClient.CONFIG_MANAGER.get().extinguishUnderwater && player.isUnderWater()) {
			return new VirtualLanternState(true, lanternType, lanternPosition, false, modelVisible, false, true);
		}

		return new VirtualLanternState(true, lanternType, lanternPosition, lightActive, modelVisible, false, false);
	}
}
