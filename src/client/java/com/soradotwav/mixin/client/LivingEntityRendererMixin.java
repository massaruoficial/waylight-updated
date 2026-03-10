package com.soradotwav.mixin.client;

import com.soradotwav.waylight.render.WaylightRenderHooks;
import net.fabricmc.fabric.api.client.rendering.v1.FabricRenderState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
abstract class LivingEntityRendererMixin {
	@Inject(method = "extractRenderState(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;F)V", at = @At("TAIL"))
	private void waylight$tagLocalPlayer(LivingEntity entity, LivingEntityRenderState renderState, float tickDelta, CallbackInfo ci) {
		LocalPlayer localPlayer = Minecraft.getInstance().player;
		((FabricRenderState) renderState).setData(WaylightRenderHooks.LOCAL_PLAYER_RENDER_STATE, entity == localPlayer);
	}
}
