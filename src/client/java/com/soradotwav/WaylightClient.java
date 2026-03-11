package com.soradotwav;

import com.mojang.blaze3d.platform.InputConstants;
import com.soradotwav.waylight.WaylightRuntime;
import com.soradotwav.waylight.render.WaylightRenderHooks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public class WaylightClient implements ClientModInitializer {
    private static final KeyMapping.Category WAYLIGHT_KEY_CATEGORY =
            KeyMapping.Category.register(Identifier.fromNamespaceAndPath(Waylight.MOD_ID, "general"));

    private static final WaylightRuntime RUNTIME = new WaylightRuntime();

    public static WaylightRuntime runtime() {
        return RUNTIME;
    }

    @Override
    public void onInitializeClient() {
        RUNTIME.configManager().load();
        WaylightRenderHooks.register();

        KeyMapping toggleLanternKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.waylight.toggle_lantern", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_L, WAYLIGHT_KEY_CATEGORY));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            RUNTIME.lanternController().tick(client);
            RUNTIME.poseController().tick(client, RUNTIME.lanternController().getState());
            RUNTIME.dynamicLightsAdapter().tick(client);

            while (toggleLanternKey.consumeClick()) {
                RUNTIME.lanternController().toggle(client);
            }
        });

        Waylight.LOGGER.info("Initializing {} client", Waylight.MOD_ID);
    }
}
