package com.soradotwav.mixin.client;

import com.soradotwav.WaylightClient;
import com.soradotwav.waylight.lantern.LanternPosition;
import com.soradotwav.waylight.lantern.VirtualLanternState;
import net.minecraft.world.InteractionHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "punchy.client.state.HandEquipStateMachine", remap = false, priority = 1400)
public abstract class PunchyVanillaHandOutMixin {
    @Inject(method = "shouldAllowVanillaHandOut", at = @At("HEAD"), cancellable = true, require = 0)
    private static void waylight$keepPunchyOffhandNative(
            InteractionHand hand,
            CallbackInfoReturnable<Boolean> cir) {
        if (hand != InteractionHand.OFF_HAND) {
            return;
        }

        try {
            VirtualLanternState state = WaylightClient.runtime().lanternController().getState();
            if (state.enabled()
                    && state.modelVisible()
                    && !state.temporarilySuppressed()
                    && state.lanternPosition() == LanternPosition.LEFT_HAND) {
                cir.setReturnValue(false);
            }
        } catch (Throwable ignored) {
        }
    }
}
