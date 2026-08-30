package com.soradotwav.waylight.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.world.level.block.state.BlockState;

/** Minecraft 26.2 block-model submission helper. */
public final class WaylightBlockRenderer {
    private static final BlockDisplayContext DISPLAY_CONTEXT = BlockDisplayContext.create();

    private WaylightBlockRenderer() {}

    public static void submit(
            BlockModelResolver blockModelResolver,
            PoseStack poseStack,
            SubmitNodeCollector collector,
            BlockState blockState,
            int packedLight,
            int packedOverlay,
            int outlineColor) {
        BlockModelRenderState renderState = new BlockModelRenderState();
        blockModelResolver.update(renderState, blockState, DISPLAY_CONTEXT);
        renderState.submit(poseStack, collector, packedLight, packedOverlay, outlineColor);
    }
}
