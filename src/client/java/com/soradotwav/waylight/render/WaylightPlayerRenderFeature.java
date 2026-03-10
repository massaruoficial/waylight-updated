package com.soradotwav.waylight.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.soradotwav.WaylightClient;
import com.soradotwav.waylight.config.WaylightConfig;
import com.soradotwav.waylight.lantern.LanternType;
import com.soradotwav.waylight.lantern.VirtualLanternState;
import net.fabricmc.fabric.api.client.rendering.v1.FabricRenderState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;

public final class WaylightPlayerRenderFeature extends RenderLayer<AvatarRenderState, PlayerModel> {
	private static final boolean ALWAYS_VISIBLE = true;

	public WaylightPlayerRenderFeature(RenderLayerParent<AvatarRenderState, PlayerModel> renderer) {
		super(renderer);
	}

	@Override
	public void submit(@NonNull PoseStack poseStack, @NonNull SubmitNodeCollector submitNodeCollector, int packedLight, AvatarRenderState avatarRenderState, float limbAngle, float limbDistance) {
		if (!ALWAYS_VISIBLE && (!(avatarRenderState instanceof FabricRenderState fabricRenderState) || !Boolean.TRUE.equals(fabricRenderState.getData(WaylightRenderHooks.LOCAL_PLAYER_RENDER_STATE)))) {
			return;
		}

		VirtualLanternState lanternState = WaylightClient.LANTERN_CONTROLLER.getState();
		if (!lanternState.modelVisible()) {
			return;
		}

		if (Minecraft.getInstance().player == null) {
			return;
		}

		BlockState lanternBlockState = lanternState.lanternType() == LanternType.SOUL
			? Blocks.SOUL_LANTERN.defaultBlockState()
			: Blocks.LANTERN.defaultBlockState();

		LanternPoseState poseState = WaylightClient.POSE_CONTROLLER.getPoseState();
		WaylightConfig config = WaylightClient.CONFIG_MANAGER.get();

		poseStack.pushPose();
		getParentModel().body.translateAndRotate(poseStack);
		poseStack.translate(-0.2F, 0.60F + poseState.bob(1.0F) * 0.03F, -0.15F);
		poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(15.0F + poseState.rollAngle(1.0F)));
		poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(165.0F + poseState.pitchAngle(1.0F)));
		poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(-35.0F + poseState.yawLag(1.0F)));
		if (config.debugAnchorGizmo) {
			submitAnchorGizmo(submitNodeCollector, poseStack);
		}
		poseStack.scale(0.36F, 0.36F, 0.36F);
		poseStack.translate(-0.5F, -0.65F, -0.5F);
		submitNodeCollector.submitBlock(poseStack, lanternBlockState, packedLight, OverlayTexture.NO_OVERLAY, avatarRenderState.outlineColor);
		poseStack.popPose();
	}

	private static void submitAnchorGizmo(SubmitNodeCollector submitNodeCollector, PoseStack poseStack) {
		submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.debugQuads(), (pose, vertexConsumer) -> {
			addAxisQuad(pose, vertexConsumer, 0.22F, 0.0F, 0.0F, 255, 48, 48);
			addAxisQuad(pose, vertexConsumer, 0.0F, 0.22F, 0.0F, 48, 255, 48);
			addAxisQuad(pose, vertexConsumer, 0.0F, 0.0F, 0.22F, 64, 160, 255);
		});
	}

	private static void addAxisQuad(PoseStack.Pose pose, com.mojang.blaze3d.vertex.VertexConsumer vertexConsumer, float axisX, float axisY, float axisZ, int red, int green, int blue) {
		final float thickness = 0.01F;

		if (axisX != 0.0F) {
			addQuad(pose, vertexConsumer, 0.0F, -thickness, -thickness, axisX, thickness, thickness, red, green, blue);
		} else if (axisY != 0.0F) {
			addQuad(pose, vertexConsumer, -thickness, 0.0F, -thickness, thickness, axisY, thickness, red, green, blue);
		} else {
			addQuad(pose, vertexConsumer, -thickness, -thickness, 0.0F, thickness, thickness, axisZ, red, green, blue);
		}
	}

	private static void addQuad(PoseStack.Pose pose, com.mojang.blaze3d.vertex.VertexConsumer vertexConsumer, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, int red, int green, int blue) {
		vertexConsumer.addVertex(pose, minX, minY, minZ).setColor(red, green, blue, 255).setNormal(pose, 0.0F, 1.0F, 0.0F);
		vertexConsumer.addVertex(pose, minX, maxY, minZ).setColor(red, green, blue, 255).setNormal(pose, 0.0F, 1.0F, 0.0F);
		vertexConsumer.addVertex(pose, maxX, maxY, maxZ).setColor(red, green, blue, 255).setNormal(pose, 0.0F, 1.0F, 0.0F);
		vertexConsumer.addVertex(pose, maxX, minY, maxZ).setColor(red, green, blue, 255).setNormal(pose, 0.0F, 1.0F, 0.0F);
	}
}
