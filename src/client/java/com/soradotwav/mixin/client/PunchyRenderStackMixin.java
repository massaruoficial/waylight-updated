package com.soradotwav.mixin.client;

import com.soradotwav.WaylightClient;
import com.soradotwav.waylight.lantern.LanternPosition;
import com.soradotwav.waylight.lantern.LanternType;
import com.soradotwav.waylight.lantern.VirtualLanternState;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "punchy.client.animation.PunchyAnimationManager", remap = false, priority = 1300)
public abstract class PunchyRenderStackMixin {
    @Inject(method = "resolveRenderStack", at = @At("HEAD"), cancellable = true, require = 0)
    private static void waylight$givePunchyVirtualLantern(
            Player player,
            HumanoidArm arm,
            ItemStack originalStack,
            CallbackInfoReturnable<ItemStack> cir) {
        if (player == null || arm == null || arm == player.getMainArm()) {
            return;
        }

        VirtualLanternState state = WaylightClient.runtime().lanternController().getState();
        if (!state.enabled()
                || !state.modelVisible()
                || state.temporarilySuppressed()
                || state.lanternPosition() != LanternPosition.LEFT_HAND) {
            return;
        }

        cir.setReturnValue((state.lanternType() == LanternType.SOUL
                ? Items.SOUL_LANTERN
                : Items.LANTERN).getDefaultInstance());
    }
}
