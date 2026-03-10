package com.soradotwav.waylight.render;

import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.world.entity.EntityType;

public final class WaylightRenderHooks {
	public static final RenderStateDataKey<Boolean> LOCAL_PLAYER_RENDER_STATE = RenderStateDataKey.create(() -> "waylight:is_local_player");

	private WaylightRenderHooks() {
	}

	public static void register() {
		LivingEntityFeatureRendererRegistrationCallback.EVENT.register((entityType, entityRenderer, registrationHelper, context) -> {
			if (entityType == EntityType.PLAYER && entityRenderer instanceof AvatarRenderer<?> avatarRenderer) {
				registrationHelper.register(new WaylightPlayerRenderFeature(avatarRenderer));
			}
		});
	}
}
