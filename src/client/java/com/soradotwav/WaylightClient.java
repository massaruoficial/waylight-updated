package com.soradotwav;

import com.mojang.blaze3d.platform.InputConstants;
import com.soradotwav.waylight.lantern.VirtualLanternController;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.api.ClientModInitializer;
import com.soradotwav.waylight.config.WaylightConfigManager;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public class WaylightClient implements ClientModInitializer {
	private static final KeyMapping.Category WAYLIGHT_KEY_CATEGORY = KeyMapping.Category.register(
		Identifier.fromNamespaceAndPath(Waylight.MOD_ID, "general")
	);

	public static final WaylightConfigManager CONFIG_MANAGER = new WaylightConfigManager();
	public static VirtualLanternController LANTERN_CONTROLLER;

	@Override
	public void onInitializeClient() {
		CONFIG_MANAGER.load();
		LANTERN_CONTROLLER = new VirtualLanternController(CONFIG_MANAGER);

		KeyMapping toggleLanternKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
			"key.waylight.toggle_lantern",
			InputConstants.Type.KEYSYM,
			GLFW.GLFW_KEY_L,
			WAYLIGHT_KEY_CATEGORY
		));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (toggleLanternKey.consumeClick()) {
				LANTERN_CONTROLLER.toggle(client);
			}
		});

		Waylight.LOGGER.info("Initializing {} client", Waylight.MOD_ID);
	}
}
