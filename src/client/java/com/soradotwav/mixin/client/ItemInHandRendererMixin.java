package com.soradotwav.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.soradotwav.WaylightClient;
import com.soradotwav.waylight.lantern.LanternPosition;
import com.soradotwav.waylight.lantern.VirtualLanternState;
import com.soradotwav.waylight.render.FirstPersonHandMotionMode;
import com.soradotwav.waylight.render.LanternPoseController;
import com.soradotwav.waylight.render.LanternRigResolver;
import com.soradotwav.waylight.render.WaylightBlockRenderer;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Waylight's independent first-person lantern renderer.
 *
 * The lantern is attached to the same pose stack used by Minecraft's left arm,
 * but the placement/orientation below is Waylight-specific and does not copy a
 * third-party renderer. Punchy replaces this path when it is installed.
 */
@Mixin(ItemInHandRenderer.class)
abstract class ItemInHandRendererMixin {
    @Shadow @Final private EntityRenderDispatcher entityRenderDispatcher;

    @Shadow
    protected abstract void renderPlayerArm(
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            int packedLight,
            float inverseArmHeight,
            float attackValue,
            HumanoidArm arm);

    @Inject(method = "submitArmWithItem", at = @At("HEAD"), cancellable = true)
    private void waylight$renderLeftHandAndLantern(
            AbstractClientPlayer player,
            float tickDelta,
            float pitch,
            InteractionHand hand,
            float swingProgress,
            ItemStack itemStack,
            float equipProgress,
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            int packedLight,
            CallbackInfo ci) {
        if (hand != InteractionHand.OFF_HAND
                || Minecraft.getInstance().options.getCameraType() != CameraType.FIRST_PERSON) {
            return;
        }

        VirtualLanternState state = WaylightClient.runtime().lanternController().getState();
        if (!state.enabled()
                || state.lanternPosition() != LanternPosition.LEFT_HAND
                || !state.modelVisible()
                || state.temporarilySuppressed()) {
            return;
        }

        // Waylight owns only the visual pass. The player's actual offhand stack
        // is left untouched so gameplay and other mods keep seeing the real item.
        ci.cancel();

        LanternRigResolver rigResolver = WaylightClient.runtime().rigResolver();
        BlockState lanternBlockState = rigResolver.lanternBlockState(state.lanternType());
        LanternPoseController.PoseState physics = WaylightClient.runtime().poseController().getPoseState();

        poseStack.pushPose();

        // A small outward cant keeps the forearm readable while making room for
        // the lantern. These values were authored for Waylight's own rig.
        poseStack.translate(0.030F, 0.105F, -0.010F);
        poseStack.mulPose(Axis.ZP.rotationDegrees(-8.0F));

        this.renderPlayerArm(
                poseStack,
                submitNodeCollector,
                packedLight,
                equipProgress,
                swingProgress,
                HumanoidArm.LEFT);

        // Place the lantern slightly below and forward of the left palm.
        // The previous X quarter-turn rotated the lantern toward the camera,
        // making the broad face horizontal again.  What we need here is a
        // screen-plane quarter-turn: rotate around Z so the lantern body/handle
        // points upward while preserving the depth/facing that already worked.
        // Negative 90 degrees is intentional: +90 left the model upside-down.
        // Keep yaw almost neutral so the lantern reads straight-on instead of
        // appearing diagonally twisted away from the player hand.
        poseStack.translate(-0.315F, 0.135F, -0.455F);
        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.mulPose(Axis.ZP.rotationDegrees(-90.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees(6.0F));
        poseStack.translate(-0.5F, -0.5F, -0.5F);

        if (WaylightClient.runtime().configManager().get().firstPersonHandMotion
                != FirstPersonHandMotionMode.STATIC) {
            float pitchSwing = physics.pitchAngle(tickDelta) * 0.36F;
            float rollSwing = physics.rollAngle(tickDelta) * 0.50F;
            float yawSwing = physics.yawLag(tickDelta) * 0.31F;
            float bob = physics.bob(tickDelta) * 0.017F;

            poseStack.translate(0.0F, bob, 0.0F);

            // Pivot around the handle rather than the lantern's center.
            poseStack.translate(0.5F, 0.91F, 0.5F);
            poseStack.mulPose(Axis.YP.rotationDegrees(yawSwing));
            poseStack.mulPose(Axis.XP.rotationDegrees(pitchSwing));
            poseStack.mulPose(Axis.ZP.rotationDegrees(rollSwing));
            poseStack.translate(-0.5F, -0.91F, -0.5F);
        }

        WaylightBlockRenderer.submit(
                ((EntityRenderDispatcherAccessor) entityRenderDispatcher).waylight$getBlockModelResolver(),
                poseStack,
                submitNodeCollector,
                lanternBlockState,
                packedLight,
                OverlayTexture.NO_OVERLAY,
                0);

        poseStack.popPose();
    }
}
