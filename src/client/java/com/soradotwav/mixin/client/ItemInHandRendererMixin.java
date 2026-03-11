package com.soradotwav.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.soradotwav.WaylightClient;
import com.soradotwav.waylight.config.WaylightConfig;
import com.soradotwav.waylight.lantern.LanternType;
import com.soradotwav.waylight.lantern.LanternPosition;
import com.soradotwav.waylight.lantern.VirtualLanternState;
import com.soradotwav.waylight.render.FirstPersonHandMotionMode;
import com.soradotwav.waylight.render.LanternRig;
import com.soradotwav.waylight.render.LanternViewProjector;
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
	private static final LanternViewProjector VIEW_PROJECTOR = new LanternViewProjector();

	@Inject(
		method = "renderHandsWithItems",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/renderer/feature/FeatureRenderDispatcher;renderAllFeatures()V"
		)
	)
	private void waylight$renderHandLantern(float tickDelta, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, LocalPlayer localPlayer, int packedLight, CallbackInfo ci) {
		VirtualLanternState state = WaylightClient.LANTERN_CONTROLLER.getState();
		if (state.lanternPosition() != LanternPosition.LEFT_HAND || !state.modelVisible() || state.temporarilySuppressed()) {
			return;
		}

		BlockState lanternBlockState = state.lanternType() == LanternType.SOUL
			? Blocks.SOUL_LANTERN.defaultBlockState()
			: Blocks.LANTERN.defaultBlockState();
		WaylightConfig config = WaylightClient.CONFIG_MANAGER.get();
		LanternRig rig = WaylightClient.RIG_RESOLVER.resolveThirdPerson(
			state,
			WaylightClient.POSE_CONTROLLER.getPoseState(),
			config
		);
		FirstPersonHandMotionMode motionMode = FirstPersonHandMotionMode.fromConfig(config.firstPersonHandMotion);
		LanternViewProjector.FirstPersonProjection projection = VIEW_PROJECTOR.projectHandLeft(rig, motionMode);

		poseStack.pushPose();
		poseStack.translate(projection.translateX(), projection.translateY(), projection.translateZ());
		poseStack.mulPose(Axis.YP.rotationDegrees(projection.rotateY()));
		poseStack.mulPose(Axis.XP.rotationDegrees(projection.rotateX()));
		poseStack.mulPose(Axis.ZP.rotationDegrees(projection.rotateZ()));
		poseStack.scale(projection.scale(), projection.scale(), projection.scale());
		poseStack.translate(-0.5F, -0.65F, -0.5F);
		submitNodeCollector.submitBlock(poseStack, lanternBlockState, packedLight, OverlayTexture.NO_OVERLAY, 0);
		poseStack.popPose();
	}
}
