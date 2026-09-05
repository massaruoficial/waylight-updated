package com.soradotwav.waylight.integration;

import com.soradotwav.WaylightClient;
import com.soradotwav.waylight.lantern.VirtualLanternState;
import dev.lambdaurora.lambdynlights.api.DynamicLightsContext;
import dev.lambdaurora.lambdynlights.api.DynamicLightsInitializer;
import dev.lambdaurora.lambdynlights.api.entity.luminance.EntityLuminance;
import dev.lambdaurora.lambdynlights.api.item.ItemLightSourceManager;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import org.jetbrains.annotations.Range;

/**
 * Registers Waylight through LambDynamicLights' native entity-light pipeline.
 *
 * <p>The shader bridge uses Iris item IDs, but non-shader dynamic lighting should
 * not depend on those IDs at all. Instead, the local player is registered as a
 * native LambDynamicLights entity light source whose luminance follows the
 * virtual Waylight state.</p>
 */
public final class WaylightDynamicLightsInitializer implements DynamicLightsInitializer {
    private static final int LUMINANCE = 15;

    @Override
    public void onInitializeDynamicLights(DynamicLightsContext context) {
        context.entityLightSourceManager().onRegisterEvent().register(registration ->
                registration.register(EntityTypes.PLAYER, new WaylightPlayerLuminance()));
    }

    private static final class WaylightPlayerLuminance implements EntityLuminance {
        @Override
        public Type type() {
            // This provider is runtime-only and is never serialized. Returning VALUE keeps
            // it compatible with the API contract without introducing a custom codec/type.
            return Type.VALUE;
        }

        @Override
        public @Range(from = 0, to = 15) int getLuminance(ItemLightSourceManager itemLightSourceManager, Entity entity) {
            Minecraft client = Minecraft.getInstance();
            if (client.player == null || entity != client.player) {
                return 0;
            }

            VirtualLanternState state = WaylightClient.runtime().lanternController().getState();
            if (!state.enabled()
                    || !state.lightActive()
                    || state.temporarilySuppressed()
                    || state.underwaterExtinguished()) {
                return 0;
            }

            return LUMINANCE;
        }
    }
}
