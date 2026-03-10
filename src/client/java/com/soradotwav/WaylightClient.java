package com.soradotwav;

import com.mojang.blaze3d.platform.InputConstants;
import com.soradotwav.waylight.lantern.VirtualLanternController;
import com.soradotwav.waylight.light.LambDynamicLightsAdapter;
import com.soradotwav.waylight.render.LanternRigResolver;
import com.soradotwav.waylight.render.LanternPoseController;
import com.soradotwav.waylight.render.LanternTransformResolver;
import com.soradotwav.waylight.render.WaylightRenderHooks;
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
	public static final LanternRigResolver RIG_RESOLVER = new LanternRigResolver();
	public static final LanternTransformResolver TRANSFORM_RESOLVER = RIG_RESOLVER.transformResolver();
	public static VirtualLanternController LANTERN_CONTROLLER;
	public static LanternPoseController POSE_CONTROLLER;
	public static LambDynamicLightsAdapter DYNAMIC_LIGHTS_ADAPTER;

	@Override
	public void onInitializeClient() {
		CONFIG_MANAGER.load();
		LANTERN_CONTROLLER = new VirtualLanternController(CONFIG_MANAGER);
		POSE_CONTROLLER = new LanternPoseController();
		DYNAMIC_LIGHTS_ADAPTER = new LambDynamicLightsAdapter();
		WaylightRenderHooks.register();

		KeyMapping toggleLanternKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
			"key.waylight.toggle_lantern",
			InputConstants.Type.KEYSYM,
			GLFW.GLFW_KEY_L,
			WAYLIGHT_KEY_CATEGORY
		));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			LANTERN_CONTROLLER.tick(client);
			POSE_CONTROLLER.tick(client, LANTERN_CONTROLLER.getState());
			DYNAMIC_LIGHTS_ADAPTER.tick(client);

			while (toggleLanternKey.consumeClick()) {
				LANTERN_CONTROLLER.toggle(client);
			}
		});

		Waylight.LOGGER.info("Initializing {} client", Waylight.MOD_ID);
	}
}
