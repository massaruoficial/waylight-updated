package com.soradotwav.waylight.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.soradotwav.WaylightClient;
import com.soradotwav.waylight.lantern.VirtualLanternState;
import net.fabricmc.fabric.api.client.rendering.v1.FabricRenderState;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;

public final class WaylightPlayerRenderFeature extends RenderLayer<AvatarRenderState, PlayerModel> {
    private static final boolean EMF_PRESENT =
            FabricLoader.getInstance().isModLoaded("entity_model_features");

    /*
     * v9: EMF/Fresh Animations compatibility no longer guesses the arm swing.
     * EmfModelPartRootMixin captures EMF's final left_arm PartPose after the
     * resource-pack animation has been evaluated. The lantern is attached to
     * that exact transform, so it cannot drift independently from the arm.
     */

    // v10 EMF/Fresh Animations palm attachment. ModelPart coordinates are in
    // player-model space after left_arm.translateAndRotate(). A vanilla/Fresh
    // arm is 12 model pixels long (= 0.75 world units), so Y=0.75 is the end
    // of the arm. The small X/Z offsets put the lantern handle in the center
    // of the palm instead of beside/inside the forearm.
    private static final float EMF_PALM_X = 0.00F;
    private static final float EMF_PALM_Y = 0.64F;
    private static final float EMF_PALM_Z = -0.02F;

    // Base rotations baked into LanternRigResolver#resolveHandLeft. They are
    // useful for the original renderer, but applying them on top of FA's
    // already-animated arm twists the lantern away from the palm. v10 keeps
    // only the dynamic physics deltas in the EMF path.
    private static final float HAND_BASE_ROT_X = -50.0F;
    private static final float HAND_BASE_ROT_Y = -10.0F;
    private static final float HAND_BASE_ROT_Z = 0.0F;

    private final BlockModelResolver blockModelResolver;

    public WaylightPlayerRenderFeature(
            RenderLayerParent<AvatarRenderState, PlayerModel> renderer,
            BlockModelResolver blockModelResolver) {
        super(renderer);
        this.blockModelResolver = blockModelResolver;
    }

    @Override
    public void submit(
            @NonNull PoseStack poseStack,
            @NonNull SubmitNodeCollector submitNodeCollector,
            int packedLight,
            AvatarRenderState avatarRenderState,
            float limbAngle,
            float limbDistance) {
        if (!(avatarRenderState instanceof FabricRenderState fabricRenderState)
                || !Boolean.TRUE.equals(fabricRenderState.getData(WaylightRenderHooks.LOCAL_PLAYER_RENDER_STATE))) {
            return;
        }

        VirtualLanternState lanternState =
                WaylightClient.runtime().lanternController().getState();
        if (!lanternState.modelVisible()) {
            return;
        }

        if (Minecraft.getInstance().player == null) {
            return;
        }

        BlockState lanternBlockState =
                WaylightClient.runtime().rigResolver().lanternBlockState(lanternState.lanternType());
        LanternPoseController.PoseState poseState =
                WaylightClient.runtime().poseController().getPoseState();
        LanternRigResolver.Transform transform =
                WaylightClient.runtime().rigResolver().resolveThirdPerson(lanternState, poseState);

        poseStack.pushPose();
        applyTransform(poseStack, transform, limbAngle, limbDistance);

        if (WaylightClient.runtime().configManager().get().debugAnchorGizmo) {
            submitAnchorGizmo(submitNodeCollector, poseStack);
        }

        poseStack.scale(0.46F, 0.46F, 0.46F);
        poseStack.translate(-0.5F, -0.65F, -0.5F);

        WaylightBlockRenderer.submit(
                blockModelResolver, poseStack, submitNodeCollector, lanternBlockState, packedLight, OverlayTexture.NO_OVERLAY, avatarRenderState.outlineColor);

        poseStack.popPose();
    }

    private void applyTransform(
            PoseStack poseStack,
            LanternRigResolver.Transform transform,
            float limbAngle,
            float limbDistance) {
        if (transform.attachment() == LanternRigResolver.Attachment.LEFT_ARM && EMF_PRESENT) {
            var emfPose = EmfPlayerPoseBridge.leftArmPose();
            if (emfPose != null) {
                // Reproduce ModelPart#translateAndRotate from EMF's *actual*
                // animated left arm. A throwaway empty ModelPart lets Minecraft
                // apply the exact translation/rotation/scale order for 26.2.
                net.minecraft.client.model.geom.ModelPart emfArm =
                        new net.minecraft.client.model.geom.ModelPart(java.util.List.of(), java.util.Map.of());
                emfArm.loadPose(emfPose);
                emfArm.translateAndRotate(poseStack);

                // v13: raise the handle farther into the animated palm. In the EMF arm
                // coordinate system +Y runs from shoulder toward the fingertips, so
                // reducing the Y offset moves the lantern upward toward the hand.
                // 0.64 keeps the handle inside the palm while preserving the v11
                // orientation fix and the v9 EMF arm-following transform.
                poseStack.translate(EMF_PALM_X, EMF_PALM_Y, EMF_PALM_Z);

                // v11: EMF's animated arm coordinate space arrives inverted for
                // the lantern model at the palm. Flip the lantern 180 degrees
                // around its local X axis so the handle stays at the hand and
                // the lantern body hangs downward instead of appearing upside down.
                // This is applied before the live swing physics, so the existing
                // inertia still pivots naturally from the handle.
                poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(180.0F));

                // Fresh Animations already supplies the arm orientation. Do not
                // add Waylight's old -50/-10 degree base pose again; that was the
                // reason v9 looked twisted beside the wrist. Preserve only the
                // live lantern inertia/physics on top of the animated hand.
                float physicsRoll = transform.rotateZ() - HAND_BASE_ROT_Z;
                float physicsPitch = transform.rotateX() - HAND_BASE_ROT_X;
                float physicsYaw = transform.rotateY() - HAND_BASE_ROT_Y;
                poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(physicsRoll));
                poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(physicsPitch));
                poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(physicsYaw));
                return;
            }
            // If EMF changed internally and no pose has been captured yet, use
            // the vanilla arm instead of a guessed independent animation.
        }

        if (transform.attachment() == LanternRigResolver.Attachment.LEFT_ARM) {
            getParentModel().leftArm.translateAndRotate(poseStack);
        } else {
            getParentModel().body.translateAndRotate(poseStack);
        }

        poseStack.translate(transform.translateX(), transform.translateY(), transform.translateZ());
        poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(transform.rotateZ()));
        poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(transform.rotateX()));
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(transform.rotateY()));
    }

    private static void submitAnchorGizmo(SubmitNodeCollector submitNodeCollector, PoseStack poseStack) {
        submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.debugQuads(), (pose, vertexConsumer) -> {
            addAxisQuad(pose, vertexConsumer, 0.22F, 0.0F, 0.0F, 255, 48, 48);
            addAxisQuad(pose, vertexConsumer, 0.0F, 0.22F, 0.0F, 48, 255, 48);
            addAxisQuad(pose, vertexConsumer, 0.0F, 0.0F, 0.22F, 64, 160, 255);
        });
    }

    private static void addAxisQuad(
            PoseStack.Pose pose,
            com.mojang.blaze3d.vertex.VertexConsumer vertexConsumer,
            float axisX,
            float axisY,
            float axisZ,
            int red,
            int green,
            int blue) {
        final float thickness = 0.01F;

        if (axisX != 0.0F) {
            addQuad(pose, vertexConsumer, 0.0F, -thickness, -thickness, axisX, thickness, thickness, red, green, blue);
        } else if (axisY != 0.0F) {
            addQuad(pose, vertexConsumer, -thickness, 0.0F, -thickness, thickness, axisY, thickness, red, green, blue);
        } else {
            addQuad(pose, vertexConsumer, -thickness, -thickness, 0.0F, thickness, thickness, axisZ, red, green, blue);
        }
    }

    private static void addQuad(
            PoseStack.Pose pose,
            com.mojang.blaze3d.vertex.VertexConsumer vertexConsumer,
            float minX,
            float minY,
            float minZ,
            float maxX,
            float maxY,
            float maxZ,
            int red,
            int green,
            int blue) {
        vertexConsumer
                .addVertex(pose, minX, minY, minZ)
                .setColor(red, green, blue, 255)
                .setNormal(pose, 0.0F, 1.0F, 0.0F);
        vertexConsumer
                .addVertex(pose, minX, maxY, minZ)
                .setColor(red, green, blue, 255)
                .setNormal(pose, 0.0F, 1.0F, 0.0F);
        vertexConsumer
                .addVertex(pose, maxX, maxY, maxZ)
                .setColor(red, green, blue, 255)
                .setNormal(pose, 0.0F, 1.0F, 0.0F);
        vertexConsumer
                .addVertex(pose, maxX, minY, maxZ)
                .setColor(red, green, blue, 255)
                .setNormal(pose, 0.0F, 1.0F, 0.0F);
    }
}
