package com.soradotwav.mixin.client;

import com.soradotwav.WaylightClient;
import com.soradotwav.waylight.lantern.LanternPosition;
import com.soradotwav.waylight.lantern.VirtualLanternState;
import com.soradotwav.waylight.render.WaylightRenderHooks;
import net.fabricmc.fabric.api.client.rendering.v1.FabricRenderState;
import net.fabricmc.loader.api.FabricLoader;
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
    private static final boolean WAYLIGHT$EMF_PRESENT =
            FabricLoader.getInstance().isModLoaded("entity_model_features");

    PlayerModelMixin(ModelPart root) {
        super(root);
    }

    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;)V", at = @At("TAIL"))
    private void waylight$raiseLeftArmForLantern(AvatarRenderState renderState, CallbackInfo ci) {
        /*
         * v7 compatibility rule:
         *
         * EMF/Fresh Animations owns the third-person player arm transforms.
         * Writing directly to PlayerModel.leftArm after setupAnim creates a
         * split rig: the visible CEM arm uses FA's transform while Waylight's
         * attachment uses the vanilla ModelPart transform.  That is what made
         * the lantern float around shoulder height in FA Player.
         *
         * With EMF installed we therefore leave the arm completely untouched;
         * WaylightPlayerRenderFeature uses a body-relative compatibility anchor
         * for the lantern instead. First-person rendering is unaffected.
         */
        if (WAYLIGHT$EMF_PRESENT) {
            return;
        }

        if (!(renderState instanceof FabricRenderState fabricRenderState)
                || !Boolean.TRUE.equals(fabricRenderState.getData(WaylightRenderHooks.LOCAL_PLAYER_RENDER_STATE))) {
            return;
        }

        VirtualLanternState state = WaylightClient.runtime().lanternController().getState();
        if (state.lanternPosition() != LanternPosition.LEFT_HAND
                || !state.enabled()
                || !state.modelVisible()
                || state.temporarilySuppressed()) {
            return;
        }

        leftArm.xRot = (float) Math.toRadians(-130.0F);
        leftArm.yRot = (float) Math.toRadians(10.0F);
        leftArm.zRot = (float) Math.toRadians(-6.0F);
    }
}
