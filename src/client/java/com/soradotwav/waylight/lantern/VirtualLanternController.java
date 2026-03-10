package com.soradotwav.waylight.lantern;

import com.soradotwav.waylight.config.WaylightConfig;
import com.soradotwav.waylight.config.WaylightConfigManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;

public final class VirtualLanternController {
	private final WaylightConfigManager configManager;
	private final LanternVisibilityRules visibilityRules = new LanternVisibilityRules();

	public VirtualLanternController(WaylightConfigManager configManager) {
		this.configManager = configManager;
	}

	public void toggle(Minecraft client) {
		LocalPlayer player = client.player;
		if (player == null) {
			return;
		}

		WaylightConfig config = configManager.get();
		config.enabled = !config.enabled;
		configManager.save();

		player.playSound(SoundEvents.ARMOR_EQUIP_CHAIN.value(), 0.8F, config.enabled ? 1.0F : 0.9F);
		player.displayClientMessage(
			Component.translatable(config.enabled ? "message.waylight.lantern_on" : "message.waylight.lantern_off"),
			true
		);
	}

	public VirtualLanternState getState(Minecraft client) {
		WaylightConfig config = configManager.get();
		return visibilityRules.resolve(
			client.player,
			client,
			config.enabled,
			LanternType.fromConfig(config.lanternType),
			PoseMode.fromConfig(config.poseMode)
		);
	}
}
