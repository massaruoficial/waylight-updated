package com.soradotwav.mixin.client;

import com.soradotwav.WaylightClient;
import com.soradotwav.waylight.lantern.PoseMode;
import com.soradotwav.waylight.lantern.VirtualLanternState;
import com.soradotwav.waylight.render.WaylightRenderHooks;
import net.fabricmc.fabric.api.client.rendering.v1.FabricRenderState;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerModel.class)
abstract class PlayerModelMixin extends HumanoidModel<AvatarRenderState> {
	PlayerModelMixin(ModelPart root) {
		super(root);
	}

	@Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;)V", at = @At("TAIL"))
	private void waylight$raiseLeftArmForLantern(AvatarRenderState renderState, CallbackInfo ci) {
		if (!(renderState instanceof FabricRenderState fabricRenderState)
			|| !Boolean.TRUE.equals(fabricRenderState.getData(WaylightRenderHooks.LOCAL_PLAYER_RENDER_STATE))) {
			return;
		}

		VirtualLanternState state = WaylightClient.LANTERN_CONTROLLER.getState();
		if (state.poseMode() != PoseMode.HAND_LEFT || state.temporarilySuppressed()) {
			return;
		}

		leftArm.xRot = (float) Math.toRadians(-130.0F);
		leftArm.yRot = (float) Math.toRadians(10.0F);
		leftArm.zRot = (float) Math.toRadians(-6.0F);
	}
}
