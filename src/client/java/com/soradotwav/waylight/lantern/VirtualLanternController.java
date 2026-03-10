package com.soradotwav.waylight.lantern;

import com.soradotwav.waylight.config.WaylightConfig;
import com.soradotwav.waylight.config.WaylightConfigManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;

public final class VirtualLanternController {
	private static final float EQUIP_VOLUME = 0.8F;
	private static final float EQUIP_PITCH_ON = 1.0F;
	private static final float EQUIP_PITCH_OFF = 0.9F;
	private static final float EXTINGUISH_VOLUME = 0.2F;
	private static final float EXTINGUISH_PITCH = 1.1F;
	private static final float RELIGHT_VOLUME = 0.22F;
	private static final float RELIGHT_PITCH = 1.25F;

	private final WaylightConfigManager configManager;
	private final LanternVisibilityRules visibilityRules = new LanternVisibilityRules();
	private VirtualLanternState currentState = new VirtualLanternState(false, LanternType.NORMAL, PoseMode.HIP, false, false);

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

		playLanternSound(player, config.enabled);
		player.displayClientMessage(
			Component.translatable(config.enabled ? "message.waylight.lantern_on" : "message.waylight.lantern_off"),
			true
		);
	}

	public void tick(Minecraft client) {
		VirtualLanternState previousState = currentState;
		currentState = resolveState(client);

		LocalPlayer player = client.player;
		if (player != null
			&& previousState.enabled()
			&& currentState.enabled()
			&& previousState.lightActive() != currentState.lightActive()) {
			playTransitionSound(player, previousState, currentState);
		}
	}

	public VirtualLanternState getState() {
		return currentState;
	}

	private VirtualLanternState resolveState(Minecraft client) {
		WaylightConfig config = configManager.get();
		return visibilityRules.resolve(
			client.player,
			client,
			config.enabled,
			LanternType.fromConfig(config.lanternType),
			PoseMode.fromConfig(config.poseMode)
		);
	}

	private static void playLanternSound(LocalPlayer player, boolean active) {
		player.playSound(SoundEvents.ARMOR_EQUIP_CHAIN.value(), EQUIP_VOLUME, active ? EQUIP_PITCH_ON : EQUIP_PITCH_OFF);
	}

	private static void playTransitionSound(LocalPlayer player, VirtualLanternState previousState, VirtualLanternState currentState) {
		if (previousState.lightActive() && !currentState.lightActive()) {
			player.playSound(SoundEvents.FIRE_EXTINGUISH, EXTINGUISH_VOLUME, EXTINGUISH_PITCH);
			return;
		}

		if (!previousState.lightActive() && currentState.lightActive()) {
			player.playSound(SoundEvents.FLINTANDSTEEL_USE, RELIGHT_VOLUME, RELIGHT_PITCH);
		}
	}
}
