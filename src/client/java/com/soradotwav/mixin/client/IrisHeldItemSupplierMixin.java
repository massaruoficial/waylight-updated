package com.soradotwav.mixin.client;

import com.soradotwav.WaylightClient;
import com.soradotwav.waylight.lantern.LanternType;
import com.soradotwav.waylight.lantern.VirtualLanternState;
import net.minecraft.world.InteractionHand;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Waylight -> Iris shader bridge used by the v16.5 build.
 *
 * Eclipse maps minecraft:lantern to shader item ID 1015 and
 * minecraft:soul_lantern to shader item ID 1022.  By exposing those
 * IDs in Iris' off-hand HeldItemSupplier, FloodFill/LPV treats the
 * virtual Waylight as the matching vanilla lantern.
 */
@Pseudo
@Mixin(targets = "net.irisshaders.iris.uniforms.IdMapUniforms$HeldItemSupplier", remap = false)
abstract class IrisHeldItemSupplierMixin {
    private static final int WAYLIGHT_NORMAL_ITEM_ID = 1015;
    private static final int WAYLIGHT_SOUL_ITEM_ID = 1022;
    private static final int WAYLIGHT_LUMINANCE = 15;

    @Shadow @Final private InteractionHand hand;
    @Shadow private int intID;
    @Shadow private int lightValue;
    @Shadow private Vector3f lightColor;

    @Inject(method = "update", at = @At("TAIL"), require = 0)
    private void waylight$exposeVirtualLanternToShaders(CallbackInfo ci) {
        if (this.hand != InteractionHand.OFF_HAND) {
            return;
        }

        VirtualLanternState state = WaylightClient.runtime().lanternController().getState();
        if (!state.enabled()
                || !state.lightActive()
                || state.temporarilySuppressed()
                || state.underwaterExtinguished()) {
            return;
        }

        LanternType type = state.lanternType();
        this.intID = type == LanternType.SOUL ? WAYLIGHT_SOUL_ITEM_ID : WAYLIGHT_NORMAL_ITEM_ID;
        this.lightValue = WAYLIGHT_LUMINANCE;
        this.lightColor = type == LanternType.SOUL
                ? new Vector3f(0.5F, 0.7F, 1.0F)
                : new Vector3f(1.0F, 0.9F, 0.8F);
    }
}
