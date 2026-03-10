package com.soradotwav.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.soradotwav.WaylightClient;
import com.soradotwav.waylight.lantern.LanternType;
import com.soradotwav.waylight.lantern.PoseMode;
import com.soradotwav.waylight.lantern.VirtualLanternState;
import com.soradotwav.waylight.render.LanternPoseState;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
abstract class ItemInHandRendererMixin {
	@Inject(
		method = "renderHandsWithItems",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/renderer/feature/FeatureRenderDispatcher;renderAllFeatures()V"
		)
	)
	private void waylight$renderHandLantern(float tickDelta, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, LocalPlayer localPlayer, int packedLight, CallbackInfo ci) {
		VirtualLanternState state = WaylightClient.LANTERN_CONTROLLER.getState();
		if (state.poseMode() != PoseMode.HAND_LEFT || !state.modelVisible() || state.temporarilySuppressed()) {
			return;
		}

		BlockState lanternBlockState = state.lanternType() == LanternType.SOUL
			? Blocks.SOUL_LANTERN.defaultBlockState()
			: Blocks.LANTERN.defaultBlockState();
		LanternPoseState poseState = WaylightClient.POSE_CONTROLLER.getPoseState();

		poseStack.pushPose();
		poseStack.translate(-0.84F + poseState.yawLag(1.0F) * 0.0022F, 0.78F - poseState.pitchAngle(1.0F) * 0.0035F, -0.92F);
		poseStack.mulPose(Axis.YP.rotationDegrees(16.0F + poseState.yawLag(1.0F) * 0.12F));
		poseStack.mulPose(Axis.XP.rotationDegrees(8.0F + poseState.pitchAngle(1.0F) * 0.16F));
		poseStack.mulPose(Axis.ZP.rotationDegrees(-8.0F + poseState.rollAngle(1.0F) * 0.1F));
		poseStack.scale(1.08F, 1.08F, 1.08F);
		poseStack.translate(-0.5F, -0.65F, -0.5F);
		submitNodeCollector.submitBlock(poseStack, lanternBlockState, packedLight, OverlayTexture.NO_OVERLAY, 0);
		poseStack.popPose();
	}
}
